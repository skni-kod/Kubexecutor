package pl.edu.prz.kod.mediator.adapters.http.oauth

import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.http4k.security.AccessToken
import java.time.Clock
import java.time.Instant
import java.util.*

interface Jwt {
    fun create(subject: String, email: String, expiresInSeconds: Long): AccessToken?
    fun verify(token: AccessToken): Boolean
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

    override fun verify(token: AccessToken): Boolean {
        try {
            verifier.verify(token.value)
            return true
        } catch (e: JWTVerificationException) {
            return false
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