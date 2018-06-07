package org.upesacm.acmacmw.retrofit;

import org.upesacm.acmacmw.model.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HomePageClient {

   @GET("Posts/{year}.json")
    Call<HashMap<String,HashMap<String,Post>>> getPosts(@Path("year") String year);

    @GET("Posts/{year}/{month}.json")
    Call<HashMap<String,Post>> getPosts(@Path("year") String year, @Path("month") String month);

}
