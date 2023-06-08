package pl.edu.prz.kod.mediator.adapters.http.oauth

import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.http4k.security.AccessToken
import pl.edu.prz.kod.mediator.adapters.http.TokenVerifiedEvent
import pl.edu.prz.kod.mediator.adapters.http.logEvent
import java.time.Instant
import java.util.*

interface Jwt {
    fun create(subject: String, email: String, expiresInSeconds: Long): AccessToken?
    fun verify(token: AccessToken): TokenVerificationResult
}

sealed class TokenVerificationResult {
    data class Success(val email: String) : TokenVerificationResult()
    object Failure : TokenVerificationResult()
}

class Auth0Jwt(private val secret: String) : Jwt {
    override fun create(subject: String, email: String, expiresAt: Long): AccessToken? {
        try {
            val algorithm = Algorithm.HMAC256(secret)
            val token = com.auth0.jwt.JWT
                .create()
                .withSubject(subject)
                .withClaim("email", email)
                .withExpiresAt(Date.from(Instant.ofEpochSecond(expiresAt)))
                .sign(algorithm)
            return AccessToken(
                value = token,
            )
        } catch (e: JWTCreationException) {
            return null
        }
    }

    override fun verify(token: AccessToken): TokenVerificationResult {
        try {
            val decoded = verifier.verify(token.value)
            val email = decoded.claims["email"]?.asString() ?: "NONE"
            logEvent(TokenVerifiedEvent(email))
            return TokenVerificationResult.Success(email)
        } catch (e: JWTVerificationException) {
            return TokenVerificationResult.Failure
        }
    }

    private val verifier by lazy {
        val algorithm = Algorithm.HMAC256(secret)
        com.auth0.jwt.JWT
            .require(algorithm)
            .acceptExpiresAt(60)
            .build()
    }
}