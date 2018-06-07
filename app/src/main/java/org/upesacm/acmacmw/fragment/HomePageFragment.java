package org.upesacm.acmacmw.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.homepage.HomeFragment;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.lang.reflect.Field;

public class HomePageFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {


    BottomNavigationView bottomNavigationView;
    private HomePageClient homePageClient;
    private FragmentManager childFm;
    public HomePageFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(HomePageClient homePageClient) {
        HomePageFragment homePageFragment=new HomePageFragment();
        homePageFragment.homePageClient=homePageClient;

        return homePageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        FragmentTransaction ft=childFm.beginTransaction();
        ft.replace(R.id.frameLayout_homepage,HomeFragment.newInstance(homePageClient));
        ft.commit();

        return view;
    }

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
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.action_star) {
            FragmentTransaction ft=childFm.beginTransaction();
            ft.replace(R.id.frameLayout_homepage, new AlumniFragment());
            ft.commit();
        }
        else if(item.getItemId()==R.id.action_home) {
            FragmentTransaction ft=childFm.beginTransaction();
            ft.replace(R.id.frameLayout_homepage, HomeFragment.newInstance(homePageClient));
            ft.commit();
        }
        return true;
    }
}
