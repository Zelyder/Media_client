package com.zelyder.mediaclient.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.zelyder.mediaclient.R
import com.zelyder.mediaclient.data.MEDIA_BASE_URL

class WebViewActivity : AppCompatActivity() {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view)

        webView = findViewById(R.id.mainWebView)

        val url = "${MEDIA_BASE_URL}1"

        webView?.let {
            it.loadUrl(url)
            it.webViewClient = WebViewClient()
            it.settings.useWideViewPort = true
            it.settings.loadWithOverviewMode = true
            it.settings.javaScriptEnabled = true
            it.webViewClient = object : WebViewClient() {
                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    it.loadUrl("javascript:(function(){var m=document.createElement('META'); m.name='viewport'; m.content='width=device-width, height=device-height, user-scalable=yes'; document.body.appendChild(m);})()")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    it.loadUrl("javascript:(function(){var m=document.createElement('META'); m.name='viewport'; m.content='width=device-width, height=device-height, user-scalable=yes'; document.body.appendChild(m);})()")
                }
            }
        }
    }
}