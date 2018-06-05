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
        System.out.println("downloadHomePage - current date : "+stringDate);

        try {
            HashMap<String,Post> map=null;
            int daycount=-1;
            while(map==null && !date.equals("01-06-2018")) {
                System.out.println("downloadHomePage -  date : "+stringDate);

                Call<HashMap<String, Post>> postsCall = homePageClient.getPosts(stringDate);
                //Call<HashMap<String,Post>> questionsCall=homePageClient.getQuestions();
                map = postsCall.execute().body();
                if (map != null) {
                    for (String key : map.keySet()) {
                        posts.add(map.get(key));
                    }
                }
                calendar.setTime(date);// date
                calendar.add(Calendar.DATE,daycount);//date - 1 day
                date=calendar.getTime();//converting back to date object
                stringDate=sdf.format(date);//converting to string

                daycount--;
            }

           /* HashMap<String,Question> questionHashMap=questionsCall.execute().body();
            if(questionHashMap!=null) {
                for(String key:questionHashMap.keySet()) {
                    questions.add(questionHashMap.get(key));
                }
            } */

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
