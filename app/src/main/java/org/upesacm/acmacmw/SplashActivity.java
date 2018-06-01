package org.upesacm.acmacmw;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.upesacm.acmacmw.adapter.HomePageAdapter;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.model.Question;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager homePager;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    HomePageAdapter homePageAdapter;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        homePager=(ViewPager)findViewById(R.id.viewPager);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        fragmentManager=getSupportFragmentManager();

        tabLayout.setupWithViewPager(homePager);
        /**************************Setting the the action bar *****************************/
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.drawer_opened, R.string.drawer_closed) ;
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /***********************************************************************************


        /********************Creating demo posts and questions********************/
        ArrayList<Post> posts=new ArrayList<>();
        for(int i=1;i<1000;i++) {
            posts.add(new Post("Post "+i));
        }
        ArrayList<Question> questions=new ArrayList<>();
        for(int i=1;i<1000;i++) {
            questions.add(new Question("Question "+i));
        }
        /************************************************************************/


        /**********************creating and setting the home page adapter******************/
        homePageAdapter = new HomePageAdapter(fragmentManager,posts,questions);
        homePager.setAdapter(homePageAdapter);
        /************************************************************************************/

    }
}
