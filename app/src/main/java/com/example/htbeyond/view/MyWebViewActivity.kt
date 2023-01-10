package com.example.htbeyond.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.htbeyond.R
import com.example.htbeyond.util.MyWebViewClient

class MyWebViewActivity : AppCompatActivity() {

    val webView: WebView by lazy { findViewById(R.id.webView) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_web_view)


        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }
            webViewClient = MyWebViewClient()
        }.run {
            loadUrl("https://www.naver.com")
        }
    }
}