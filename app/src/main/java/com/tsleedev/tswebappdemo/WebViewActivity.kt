package com.tsleedev.tswebappdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_web_view)

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportMultipleWindows(true)
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = MyWebChromeClient()

        // Add WebAppInterface to WebView
        webView.addJavascriptInterface(WebAppInterface(this, webView), "Android")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        webView.loadUrl("https://tswebviewhosting.web.app")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

private class MyWebChromeClient : WebChromeClient() {
    override fun onCreateWindow(
        view: WebView,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean {
        val currentUrl = view.url
        val newWebView = WebView(view.context)
        newWebView.settings.javaScriptEnabled = true
        newWebView.settings.setSupportMultipleWindows(true)
        newWebView.webViewClient = WebViewClient()
        newWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(newView: WebView?, url: String?): Boolean {
                if (url != null && currentUrl != null) {
                    val currentHost = Uri.parse(currentUrl).host
                    val newHost = Uri.parse(url).host
                    if (newHost != null && currentHost != null && newHost != currentHost) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        view.context.startActivity(intent)
                        return true
                    }
                }
                return false
            }
        }
        newWebView.webChromeClient = this

        newWebView.addJavascriptInterface(WebAppInterface(view.context, newWebView), "Android")

        // Add the new WebView to the parent view
        (view.parent as ViewGroup).addView(newWebView)
        val transport = resultMsg.obj as WebView.WebViewTransport
        transport.webView = newWebView
        resultMsg.sendToTarget()
        return true
    }

    override fun onCloseWindow(window: WebView) {
        // Remove the WebView from the parent view
        (window.parent as ViewGroup).removeView(window)
    }
}