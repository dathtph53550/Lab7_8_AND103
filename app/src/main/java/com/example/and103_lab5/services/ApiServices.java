package com.example.and103_lab5.services;

import com.example.and103_lab5.model.Distributor;
import com.example.and103_lab5.model.Fruit;
import com.example.and103_lab5.model.Order;
import com.example.and103_lab5.model.Page;
import com.example.and103_lab5.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiServices {
    public static String BASE_URL = "http://192.168.0.100:3000/api/";

    @GET("getListDistributor")
        Call<Response<ArrayList<Distributor>>> getListDistributor();

    @GET("search-distributor")
        Call<Response<ArrayList<Distributor>>> searchDistributor(@Query("key") String key);

    @POST("add_distributor")
    Call<Response<ArrayList<Distributor>>> addDistributor(@Body Distributor distributor);


    @PUT("updateDistributorById/{id}")
    Call<Response<ArrayList<Distributor>>> updateDistributor(@Path("id") String id,@Body Distributor distributor);

    @DELETE("destroyDistributorById/{id}")
    Call<Response<ArrayList<Distributor>>> deleteDistributor(@Path("id") String id);

    @GET("get_list_fruit")
    Call<Response<ArrayList<Fruit>>> getList(@Header("Authorization") String token);

    @Multipart
    @POST("fruitWithImage")
    Call<Response<Fruit>> addFruitWithFileImage(@PartMap Map<String, RequestBody> requestBodyMap,
                                                @Part ArrayList<MultipartBody.Part> ds_hinh
    );

    @Multipart
    @PUT("update_fruitById/{id}")
    Call<Response<Fruit>> updateFruitWithFileImage(@PartMap Map<String, RequestBody> requestBodyMap,
                                                   @Path("id") String id,
                                                   @Part ArrayList<MultipartBody.Part> ds_hinh
    );

    @GET("get_fruitById/{id}")
    Call<Response<Fruit>> getFruitById(@Path("id") String id);

    @DELETE("destroy_fruitById/{id}")
    Call<Response<Fruit>> deleteFruits(@Path("id") String id);

    @GET("get-page-fruit")
    Call<Response<Page<ArrayList<Fruit>>>> getPageFruit( @QueryMap Map<String, String> stringMap);

    @GET("get-page-fruit")
    Call<Response<Page<ArrayList<Fruit>>>> getPageFruit(@Header("Authorization") String token, @Query("page") int page);

    @POST("add-order")
    Call<Response<ArrayList<Distributor>>> order(@Body Order order);
}


