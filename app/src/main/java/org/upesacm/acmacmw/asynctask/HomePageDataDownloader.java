package org.upesacm.acmacmw.asynctask;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;


import org.upesacm.acmacmw.adapter.HomePageAdapter;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.model.Question;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageDataDownloader extends AsyncTask<Object,Void,Boolean> {
    HomePageClient homePageClient;
    ArrayList<Post> posts=new ArrayList<>();
    ArrayList<Question> questions=new ArrayList<>();
    HomePageAdapter homePageAdapter;
    FragmentManager fragmentManager;
    ViewPager homePager;
    ProgressBar progressBar;
    @Override
    protected Boolean doInBackground(Object... objects) {
        homePageClient=(HomePageClient)objects[0];
        fragmentManager=(FragmentManager)objects[1];
        homePager=(ViewPager)objects[2];
        progressBar=(ProgressBar)objects[3];
        try {
            Calendar calendar=Calendar.getInstance();

            HashMap<String,Post> hashMap=homePageClient.getPosts("Y"+calendar.get(Calendar.YEAR)
                    ,"M"+calendar.get(Calendar.MONTH))
                    .execute()
                    .body();

            System.out.println("hashmap : "+hashMap);
            if(hashMap!=null) {
                for(String key:hashMap.keySet()) {
                    posts.add(hashMap.get(key));
                }
                System.out.println(posts.size());
            }
        }catch(IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean successful) {
        System.out.println("onPostexeceute");
        homePageAdapter = new HomePageAdapter.Builder()
                    .setFragmentManager(fragmentManager)
                    .setPosts(posts)
                    .setQuestions(questions)
                    .setHomePageClient(homePageClient)
                    .build();
        homePager.setAdapter(homePageAdapter);

        homePager.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
