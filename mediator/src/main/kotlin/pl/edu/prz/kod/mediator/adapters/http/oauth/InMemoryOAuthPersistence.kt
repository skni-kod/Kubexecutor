package pl.edu.prz.kod.mediator.adapters.http.oauth

import com.auth0.jwt.JWT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Uri
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.invalidateCookie
import org.http4k.security.AccessToken
import org.http4k.security.CrossSiteRequestForgeryToken
import org.http4k.security.Nonce
import org.http4k.security.OAuthCallbackError
import org.http4k.security.OAuthPersistence
import org.http4k.security.openid.IdToken
import java.time.Clock
import java.time.Duration
import java.time.Instant

/**
 * This persistence handles both Bearer-token (API) and cookie-swapped access token (standard OAuth-web) flows.
 */
class InMemoryOAuthPersistence(private val clock: Clock, private val frontendHttpUrl: String) : OAuthPersistence {
    private val csrfName = "securityServerCsrf"
    private val originalUriName = "securityServerUri"
    private val clientAuthCookie = "securityServerAuth"
    private val jwtTokens = mutableSetOf<AccessToken>()

    private val jwts = Auth0Jwt("secret")

    override fun retrieveCsrf(request: Request) = request.cookie(csrfName)?.value?.let(::CrossSiteRequestForgeryToken)

    override fun retrieveNonce(request: Request): Nonce? = null
    override fun retrieveOriginalUri(request: Request): Uri? = Uri.of(frontendHttpUrl)

    override fun retrieveToken(request: Request) = (tryBearerToken(request)
        ?: tryCookieToken(request))
        ?.takeIf(jwts::verify)

    override fun assignCsrf(redirect: Response, csrf: CrossSiteRequestForgeryToken) =
        redirect.cookie(expiring(csrfName, csrf.value))

    override fun assignNonce(redirect: Response, nonce: Nonce): Response = redirect

    override fun assignOriginalUri(redirect: Response, originalUri: Uri): Response = redirect

    override fun assignToken(request: Request, redirect: Response, accessToken: AccessToken, idToken: IdToken?) =
        JWT.decode(idToken!!.value)
            .let {
                val claims = it.claims
                jwts.create(
                    subject = it.subject,
                    email = claims["email"]?.asString() ?: "NONE",
                    expiresAt = claims["exp"]?.asLong() ?: Instant.MIN.epochSecond
                )
            }.let {
                jwtTokens.add(it!!)
                redirect
                    .cookie(expiring(clientAuthCookie, it.value))
                    .invalidateCookie(csrfName)
                    .invalidateCookie(originalUriName)
            }

    override fun authFailureResponse(reason: OAuthCallbackError) = Response(FORBIDDEN)
        .invalidateCookie(csrfName)
        .invalidateCookie(originalUriName)
        .invalidateCookie(clientAuthCookie)

    private fun tryCookieToken(request: Request) =
        request.cookie(clientAuthCookie)?.value
            ?.let { AccessToken(it) }

    private fun tryBearerToken(request: Request) = request.header("Authorization")
        ?.removePrefix("Bearer ")
        ?.let { AccessToken(it) }

    private fun expiring(name: String, value: String) = Cookie(
        name, value,
        path = "/",
        expires = clock.instant().plus(Duration.ofDays(1))
    )
}