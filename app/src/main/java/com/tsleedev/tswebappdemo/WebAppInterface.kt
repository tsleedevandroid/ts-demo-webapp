package com.tsleedev.tswebappdemo

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import org.json.JSONObject

class WebAppInterface(private val context: Context, private val webView: WebView) {

    @JavascriptInterface
    fun screenEvent(response: String) {
        try {
            val jsonObject = JSONObject(response)
            val name = jsonObject.getString("name")
            val parameters = jsonObject.optJSONObject("parameters")

            var message = "name = $name"
            if (parameters != null) {
                message += ", parameters = $parameters"
            }

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("WebAppInterface", "Failed to parse response", e)
        }
    }

    @JavascriptInterface
    fun logEvent(response: String) {
        try {
            val jsonObject = JSONObject(response)
            val name = jsonObject.getString("name")
            val parameters = jsonObject.optJSONObject("parameters")

            var message = "name = $name"
            if (parameters != null) {
                message += ", parameters = $parameters"
            }

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("WebAppInterface", "Failed to parse response", e)
        }
    }

    @JavascriptInterface
    fun setUserProperty(response: String) {
        try {
            val jsonObject = JSONObject(response)
            val name = jsonObject.getString("name")
            val value = jsonObject.getString("value")

            val message = "name = $name, value = $value"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("WebAppInterface", "Failed to parse response", e)
        }
    }

    @JavascriptInterface
    fun openPhoneSettings(response: String) {
        try {
            val jsonObject = JSONObject(response)

            // Open phone settings
            val intent = Intent(Settings.ACTION_SETTINGS)
            context.startActivity(intent)

            // Execute JavaScript callback
            jsonObject.optString("callbackId").let { callbackId ->
                if (callbackId.isNotEmpty()) {
                    val script = "$callbackId();"
                    webView.post {
                        webView.evaluateJavascript(script, null)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("WebAppInterface", "Failed to open phone settings", e)
        }
    }
}