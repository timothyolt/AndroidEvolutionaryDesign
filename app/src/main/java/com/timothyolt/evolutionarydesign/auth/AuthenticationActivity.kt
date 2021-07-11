package com.timothyolt.evolutionarydesign.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LinearLayout(this))
        val authentication = authentication(intent)
        if (authentication == null) {
            val outgoingOAuthIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.imgur.com/oauth2/authorize?client_id=6b1112a4f9783ad&response_type=token")
            }
            startActivity(outgoingOAuthIntent)
        } else {
            Toast.makeText(this, authentication.accessToken, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun authentication(intent: Intent): Authentication? {
        val uri = intent.data
        return if (
            uri != null
            && uri.host == "timothyolt.com"
            && uri.path == "/android-evolutionary-design/login"
        ) {
            val fragments = uri.fragment?.split('&')?.associate {
                val (first, second) = it.split('=')
                first to second
            } ?: emptyMap()
            val accessToken = fragments["access_token"]
            if (accessToken != null) {
                Authentication(
                    accessToken = accessToken,
                    tokenType = fragments["token_type"],
                    expiresIn = fragments["expires_in"]
                )
            } else null
        } else null
    }

    data class Authentication(
        val accessToken: String,
        val tokenType: String?,
        val expiresIn: String?
    )
}