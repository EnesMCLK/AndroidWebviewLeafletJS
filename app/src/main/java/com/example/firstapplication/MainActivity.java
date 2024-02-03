package com.example.firstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView mapView;

    public static class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (WebView) findViewById(R.id.mapview);
        Button buttonTriggerJS = findViewById(R.id.mButton);

        mapView.getSettings().setJavaScriptEnabled(true);
        mapView.getSettings().setDomStorageEnabled(true);
        mapView.loadUrl("file:///android_asset/leafletJS/map.html");

        mapView.addJavascriptInterface(new WebAppInterface(this), "Android");

        mapView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                if (url.toString().startsWith("popupclicked://")) {
                    // Burada Popup tıklandığında yapılacak işlemleri gerçekleştirin
                    Toast.makeText(getApplicationContext(), "Popup clicked!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        buttonTriggerJS.setOnClickListener(v -> {
            // JavaScript'i tetiklemek için evaluateJavascript() kullanılır.
            mapView.evaluateJavascript("javascript:ucus();", null);
        });
    }
}
