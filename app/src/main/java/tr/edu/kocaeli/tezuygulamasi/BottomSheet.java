package tr.edu.kocaeli.tezuygulamasi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

// ---------------------------------- BOTTOMSHEET YAPICI SINIFI ----------------------------------
public class BottomSheet extends BottomSheetDialogFragment {
    protected View view;
    private WebAppInterface webAppInterface;
    private List<String> mBaslik,mMarka,mSoketTuru,mSoketGucu;
    private String mSiraNo;
    private TextView tvBaslik,tvMarka;
    private Button btnRoute;
    private DatabaseHelper dbhelper;
    private RecyclerView recyclerView;
    private WebView mapView;

    public static BottomSheet newInstance(String data) {
        BottomSheet fragment = new BottomSheet();
        Bundle args = new Bundle();
        args.putString("data_key", data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSiraNo = getArguments().getString("data_key");
            mSiraNo = String.valueOf(mSiraNo);
        }
        mapView = MainActivity.mapView;
        webAppInterface = new WebAppInterface(getContext(),mapView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Layout'u inflate ederek myData ile arayüz güncellenir
        view = inflater.inflate(R.layout.modal_bottom_sheet, container, false);
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbhelper = new DatabaseHelper(getContext());
        String X = String.valueOf(dbhelper.getX(mSiraNo).get(0));
        String Y = String.valueOf(dbhelper.getY(mSiraNo).get(0));
        double dX = Double.parseDouble(dbhelper.getX(mSiraNo).get(0));
        double dY = Double.parseDouble(dbhelper.getY(mSiraNo).get(0));

        view = view.findViewById(R.id.modalBottomSheetContainer);
        recyclerView = view.findViewById(R.id.bsSoketLayout);
        tvBaslik = view.findViewById(R.id.baslik);
        tvMarka = view.findViewById(R.id.marka);
        btnRoute = view.findViewById(R.id.btnRoute);

        btnRoute.setOnClickListener(v -> webAppInterface.setEndDest(X,Y));
        btnRoute.setOnLongClickListener(v -> { showMapOptions(dX,dY); return true; });

        SoketAdapter soketAdapter = new SoketAdapter(getContext(),soketTuru(),soketGucu());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(soketAdapter);

        tvBaslik.setText(baslik().get(0));
        tvMarka.setText(marka().get(0));
    }

    public void showMapOptions(double destLatitude, double destLongitude) {
        // Hedef için genel geo URI
        Uri locationUri = Uri.parse("geo:" + destLatitude + "," + destLongitude);

        // Genel intent oluşturma
        Intent intent = new Intent(Intent.ACTION_VIEW, locationUri);

        // Eğer cihaz Huawei ise direkt intenti başlat
        if (Build.MANUFACTURER.equalsIgnoreCase("Huawei")) {
            startActivity(intent);
        } else {
            // Uyumlu tüm aktiviteleri bulma
            PackageManager packageManager = getContext().getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

            // Özel diyalog için seçenekler listesini oluşturma
            List<Intent> intentList = new ArrayList<>();
            for (ResolveInfo resolved : activities) {
                Intent targetedIntent = new Intent(Intent.ACTION_VIEW);
                targetedIntent.setData(locationUri);
                targetedIntent.setPackage(resolved.activityInfo.packageName);
                // Eğer Google Maps ise, yol tarifi modunu etkinleştir
                if (resolved.activityInfo.packageName.equals("com.google.android.apps.maps")) {
                    targetedIntent.setData(Uri.parse("https://www.google.com/maps/dir//"+
                            destLatitude + "," + destLongitude + "/@" + destLatitude + "," +
                            destLongitude + ",17z?entry=ttu"));
                }
                intentList.add(targetedIntent);
            }
            // Intent seçici diyalog oluşturma ve başlatma
            if (!intentList.isEmpty()) {
                Intent chooserIntent = Intent.createChooser(intentList.remove(0),
                        "Harita Uygulaması Seçin");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        intentList.toArray(new Parcelable[intentList.size()]));
                startActivity(chooserIntent);
            } else {
                Toast.makeText(getContext(), "Uygun bir harita uygulaması bulunamadı.",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public List<String> baslik(){
        mBaslik = dbhelper.getBaslik(mSiraNo);
        return mBaslik;
    }

    public List<String> marka(){
        mMarka = dbhelper.getMarka(mSiraNo);
        return mMarka;
    }
    public List<String> soketTuru(){
        mSoketTuru = dbhelper.getSoketTuru(mSiraNo);
        return mSoketTuru;
    }

    public List<String> soketGucu(){
        mSoketGucu = dbhelper.getSoketGucu(mSiraNo);
        return mSoketGucu;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public class WebAppInterface {
        Context mContext;
        Dialog mDialog;
        DatabaseHelper dbHelper;
        String siraNo;
        TextView mbsTextView;
        WebView mWebView;

        WebAppInterface(Context context, WebView webView) {
            mContext = getContext();
            mWebView = mapView;
            dbHelper = new DatabaseHelper(context.getApplicationContext());
        }
        @JavascriptInterface
        public void setEndDest(String latitude, String longitude) {
            mWebView.loadUrl("javascript:setEndDest(" + latitude + "," + longitude + ")");
            Log.e("setEndDest","Latitude: "+latitude+", Longitude: "+longitude);
        }
    }
}
