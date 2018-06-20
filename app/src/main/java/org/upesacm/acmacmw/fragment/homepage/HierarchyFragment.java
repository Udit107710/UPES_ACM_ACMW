package org.upesacm.acmacmw.fragment.homepage;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.PagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HierarchyFragment extends Fragment {
    ViewPager viewPager;
    PagerAdapter mPagerAdapter;
    FirebaseDatabase database;


    public static HierarchyFragment newInstance(FirebaseDatabase database) {
        HierarchyFragment fragment=new HierarchyFragment();
        fragment.database = database;
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hierarchy, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("ACM"));
        tabLayout.addTab(tabLayout.newTab().setText("ACM-W"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
         viewPager = (ViewPager) view.findViewById(R.id.pager);
         mPagerAdapter=new PagerAdapter(getActivity().getSupportFragmentManager(),database);
        viewPager.setAdapter(mPagerAdapter);
         viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
