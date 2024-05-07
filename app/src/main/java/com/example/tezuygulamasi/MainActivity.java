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
    private static LocationManager locationManager;

    @SuppressLint("StaticFieldLeak")
    protected static WebView mapView;
    protected FloatingActionButton buttonTriggerJS, meTriggerJS, shortRoute;
    private boolean isExecuted = false; // İlk çalıştırma kontrolü
    private boolean isFirstTimeLocationAsking = true;  // İlk izin isteği kontrolü
    protected BottomSheetDialog dialog;
    protected BottomSheet bottomSheet;
    protected View view;
    protected DatabaseHelper data;
    protected String file="file:///android_asset/leafletJS/";
    private String strLocationLatitude, strLocationLongitude;
    private double dLocationLatitude, dLocationLongitude;
    private DatabaseHelper dbhelper;
    private WebAppInterface webAppInterface;


    // ---------------------------------- WEB APP INTERFACE ----------------------------------
    //! Bu class Android ile Webview arasinda bağlantının sağlandığı yer.
    @SuppressLint("SetJavaScriptEnabled")
    public class WebAppInterface {
        Context mContext;
        Dialog mDialog;
        DatabaseHelper dbHelper;
        String siraNo;
        TextView mbsTextView;
        WebView mWebView;

        WebAppInterface(Context context, Dialog dialog, WebView webView) {
            mContext = context;
            mDialog = dialog;
            mWebView = webView;
            dbHelper = new DatabaseHelper(context.getApplicationContext());
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
        public void sendLocation(double latitude, double longitude) {
            mWebView.loadUrl("javascript:receiveLocation(" + latitude + "," + longitude + ")");
        }
        @JavascriptInterface
        public void showUserLocation(double latitude, double longitude) {
            // Webview'de kullanıcının konumunu yakaladığı takdirde göster
            if (latitude>0 && longitude>0) {
                mWebView.loadUrl("javascript:showUserLocation(" + latitude + "," + longitude + ")");
            }
        }
        @JavascriptInterface
        public void getShowLocation(double latitude, double longitude) {
            // Webview'de kullanıcının konumunu getir ve göster
            mWebView.loadUrl("javascript:getShowLocation(" + latitude + "," + longitude + ")");
        }
        @JavascriptInterface
        public void findClosestMarker() {
            // Kullanıcının konumuna en yakın istasyonu bul
            mWebView.loadUrl("javascript:findClosestMarker()");
        }
        @JavascriptInterface
        public void showToast(String toast) {
            // WebView'den gelen Toast mesajını activityde göster
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void showLog(String tag, String message) {
            // WebView'den gelen mesajı Log mesajı olarak göster
            Log.e("Webview "+tag,message);
        }
    }

// ---------------------------------- DATABASE ----------------------------------
    protected void databaseHelper(DatabaseHelper databaseHelper){
        // copyDatabase() fonksiyonu ile veritabanını hafızaya kopyala ve hafıza üzerinden çalıştır
        try {
            databaseHelper.copyDatabase();
            Log.e("DATABASE","Veritabanı kopyalandı.");

        }catch (Exception e){
            e.printStackTrace();
            Log.e("DATABASE","Veritabanırken hata oluştu.");
        }
    }

// ---------------------------------- METODLAR ----------------------------------
    protected void cMapView (WebView mapView, FloatingActionButton buttonTriggerJS){
        mapView.getSettings().setJavaScriptEnabled(true);
        mapView.getSettings().setDomStorageEnabled(true);
        mapView.loadUrl(file+"index.html");
        buttonTriggerJS.setOnClickListener(v -> {
            // Konum tuşuna tıklanıldığında izinleri kontrol et ve başlat
            checkPermissionsAndStart();
            if (dLocationLatitude>0 && dLocationLongitude>0){   // Konum bilgilerine ulaşıldığı takdirde
                // Webview'e gönder ve kullanıcının konumunu güncelle ve eski konuma ait kullanıcıları temizle
                mapView.loadUrl("javascript:receiveLocation(" + dLocationLatitude + "," + dLocationLongitude + ")");
            }
        });
        meTriggerJS.setOnClickListener(v -> {
            // İmge tuşuna tıklanıldığında izinleri kontrol et ve başlat
            checkPermissionsAndStart();
            // Konum bilgilerine ulaşıldığı takdirde
            if (dLocationLatitude>0 && dLocationLongitude>0){
                // Webview'de kullanıcının konumunu getir ve göster
                mapView.loadUrl("javascript:getShowLocation(" + dLocationLatitude + "," + dLocationLongitude + ")");
            }
        });
        shortRoute.setOnClickListener(v -> {
            // Konum bilgilerine ulaşıldığı takdirde
            if (dLocationLatitude>0 && dLocationLongitude>0){
                // Webview' de en kısa yol fonksiyonunu çalıstır
                mapView.loadUrl("javascript:findClosestMarker()");
            }
        });
    }
    protected void runOnceShowUserLocation() {
        // Çalışmışsa çalıştırma çünkü bu metod yalnızca bir kere çalışır
        if (!isExecuted) {
            webAppInterface.showUserLocation(dLocationLatitude,dLocationLongitude);
            Toast.makeText(this,"Konum bilgisine erişildi ve konum gösterildi",Toast.LENGTH_SHORT).show();
            isExecuted = true;  // Metodu çalıştı olarak işaretle
            meTriggerJS.show(); // İmge tuşunu görünür yap
            shortRoute.show();  // En kısa yol tuşunu görünür yap
        }
    }

// ---------------------------------- ACTIVITY LIFE CYCLE ----------------------------------
    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);
        buttonTriggerJS = findViewById(R.id.mButton);
        meTriggerJS = findViewById(R.id.mMe); meTriggerJS.hide();
        shortRoute = findViewById(R.id.mShortRoute); shortRoute.hide();

        cMapView(mapView,buttonTriggerJS);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocationUpdates();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        databaseHelper(dbHelper);

        dialog = new BottomSheetDialog(this);
        mapView.addJavascriptInterface(new WebAppInterface(this,dialog,mapView), "Android");
    }

    // ---------------------------------- GPS ----------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi
                checkLocationEnabled();
            } else {
                // İzin reddedildi, kullanıcıya açıklama yap ve tekrar izin iste
                if (!isFirstTimeLocationAsking) {
                    isFirstTimeLocationAsking = false;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                        showRationaleDialog("Konum İzni Gerekli", "Bu uygulama, belirli özellikler için konum iznine ihtiyaç duymaktadır. Lütfen izin verin.");
                    }
                } else {
                    // İzin ikinci kez reddedildi, işlemi durdur
                    Toast.makeText(this, "Konum izni reddedildi, uygulama sınırlı işlevsellikle çalışacak.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void showRationaleDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("İzin Ver", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE))
                .setNegativeButton("Reddet", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
    @SuppressLint("SetTextI18n")
    public void onLocationChanged(Location location) {
        strLocationLatitude = String.valueOf(location.getLatitude());
        strLocationLongitude = String.valueOf(location.getLongitude());
        dLocationLatitude = location.getLatitude();
        dLocationLongitude = location.getLongitude();

        // Kullanıcıya konum bilgisi gönderme
        webAppInterface = new WebAppInterface(this,dialog,mapView);
        mapView.loadUrl("javascript:receiveLocation(" + strLocationLatitude + "," + strLocationLongitude + ")");

        if (dLocationLatitude>0 && dLocationLongitude>0){ runOnceShowUserLocation(); }
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
