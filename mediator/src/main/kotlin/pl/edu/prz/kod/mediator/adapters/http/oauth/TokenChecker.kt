package pl.edu.prz.kod.mediator.adapters.http.oauth

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.http4k.security.AccessToken

interface TokenChecker {
    fun check(accessToken: AccessToken): Boolean
}

class GoogleTokenChecker(googleClientId: String) : TokenChecker {

    val transport = NetHttpTransport()
    val jsonFactory = GsonFactory()
    val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        .setAudience(listOf(googleClientId))
        .build()

    override fun check(accessToken: AccessToken): Boolean {
        val idToken = verifier.verify(accessToken.value)

        return idToken != null
    }

}