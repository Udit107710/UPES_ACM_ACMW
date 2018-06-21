package org.upesacm.acmacmw.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.fragment.homepage.ContactUsFragment;
import org.upesacm.acmacmw.fragment.homepage.HierarchyFragment;
import org.upesacm.acmacmw.fragment.homepage.PostsFragment;
import org.upesacm.acmacmw.fragment.homepage.UpcomingEventsFragment;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.lang.reflect.Field;

public class HomePageFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {


    BottomNavigationView bottomNavigationView;
    private HomePageClient homePageClient;
    Context context;
    private FragmentManager childFm;
    FirebaseDatabase database;
    PostsFragment postsFragment;
    Fragment userSelectedFragment;
    public HomePageFragment() {
        // Required empty public constructor
    }

    public static HomePageFragment newInstance(FirebaseDatabase database,HomePageClient homePageClient,Context context) {
        HomePageFragment homePageFragment=new HomePageFragment();
        homePageFragment.homePageClient=homePageClient;
        homePageFragment.context=context;
        homePageFragment.database = database;
        return homePageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate homepagefragment");
        super.onCreate(savedInstanceState);
        childFm=getChildFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home_page, container, false);

        bottomNavigationView=(BottomNavigationView)view.findViewById(R.id.bottomNavigationView);
        disableShiftMode(bottomNavigationView);


        /* ********************** Setting up listener for bottom navigation view ********/
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        /* ****************************************************************************** */

        postsFragment = PostsFragment.newInstance(database,homePageClient);
            FragmentTransaction ft = childFm.beginTransaction();
            ft.replace(R.id.frameLayout_homepage, (userSelectedFragment==null)?postsFragment:userSelectedFragment
                    , "posts_fragment");
            ft.commit();

        return view;
    }


    @Override
    public void onResume() {
        System.out.println("onResume homepagefragment");
        ((HomeActivity)context).setDrawerEnabled(true);
        ((HomeActivity)context).setActionBarTitle("ACM ACM-W App");
        super.onResume();
    }

    @SuppressLint("RestrictedApi")
    void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                //icons at centre
                item.setPadding(0, 20, 0, 0);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
                //increasing icon size
                final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
                final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
                final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
                layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
                iconView.setLayoutParams(layoutParams);
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.action_posts) {
            FragmentTransaction ft=childFm.beginTransaction();
            userSelectedFragment = postsFragment;
            ft.replace(R.id.frameLayout_homepage, userSelectedFragment);
            ft.commit();
        }
        else if(item.getItemId()==R.id.action_upcoming_events) {
            FragmentTransaction ft=childFm.beginTransaction();
            userSelectedFragment = new UpcomingEventsFragment();
            ft.replace(R.id.frameLayout_homepage,userSelectedFragment );
            ft.commit();
        }
        else if(item.getItemId() == R.id.action_heirarchy) {
            HierarchyFragment hierarchyFragment = new HierarchyFragment();
            FragmentTransaction ft=childFm.beginTransaction();
            userSelectedFragment = hierarchyFragment;
            ft.replace(R.id.frameLayout_homepage,userSelectedFragment);
            ft.commit();
        }
        else if(item.getItemId() == R.id.action_contact) {
            FragmentTransaction ft=childFm.beginTransaction();
            userSelectedFragment=new ContactUsFragment();
            ft.replace(R.id.frameLayout_homepage, userSelectedFragment);
            ft.addToBackStack("homepage");
            ft.commit();
        }
        return true;
    }
}
