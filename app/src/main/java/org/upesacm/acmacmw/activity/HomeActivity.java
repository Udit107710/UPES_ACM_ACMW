package org.upesacm.acmacmw.activity;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.HomePageAdapter;
import org.upesacm.acmacmw.asynctask.HomePageDataDownloader;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.model.Question;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{
    private static final String BASE_URL="https://acm-acmw-app-6aa17.firebaseio.com/";
    TabLayout tabLayout;
    ViewPager homePager;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    HomePageAdapter homePageAdapter;
    FragmentManager fragmentManager;
    NavigationView navigationView;
    Retrofit retrofit;
    HomePageClient homePageClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        homePager=(ViewPager)findViewById(R.id.viewPager);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        fragmentManager=getSupportFragmentManager();
        navigationView=findViewById(R.id.nav_view);
        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        homePageClient =retrofit.create(HomePageClient.class);


        tabLayout.setupWithViewPager(homePager);

        /* *************************Setting the the action bar *****************************/
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.drawer_opened, R.string.drawer_closed) ;
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /* **********************************************************************************/


        /* *******************Downloading data for homepage********************/
        HomePageDataDownloader downloader=new HomePageDataDownloader();
        downloader.execute(homePageClient,fragmentManager,homePager);
        /* ***********************************************************************/

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        System.out.println("onNaviagationItemSelected");
        return true;
    }
}
