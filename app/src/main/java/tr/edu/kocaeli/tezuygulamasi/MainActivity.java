package tr.edu.kocaeli.tezuygulamasi;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
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

// ---------------------------------- MAINACTIVITY YAPICI SINIFI ----------------------------------
@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final int GPS_ENABLE_REQUEST = 102;

    private static LocationManager locationManager;
    @SuppressLint("StaticFieldLeak")
    protected static WebView mapView;
    protected View view;
    protected FloatingActionButton buttonTriggerJS, meTriggerJS, shortRoute, clearRoute;
    private boolean isExecuted = false; // İlk çalıştırma kontrolü
    private boolean isFirstTimeLocationAsking = true;  // İlk izin isteği kontrolü
    protected BottomSheetDialog dialog;
    protected BottomSheet bottomSheet;
    protected DatabaseHelper data,dbhelper;
    protected String file="file:///android_asset/leafletJS/";
    private String strLocationLatitude, strLocationLongitude;
    private double dLocationLatitude, dLocationLongitude;
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
                mWebView.loadUrl("javascript:showUserLocation(" + strLocationLatitude + "," +
                        strLocationLongitude + ")");
            }
        }
        @JavascriptInterface
        public void getShowLocation(double latitude, double longitude) {
            // Webview'de kullanıcının konumunu getir ve göster
            mWebView.loadUrl("javascript:getShowLocation(" + strLocationLatitude + "," +
                    strLocationLongitude + ")");
        }
        @JavascriptInterface
        public void receiveLocation(double latitude, double longitude) {
            // Webview'de kullanıcının konumunu güncelle
            mapView.loadUrl("javascript:receiveLocation(" + strLocationLatitude + "," +
                    strLocationLongitude + ")");
        }
        @JavascriptInterface
        public void findClosestMarker() {
            // Kullanıcının konumuna en yakın istasyonu bul
            mWebView.loadUrl("javascript:findClosestMarker()");
        }
        @JavascriptInterface
        public void setMapView(double latitude, double longitude) {
            // Kullanıcının konumuna en yakın istasyonu bul
            mWebView.loadUrl("javascript:setMapView(" + strLocationLatitude + "," +
                    strLocationLongitude + ")");
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
    } // WebAppInterface sınıfı kapanır

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
    } // databaseHelper fonksiyonu kapanır

// ---------------------------------- METODLAR ----------------------------------
    protected void cMapView (WebView mapView){
        mapView.getSettings().setJavaScriptEnabled(true);   // Javasscript ayarlarını etkinleştir
        mapView.getSettings().setDomStorageEnabled(true);   // Depolama ayarlarını etkinleştir
        mapView.loadUrl(file+"index.html");                 // Web sayfasını yükle

        buttonTriggerJS.setOnClickListener(v -> {
            // Konum tuşuna tıklanıldığında izinleri kontrol et ve başlat
            checkPermissionsAndStart();
            // Konum bilgilerine ulaşıldığı takdirde
            if (dLocationLatitude>0 && dLocationLongitude>0){
                // Webview'e gönder ve kullanıcının konumunu güncelle ve
                // Eski konuma ait kullanıcı bilgilerini temizle
                webAppInterface.receiveLocation(dLocationLatitude,dLocationLongitude);
            }
        });
        meTriggerJS.setOnClickListener(v -> {
            // İmge tuşuna tıklanıldığında izinleri kontrol et ve başlat
            checkPermissionsAndStart();
            // Konum bilgilerine ulaşıldığı takdirde
            if (dLocationLatitude>0 && dLocationLongitude>0){
                // Webview'de kullanıcının konumunu getir ve göster
                webAppInterface.getShowLocation(dLocationLatitude,dLocationLongitude);
            }
        });
        shortRoute.setOnClickListener(v -> {
            // Konum bilgilerine ulaşıldığı takdirde
            if (dLocationLatitude>0 && dLocationLongitude>0){
                // Webview' de en kısa yol fonksiyonunu çalıstır
                webAppInterface.findClosestMarker();
            }
        });
    } // cMapView fonksiyonu kapanır
    protected void runOnceShowUserLocation() {
        // Çalışmışsa çalıştırma çünkü bu metod yalnızca bir kere çalışır
        if (!isExecuted) {
            if (dLocationLatitude>0 && dLocationLongitude>0) {
                webAppInterface.setMapView(dLocationLatitude,dLocationLongitude);
                isExecuted = true;  // Metodu çalıştı olarak işaretle
                meTriggerJS.show(); // İmge tuşunu görünür yap
                shortRoute.show();  // En kısa yol tuşunu görünür yap
            }
        }
    }

// ---------------------------------- ACTIVITY LIFE CYCLE ----------------------------------
    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // Görünüm oluşturulur

        mapView = findViewById(R.id.mapview);                           // WebView görünümü
        buttonTriggerJS = findViewById(R.id.mButton);                   // Konum düğmesi
        meTriggerJS = findViewById(R.id.mMe); meTriggerJS.hide();       // İmge düğmesi
        shortRoute = findViewById(R.id.mShortRoute); shortRoute.hide(); // En kısa yol düğmesi

        cMapView(mapView);                                  // cmapView fonksiyonuna yönlendirir
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE); // Lokasyon yönetimi tanımlanır
        startLocationUpdates();                             // Lokasyon servisleri başlatılır

        DatabaseHelper dbHelper =
                new DatabaseHelper(this);           // Veritabanı nesnesi oluşturulur
        databaseHelper(dbHelper);                           // Kopyalanmak üzere sisteme gönderilir

        dialog = new BottomSheetDialog(this);               // Alt panel nesnesi oluşturulur
        webAppInterface =
                new WebAppInterface(this,dialog,mapView);  // Web arayüzü nesnesi oluşturulur
        mapView.addJavascriptInterface(new WebAppInterface
                (this,dialog,mapView), "Android");  // Javascript arayüzüne bağlanılır
    }

    // ---------------------------------- GPS ----------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi
                checkLocationEnabled();
            } else {
                // İzin reddedildi, kullanıcıya açıklama yap ve tekrar izin iste
                if (!isFirstTimeLocationAsking) {
                    isFirstTimeLocationAsking = false;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            ACCESS_FINE_LOCATION) &&
                            ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    ACCESS_COARSE_LOCATION)) {
                        showRationaleDialog("Konum İzni Gerekli", "Bu uygulama, " +
                                "belirli özellikler için konum iznine ihtiyaç duymaktadır. " +
                                "Lütfen izin verin.");
                    }
                } else {
                    // İzin ikinci kez reddedildi, işlemi durdur
                    Toast.makeText(this, "Konum izni reddedildi, " +
                            "uygulama sınırlı işlevsellikle çalışacak.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void showRationaleDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("İzin Ver", (dialog, which) ->
                        ActivityCompat.requestPermissions(this, new String[]
                                {ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE))
                .setNegativeButton("Reddet", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
    public void onLocationChanged(Location location) {
        strLocationLatitude = String.valueOf(location.getLatitude());
        strLocationLongitude = String.valueOf(location.getLongitude());
        dLocationLatitude = location.getLatitude();
        dLocationLongitude = location.getLongitude();

        // Kullanıcıya konum bilgisi gönderme
        webAppInterface.showUserLocation(dLocationLatitude,dLocationLongitude);

        if (dLocationLatitude>0 && dLocationLongitude>0){ runOnceShowUserLocation(); }
    }
    private void checkPermissionsAndStart() {
        // Konum izinlerini kontrol et
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // İzin iste
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // İzinler verilmiş ise konum servislerinin açık olup olmadığını kontrol et
            checkLocationEnabled();
        }
    }
    private void checkLocationEnabled() {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
} // MainActivity yapıcı sınıfı kapanır
