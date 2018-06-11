package org.upesacm.acmacmw.asynctask;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;


import org.upesacm.acmacmw.adapter.HomeViewPagerAdapter;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.model.Question;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class HomePageDataDownloader extends AsyncTask<Object,Void,Boolean> {
    HomePageClient homePageClient;
    ArrayList<Post> posts;
    ArrayList<Question> questions=new ArrayList<>();
    HomeViewPagerAdapter homeViewPagerAdapter;
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
            posts=new ArrayList<>();
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
        homeViewPagerAdapter = new HomeViewPagerAdapter.Builder()
                    .setFragmentManager(fragmentManager)
                    .setPosts(posts)
                    .setQuestions(questions)
                    .setHomePageClient(homePageClient)
                    .build();
        homePager.setAdapter(homeViewPagerAdapter);

        homePager.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public boolean isDataAvailable() {
        return posts!=null;
    }
}
