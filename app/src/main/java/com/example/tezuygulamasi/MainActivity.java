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
    protected FloatingActionButton buttonTriggerJS;

    //! Bu class Android ile Webview arasinda bağlantının sağlandığı yer.
    @SuppressLint("SetJavaScriptEnabled")
    public class WebAppInterface {
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public  void markerclicked(String siraNo) {
            Log.e("WebAppInterface","markerclicked: "+siraNo);
            // JavaScript tarafından gönderilen tıklama mesajını yakala ve bir toast mesajı olarak göster
            runOnUiThread(() -> Toast.makeText(mContext, siraNo, Toast.LENGTH_SHORT).show());
        }
    }

    protected void databaseHelper(DatabaseHelper databaseHelper){
        // Veritabanını hafızaya kopyala.
        try {
            databaseHelper.copyDatabase();
            Log.e("DATABASE","Veritabanı kopyalandı.");
        }catch (Exception e){
            e.printStackTrace();
            Log.e("DATABASE","Veritabanırken hata oluştu.");
        }

        // Veritabanından sorgu çalıştır.
        try {
            String Data = databaseHelper.getData();
            Toast.makeText(this, Data, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
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
        buttonTriggerJS = findViewById(R.id.mButton);

        cmapView(mapView,buttonTriggerJS);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        databaseHelper(dbHelper);

    }
}
