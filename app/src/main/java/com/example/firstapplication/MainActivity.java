package com.example.firstapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends AppCompatActivity {
    protected WebView mapView;

    //! Bu class Android ile Webview arasinda bağlantının sağlandığı yer.
    @SuppressLint("SetJavaScriptEnabled")
    public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public void markerclicked() {
            Log.e("WebAppInterface","markerclicked");
            // JavaScript tarafından gönderilen tıklama mesajını yakala ve bir toast mesajı olarak göster
            runOnUiThread(() -> Toast.makeText(mContext, "Marker'a tıklandı!", Toast.LENGTH_SHORT).show());
        }
    }

    protected void cmapView (WebView mapView, FloatingActionButton buttonTriggerJS){

        mapView.getSettings().setJavaScriptEnabled(true);
        mapView.getSettings().setDomStorageEnabled(true);
        mapView.loadUrl("file:///android_asset/leafletJS/map.html");

        mapView.addJavascriptInterface(new WebAppInterface(this), "Android");


        mapView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        buttonTriggerJS.setOnClickListener(v -> {
            // JavaScript'i tetiklemek için evaluateJavascript() kullanılır.
            mapView.evaluateJavascript("javascript:ucus();", null);
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);
        FloatingActionButton buttonTriggerJS = findViewById(R.id.mButton);

        cmapView(mapView,buttonTriggerJS);


    }
}
