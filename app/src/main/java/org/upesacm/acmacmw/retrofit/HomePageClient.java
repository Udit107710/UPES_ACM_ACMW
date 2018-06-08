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
    @GET("posts/{date}.json")
    Call<HashMap<String,Post>> getPosts(@Path("date") String date);

    @GET("posts.json")
    Call<HashMap<String,HashMap<String,Post>>> getAllPosts();
}
