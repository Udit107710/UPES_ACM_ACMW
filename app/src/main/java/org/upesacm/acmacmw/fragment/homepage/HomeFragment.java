package org.upesacm.acmacmw.fragment.homepage;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.asynctask.HomePageDataDownloader;
import org.upesacm.acmacmw.retrofit.HomePageClient;


public class HomeFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager homePager;
    FragmentManager childFm;
    HomePageClient homePageClient;
    ProgressBar progressBar;
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(HomePageClient homePageClient) {
        HomeFragment fragment = new HomeFragment();
        fragment.homePageClient=homePageClient;
        return fragment;
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
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        tabLayout = (TabLayout)view.findViewById(R.id.tabLayout);
        homePager=(ViewPager)view.findViewById(R.id.viewPager);
        progressBar=view.findViewById(R.id.progress_bar_home);

        tabLayout.setupWithViewPager(homePager);

        homePager.setVisibility(View.INVISIBLE);
        /* *******************Downloading data for homepage********************/
        HomePageDataDownloader downloader=new HomePageDataDownloader();
        downloader.execute(homePageClient,childFm,homePager,progressBar);
        /* ***********************************************************************/
        return view;
    }
}
