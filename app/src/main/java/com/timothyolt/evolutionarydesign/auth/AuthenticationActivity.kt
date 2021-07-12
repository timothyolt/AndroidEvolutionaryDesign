package com.timothyolt.evolutionarydesign.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.timothyolt.evolutionarydesign.MainActivity

class AuthenticationActivity : AppCompatActivity() {

    private var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textView = TextView(this)
        setContentView(textView)
    }

    override fun onResume() {
        super.onResume()
        val authentication = authentication(intent)
        if (authentication == null) {
            textView?.text = "Redirecting to Imgur..."
            val outgoingOAuthIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.imgur.com/oauth2/authorize?client_id=6b1112a4f9783ad&response_type=token")
            }
            startActivity(outgoingOAuthIntent)
        } else {
            textView?.text = "Redirecting to App..."
            // the good old anti-pattern
            startActivity(Intent(this, MainActivity::class.java))
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