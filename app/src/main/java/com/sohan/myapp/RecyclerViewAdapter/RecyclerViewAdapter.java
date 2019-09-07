package com.sohan.myapp.RecyclerViewAdapter;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sohan.myapp.Api.checkInternetConnection;
import com.sohan.myapp.Model.MyDataResponseModel;
import com.sohan.myapp.OnClickInterface.OnClickForRVAdapters;
import com.sohan.myapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import static android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    Context context;
    List<MyDataResponseModel.Item> _myAppDataList;
    private final String TAG = "myApp_"+this.getClass().getSimpleName();
    SimpleDateFormat dateFormat;
    private OnClickForRVAdapters onClickForRVAdapters;


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, link, time;
        ImageView topImage;
        LinearLayout fullView;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tvTitle);
            description = (TextView) view.findViewById(R.id.tvDescription);
            link = (TextView) view.findViewById(R.id.tvLink);
            time = (TextView)view.findViewById(R.id.tvTime);
            topImage = (ImageView)view.findViewById(R.id.imgTopImage);
            fullView = (LinearLayout) view.findViewById(R.id.llTotalHolder);
        }
    }
    public RecyclerViewAdapter(List<MyDataResponseModel.Item> myAppDataList, OnClickForRVAdapters onClickForRVAdapters) { //ArrayList<ScriptModel> newsArchives
        _myAppDataList = myAppDataList;
        this.onClickForRVAdapters = onClickForRVAdapters;
    }

    @Override
    public int getItemCount() {

        return _myAppDataList .size();
        //return 1;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_my_app, parent, false);
        RecyclerViewAdapter.ViewHolder viewHolder = new RecyclerViewAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.title.setText(_myAppDataList.get(position).getTitle());

        holder.description.setText(Html.fromHtml(_myAppDataList.get(position).getDescription()));
        holder.description.setMovementMethod(LinkMovementMethod.getInstance());

        if(_myAppDataList.get(position).getLink().length()>0){
            String link = _myAppDataList.get(position).getLink();
            String[] splitArrar = new String[2];
            splitArrar= link.split("www.");
            splitArrar = splitArrar[1].split("/");
            holder.link.setText(splitArrar[0]);
        }else{
            holder.link.setText("");
        }

        if(_myAppDataList.get(position).getPublished().length()>0){
            String dateString = _myAppDataList.get(position).getPublished();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'",Locale.US);
            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(dateString);
                long cuMillis = System.currentTimeMillis();
                String timeAgo = (String) DateUtils.getRelativeTimeSpanString(convertedDate.getTime(), cuMillis, 1, FORMAT_ABBREV_RELATIVE);
                holder.time.setText(timeAgo);
            } catch (ParseException e) {
                holder.time.setText("");
            }
        }else{
            holder.time.setText("");
        }

        if(checkInternetConnection.isNetworkAvailable(context)){
            Picasso.get()
                    .load(_myAppDataList.get(position).getMedia().getM())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.file_placeholder)
                    .error(R.drawable.file_placeholder)
                    .into(holder.topImage);
        }else{
            Picasso.get()
                    .load(_myAppDataList.get(position).getMedia().getM())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.file_placeholder)
                    .error(R.drawable.file_placeholder)
                    .into(holder.topImage);
        }


        holder.fullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickForRVAdapters.selectIndex(position);
            }
        });
    }
}
