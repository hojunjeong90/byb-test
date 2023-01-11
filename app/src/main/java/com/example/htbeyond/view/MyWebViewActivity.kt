package com.example.htbeyond.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.htbeyond.R
import com.example.htbeyond.util.MyWebAppInterface
import com.example.htbeyond.util.MyWebChromeClient
import com.example.htbeyond.util.MyWebViewClient

class MyWebViewActivity : AppCompatActivity() {

    val webView: WebView by lazy { findViewById(R.id.webView) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_web_view)
        webView.apply {
            /**
             * WebView Setting
             *
             * WebSettings : 자바스크립트 사용 설정
             * WebViewClient : 콘텐츠 랜더링에 영향을 미치는 이벤트 처리. URL 로드의 시작, 중간, 끝의 콜백 처리. 이때 데이터도 전달할 수 있습니다.
             * WebChromeClient : 전체 화면 설정, 새로운 창을 만들거나 닫는 것과 관련된 호스트 앱의 UI 변경 제어
             */
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                userAgentString = "Android-User-Agent-String"
            }
            webChromeClient = MyWebChromeClient()
            webViewClient = MyWebViewClient()
            /**
             * Web to App
             * addJavascriptInterface : JavaScript 에서 실행할 수 있는 안드로이드 작업을 추가 {name.functionName} 로 사용.
             */
            addJavascriptInterface(MyWebAppInterface(this@MyWebViewActivity), "android")
            /**
             * TMI
             * setDownloadListener : File Download 구성에 쓴다.
             */
            // setDownloadListener()
        }.also {
            /**
             * Cookie로 전달
             */
            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(webView, true)
                setCookie("ROOT_URL", "authorization=JWT-TOKEN; httpOnly")
            }.run {
                flush()
            }
        }.run {
            /**
             * Header로 전달
             */
            val additionalHttpHeaders = HashMap<String, String>()
            loadUrl("file:///android_asset/test.html", additionalHttpHeaders)
        }
        /**
         * App to Web
         * loadUrl : {javascript:filename.method} 방식으로 직접 호출
         */
        // val num = 1
        // webView.loadUrl("javascript:test_script.calculateNum(\"$num\")")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        /**
         * WebView 뒤로가기 기능 지원
         */
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}