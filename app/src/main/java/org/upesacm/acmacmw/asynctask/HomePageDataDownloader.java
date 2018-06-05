package org.upesacm.acmacmw.asynctask;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;


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
    @Override
    protected Boolean doInBackground(Object... objects) {
        homePageClient=(HomePageClient)objects[0];
        fragmentManager=(FragmentManager)objects[1];
        homePager=(ViewPager)objects[2];

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        Date date=calendar.getTime();
        String stringDate=sdf.format(date);

        calendar.add(Calendar.MONTH,-1);
        String defaultDate=sdf.format(calendar.getTime());
        System.out.println("downloadHomePage - current date : "+stringDate);

        try {
            HashMap<String,Post> hashMap=null;
            Call<HashMap<String,HashMap<String,Post>>> call=homePageClient.getAllPosts();
            HashMap<String,HashMap<String,Post>> stringHashMapHashMap=call.execute().body();

            while(!stringDate.equals(defaultDate)) {
                System.out.println("downloadHomePage -  date : "+stringDate);
                hashMap=stringHashMapHashMap.get(stringDate);
                if (hashMap != null) {
                    for (String key : hashMap.keySet()) {
                        posts.add(hashMap.get(key));
                    }
                }
                System.out.println("posts : "+posts.size());
                calendar.setTime(date);// date
                calendar.add(Calendar.DATE,-1);//date - 1 day
                date=calendar.getTime();//converting back to date object
                stringDate=sdf.format(date);//converting to string
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
    }
}
