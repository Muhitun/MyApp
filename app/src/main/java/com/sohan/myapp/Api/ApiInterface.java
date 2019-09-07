package com.sohan.myapp.Api;

import com.sohan.myapp.Model.MyDataResponseModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("photos_public.gne?")
    Observable<MyDataResponseModel> myDataResponse(@Query("format") String format,
                                                       @Query("nojsoncallback") String nojsoncallback);
}
