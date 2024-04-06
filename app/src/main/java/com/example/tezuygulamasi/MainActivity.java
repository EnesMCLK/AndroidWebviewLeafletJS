package com.example.tezuygulamasi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

        buttonTriggerJS.setOnClickListener(v -> mapView.evaluateJavascript("javascript:ucus();", null));
    }
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);
        FloatingActionButton buttonTriggerJS = findViewById(R.id.mButton);

        cmapView(mapView,buttonTriggerJS);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        // Veritabanını hafızaya kopyala.
        try {
            dbHelper.copyDatabase();
            Log.e("DATABASE","Veritabanı kopyalandı.");
        }catch (Exception e){
            e.printStackTrace();
        }

        // Veritabanından sorgu çalıştır.
        try {
            String Data = dbHelper.getData();
            Toast.makeText(this, Data, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
