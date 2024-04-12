package com.example.tezuygulamasi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends AppCompatActivity {
    protected WebView mapView;
    protected FloatingActionButton buttonTriggerJS;
    protected BottomSheetDialog dialog;
    protected BottomSheet bottomSheet;
    protected View view;
    public DatabaseHelper data;


    //! Bu class Android ile Webview arasinda bağlantının sağlandığı yer.
    @SuppressLint("SetJavaScriptEnabled")
    public class WebAppInterface {
        Context mContext;
        Dialog mdialog;
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
        String siraNo;
        TextView mbsTextView;

        WebAppInterface(Context context, Dialog dialog) {
            mContext = context;
            mdialog = dialog;
        }

        @JavascriptInterface
        public void markerClicked(String siraNo) {
            this.siraNo = siraNo;
            this.siraNo = getMarkerClicked();
        }
        public String getMarkerClicked(){
            bottomSheet = BottomSheet.newInstance(siraNo);
            bottomSheet.show(getSupportFragmentManager(), "BottomSheetDialogFragment");
            return this.siraNo;
        }
    }

    protected void databaseHelper(DatabaseHelper databaseHelper){
        // copyDatabase() metodu ile veritabanını hafızaya kopyala.
        try {
            databaseHelper.copyDatabase();
            Log.e("DATABASE","Veritabanı kopyalandı.");

        }catch (Exception e){
            e.printStackTrace();
            Log.e("DATABASE","Veritabanırken hata oluştu.");
        }
    }

    protected void cmapView (WebView mapView, FloatingActionButton buttonTriggerJS){
        mapView.getSettings().setJavaScriptEnabled(true);
        mapView.getSettings().setDomStorageEnabled(true);
        mapView.loadUrl("file:///android_asset/leafletJS/map.html");
        buttonTriggerJS.setOnClickListener(v -> mapView.evaluateJavascript("javascript:ucus();", null));
    }

    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);
        buttonTriggerJS = findViewById(R.id.mButton);
        cmapView(mapView,buttonTriggerJS);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        databaseHelper(dbHelper);

        dialog = new BottomSheetDialog(MainActivity.this,R.style.Theme_TezUygulamasi);
        mapView.addJavascriptInterface(new WebAppInterface(this,dialog), "Android");
    }
}
