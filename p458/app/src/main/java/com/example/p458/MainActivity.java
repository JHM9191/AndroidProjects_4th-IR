package com.example.p458;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        webView = findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new JS(),"js");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    class JS{
        @android.webkit.JavascriptInterface
        public void webclick(String str){
            textView.setText(str);
        }
    }

    public void ckbt(View v){
        if(v.getId() == R.id.button){
            webView.loadUrl("http://m.naver.com");
        }else if(v.getId() == R.id.button2){
            webView.loadUrl("http://70.12.113.219/webview");
        }else if(v.getId() == R.id.button3){
            webView.loadUrl("javascript:s('zzzzzz')");
        }
    }
}




