package org.upesacm.acmacmw.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.upesacm.acmacmw.fragment.LoginDialogFragment;
import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.HomePageAdapter;
import org.upesacm.acmacmw.fragment.HomePageFragment;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{
    private static final String BASE_URL="https://acm-acmw-app-6aa17.firebaseio.com/";

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

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        fragmentManager=getSupportFragmentManager();

        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        homePageClient =retrofit.create(HomePageClient.class);


        /* *****************Experimenting with Fragment Transactions ***********************/
        final FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,HomePageFragment.newInstance(homePageClient),"homepage");
        fragmentTransaction.commit();
        /* *********************************************************************************/

        navigationView=findViewById(R.id.nav_view);

        /* *************************Setting the the action bar *****************************/
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.drawer_opened, R.string.drawer_closed) ;
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /* **********************************************************************************/


        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout=navigationView.getHeaderView(0);
        Button signin=headerLayout.findViewById(R.id.button_sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginDialogFragment loginDialogFragment =new LoginDialogFragment();
                loginDialogFragment.show(fragmentManager,"fragment_login");
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        System.out.println("onNaviagationItemSelected");
        return true;
    }
}
