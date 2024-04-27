package com.example.tezuygulamasi;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final int GPS_ENABLE_REQUEST = 102;
    private LocationManager locationManager;

    protected WebView mapView;
    protected FloatingActionButton buttonTriggerJS;
    protected BottomSheetDialog dialog;
    protected BottomSheet bottomSheet;
    protected View view;
    protected DatabaseHelper data;
    protected String file="file:///android_asset/leafletJS/";
    private String strLocationLatitude, strLocationLongitude;
    private double dLocationLatitude, dLocationLongitude;


    // ---------------------------------- WEB APP INTERFACE ----------------------------------
    //! Bu class Android ile Webview arasinda bağlantının sağlandığı yer.
    @SuppressLint("SetJavaScriptEnabled")
    public class WebAppInterface {
        Context mContext;
        Dialog mDialog;
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
        String siraNo;
        TextView mbsTextView;
        WebView mWebView;

        WebAppInterface(Context context, Dialog dialog, WebView webView) {
            mContext = context;
            mDialog = dialog;
            mWebView = webView;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void markerClicked(String siraNo) {
            this.siraNo = siraNo;
            this.siraNo = getMarkerClicked();
        }

        public String getMarkerClicked(){
            bottomSheet = BottomSheet.newInstance(siraNo);
            bottomSheet.show(getSupportFragmentManager(), "BottomSheetDialogFragment");
            return siraNo;
        }

        @JavascriptInterface
        public void sendLongitude(String data) {
            String jsFunction = String.format("javascript: aLongitude = '%s';", data);
            Log.e("androidLongitude",data);
            mWebView.evaluateJavascript(jsFunction, null);
        }

        @JavascriptInterface
        public void sendLatitude(String data) {
            String jsFunction = String.format("javascript: aLatitude = '%s';", data);
            Log.e("androidLatitude",data);
            mWebView.evaluateJavascript(jsFunction, null);
        }

        @JavascriptInterface
        public void sendLocation(double latitude, double longitude) {
            mWebView.loadUrl("javascript:receiveLocation(" + latitude + "," + longitude + ")");
            Log.e("sendLocation","Latitude: "+latitude+", Longitude: "+longitude);
        }

        @JavascriptInterface
        public void showLog(String tag, String message) {
            // WebView'den gelen mesajı Log mesajı olarak göster
            Log.e("Webview "+tag,message);
        }
    }


// ---------------------------------- DATABASE ----------------------------------
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

// ---------------------------------- METODLAR ----------------------------------
    protected void cmapView (WebView mapView, FloatingActionButton buttonTriggerJS){
        mapView.getSettings().setJavaScriptEnabled(true);
        mapView.getSettings().setDomStorageEnabled(true);
        mapView.loadUrl(file+"index.html");
        //buttonTriggerJS.setOnClickListener(v -> mapView.evaluateJavascript("javascript:showAndroidToast();", null));
        buttonTriggerJS.setOnClickListener(v -> checkPermissionsAndStart());
    }

// ---------------------------------- ACTIVITY LIFE CYCLE ----------------------------------
    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);
        buttonTriggerJS = findViewById(R.id.mButton);

        cmapView(mapView,buttonTriggerJS);
        requestLocationUpdates();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        databaseHelper(dbHelper);

        dialog = new BottomSheetDialog(MainActivity.this,R.style.Theme_TezUygulamasi);
        mapView.addJavascriptInterface(new WebAppInterface(this,dialog,mapView), "Android");
    }

// ---------------------------------- GPS ----------------------------------
    private void requestLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = location -> {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mapView.loadUrl("javascript:receiveLocation(" + latitude + "," + longitude + ")");
        };

        try {
            // Konum güncellemelerini başlat
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 5F, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi
                checkLocationEnabled();
            } else {
                // İzin reddedildi, kullanıcıya açıklama yap ve tekrar izin iste
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                    // Kullanıcı izni reddetti ancak tekrar sorma seçeneği kapalı değil, açıklama göster ve tekrar izin iste
                    showRationaleDialog("Konum İzni Gerekli", "Bu uygulama, belirli özellikler için konum iznine ihtiyaç duymaktadır. Lütfen izin verin.");
                } else {
                    // Kullanıcı 'bir daha sorma' seçeneğini işaretledi, uygulamanın konum özelliğini kullanamayacağını bildir
                    showRationaleDialog("Konum İzni Gerekli", "Bu uygulama, belirli özellikler için konum iznine ihtiyaç duymaktadır. Uygulama ayarlarından konum izinlerini aktifleştirin.");
                }
            }
        }
    }

    private void showRationaleDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("İzin Ver", (dialog, which) -> {
                    // Kullanıcıyı ikna ettikten sonra izni tekrar iste
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                })
                .setNegativeButton("İptal", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @SuppressLint("SetTextI18n")
    public void onLocationChanged(Location location) {
        strLocationLatitude = String.valueOf(location.getLatitude());
        strLocationLongitude = String.valueOf(location.getLongitude());
        dLocationLatitude = location.getLatitude();
        dLocationLongitude = location.getLongitude();
        WebAppInterface webAppInterface = new WebAppInterface(this,dialog,mapView);
        webAppInterface.sendLatitude(strLocationLatitude);
        webAppInterface.sendLongitude(strLocationLongitude);
        mapView.loadUrl("javascript:receiveLocation(" + strLocationLatitude + "," + strLocationLongitude + ")");
        mapView.addJavascriptInterface(new WebAppInterface(this,dialog,mapView), "Android");
        try {
            // Konum güncellemelerini başlat
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 5F, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        Log.e("GPS","Latitude:"+dLocationLatitude+" Longitude:"+dLocationLongitude);
    }

    private void checkPermissionsAndStart() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // İzin iste
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // İzinler verilmiş, konum servislerinin açık olup olmadığını kontrol et
            checkLocationEnabled();
        }
    }

    private void checkLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Konum servisleri kapalı, kullanıcıyı ayarlara yönlendir
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, GPS_ENABLE_REQUEST);
        } else {
            // Konum servisleri açık, konum güncellemelerini başlat
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
