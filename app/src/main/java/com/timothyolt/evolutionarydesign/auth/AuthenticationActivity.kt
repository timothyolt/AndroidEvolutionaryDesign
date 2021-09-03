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
        val authentication: Authentication
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
            dependencies.authentication.state = authentication
            textView?.text = "Redirecting to App..."
            startActivity(dependencies.navigateToMain)
            finish()
        }
    }

    private fun authentication(intent: Intent): Authentication.State? {
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
                Authentication.State(
                    accessToken = accessToken,
                    tokenType = fragments["token_type"],
                    expiresIn = fragments["expires_in"]
                )
            } else null
        } else null
    }
}
