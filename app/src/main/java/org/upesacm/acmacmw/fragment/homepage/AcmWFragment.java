package org.upesacm.acmacmw.fragment.homepage;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.HeirarchyAdapter;
import org.upesacm.acmacmw.model.HeirarchyModel;

import java.util.ArrayList;
import java.util.List;

public class AcmWFragment extends android.support.v4.app.Fragment {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    RecyclerView mRecyclerView;
    List<HeirarchyModel> acmWheirarchyModels;

    public static AcmWFragment newInstance(FirebaseDatabase database) {
        AcmWFragment fragment=new AcmWFragment();
        fragment.mFirebaseDatabase = database;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseReference=mFirebaseDatabase.getReference().child("Heirarchy");
    }

    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acm_w, container, false);
        mRecyclerView=view.findViewById(R.id.acm_w_office_bearer);
        acmWheirarchyModels=new ArrayList<>();
        if(mDatabaseReference!=null)
        {
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    acmWheirarchyModels.removeAll(acmWheirarchyModels);
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        HeirarchyModel heirarchyModel=dataSnapshot1.getValue(HeirarchyModel.class);
                        if(heirarchyModel.getAcm_acmw().equals("ACMW")){
                            acmWheirarchyModels.add(heirarchyModel);
                        }
                        if(acmWheirarchyModels!=null)
                        {
                            HeirarchyAdapter heirarchyAdapter=new HeirarchyAdapter(acmWheirarchyModels);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            mRecyclerView.setAdapter(heirarchyAdapter);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }


        return view;

    }
}
