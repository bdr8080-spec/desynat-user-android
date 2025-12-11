package com.desynatuser.ui.home

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.desynatuser.R

class PaymentWebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webView = findViewById<WebView>(R.id.webView)
        val url = intent.getStringExtra("paymentUrl")

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // check success url or fail url
                if (url?.contains("success") == true) {
                    setResult(RESULT_OK)
                    finish()
                } else if (url?.contains("error") == true || url?.contains("fail") == true) {
                    setResult(RESULT_CANCELED)
                    finish()
                }
            }
        }

        webView.loadUrl(url ?: "")
    }
}
