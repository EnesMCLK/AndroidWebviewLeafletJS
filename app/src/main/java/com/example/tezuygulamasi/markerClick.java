package com.example.tezuygulamasi;

import android.webkit.JsResult;
import android.webkit.WebView;

public interface markerClick {
    boolean onJsAlert(WebView view, String url, String message, JsResult result);
}
