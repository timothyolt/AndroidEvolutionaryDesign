package com.timothyolt.evolutionarydesign.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.timothyolt.evolutionarydesign.requireInjector

class AuthenticationActivity : AppCompatActivity() {

    interface Dependencies {
        val oAuthRequestUrl: String
        val oAuthCallbackUrl: String
        val navigateToMain: Intent
    }

    private lateinit var dependencies: Dependencies

    private var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencies = requireInjector().inject(this)
        textView = TextView(this)
        setContentView(textView)
    }

    override fun onResume() {
        super.onResume()
        val authentication = authentication(intent)
        if (authentication == null) {
            textView?.text = "Redirecting to Imgur..."
            val outgoingOAuthIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(dependencies.oAuthRequestUrl)
            }
            startActivity(outgoingOAuthIntent)
        } else {
            textView?.text = "Redirecting to App..."
            startActivity(dependencies.navigateToMain)
            finish()
        }
    }

    private fun authentication(intent: Intent): Authentication? {
        val expectedUri = Uri.parse(dependencies.oAuthCallbackUrl)
        val intentBaseUri = intent.data?.run {
            buildUpon()
                .fragment(null)
                .build()
        }
        return if (intentBaseUri != null && intentBaseUri == expectedUri) {
            val fragments = intent.data?.fragment?.split('&')?.associate {
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

    private data class Authentication(
        val accessToken: String,
        val tokenType: String?,
        val expiresIn: String?
    )

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val TOKEN_TYPE_KEY = "token_type"
        private const val EXPIRY_KEY = "expires_in"
    }
}
