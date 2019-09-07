package com.sohan.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sohan.myapp.Api.ApiClient;
import com.sohan.myapp.Api.ApiInterface;
import com.sohan.myapp.Api.checkInternetConnection;
import com.sohan.myapp.Model.MyDataResponseModel;
import com.sohan.myapp.OnClickInterface.OnClickForRVAdapters;
import com.sohan.myapp.RecyclerViewAdapter.RecyclerViewAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "myApp_" + this.getClass().getSimpleName();

    private CompositeDisposable mCompositeDisposable;
    ApiInterface apiInterface;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    List<MyDataResponseModel.Item> clientDataList;
    RecyclerViewAdapter adapter;
    ProgressDialog dialog;
    TextView title;
    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout mainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clientDataList = new ArrayList<>();
        mCompositeDisposable = new CompositeDisposable();
        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        recyclerView = (RecyclerView)findViewById(R.id.rvMyDataList);
        recyclerView.hasFixedSize();
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        title = (TextView)findViewById(R.id.tvTitle);
        if(checkInternetConnection.isNetworkAvailable(getApplicationContext())){
            getData();
        }else{
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setCancelable(false);
            ad.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getData();
                }
            });
            ad.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            ad.setTitle("Error");
            ad.setMessage("No Internet connection");
            ad.show();
        }
        mySwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(checkInternetConnection.isNetworkAvailable(getApplicationContext())){
                            getMyAppData();
                        }else {
                            mySwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
        );
    }

    public void getData(){
        if(checkInternetConnection.isNetworkAvailable(getApplicationContext())){
            getMyAppData();
        }else{
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setCancelable(false);
            ad.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getData();
                }
            });
            ad.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            ad.setTitle("Error");
            ad.setMessage("No Internet connection");
            ad.show();
        }
    }

    public void getMyAppData(){
        dialog = ProgressDialog.show(MainActivity.this, "", "Data loading. Please wait.....", true);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        mCompositeDisposable.add(apiInterface.myDataResponse("json", "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError));
    }


    private void handleResponse(MyDataResponseModel myAppResponseResponse) {
        dialog.dismiss();
        mySwipeRefreshLayout.setRefreshing(false);
        Log.d(TAG, "Response of product purchase view api "+myAppResponseResponse);
        clientDataList.clear();
        clientDataList.addAll(myAppResponseResponse.getItems());
        if(clientDataList.size()>0){
            title.setText(myAppResponseResponse.getTitle());
            adapter = new RecyclerViewAdapter(clientDataList, new OnClickForRVAdapters() {
                @Override
                public void selectIndex(int position) {
                    if(clientDataList.get(position).getLink().length()>0){
                        InternalDataProvider.getInstance().setWebUrl(clientDataList.get(position).getLink());
                        startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
                    }else{
                        Toast.makeText(getApplicationContext(), "No link to load webview", Toast.LENGTH_LONG).show();
                    }
                }
            });
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
            recyclerView.setAdapter(null);
        }

    }

    private void handleError(Throwable error) {
        mySwipeRefreshLayout.setRefreshing(false);
        dialog.dismiss();
        Log.d(TAG, "Error "+error);
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
        ad.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getData();
            }
        });
        ad.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        ad.setTitle("Error");
        ad.setMessage(error+"Something went wrong");
        ad.show();
    }





    @Override
    protected void onResume() {
        super.onResume();
    }
}
