package com.sohan.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.sohan.myapp.Api.checkInternetConnection;

public class WebViewActivity extends AppCompatActivity {

    String webviewurl;
    WebView mywebview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mywebview = (WebView)findViewById(R.id.webView);

        if(checkInternetConnection.isNetworkAvailable(getApplicationContext())){
            if(InternalDataProvider.getInstance().getWebUrl().length()>0){  // have data
                webviewurl = InternalDataProvider.getInstance().getWebUrl();
                setWebview();
            }else{
                AlertDialog.Builder ad = new AlertDialog.Builder(WebViewActivity.this);
                ad.setCancelable(false);
                ad.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setWebview();
                    }
                });
                ad.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                ad.setTitle("Error");
                ad.setMessage("No Internet connection");
                ad.show();
            }

        }else{
            AlertDialog.Builder ad = new AlertDialog.Builder(WebViewActivity.this);
            ad.setCancelable(false);
            ad.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setWebview();
                }
            });
            ad.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            ad.setTitle("Error");
            ad.setMessage("No Internet connection");
            ad.show();
        }
    }

    public void setWebview(){
        if(checkInternetConnection.isNetworkAvailable(getApplicationContext())){
            if(webviewurl.length()>0){
                WebSettings webSettings = mywebview.getSettings();
                webSettings.setJavaScriptEnabled(true);
                mywebview.loadUrl(webviewurl);
            }else{
                finish();
            }
        }else {
            AlertDialog.Builder ad = new AlertDialog.Builder(WebViewActivity.this);
            ad.setCancelable(false);
            ad.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(InternalDataProvider.getInstance().getWebUrl().length()>0){
                        setWebview();
                    }else{
                        finish();
                    }
                }
            });
            ad.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            ad.setTitle("Error");
            ad.setMessage("No Internet connection");
            ad.show();
        }
    }
}
