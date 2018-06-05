package org.upesacm.acmacmw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class Alumni extends AppCompatActivity {

    RecyclerView recyclerView;
    AlumniDetailAdapter adapter;
    List<AlumniDetail> detailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumni);

        detailList= new ArrayList<>();
        recyclerView= (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        detailList.add(new AlumniDetail(R.drawable.a1, "ABC", "App Head", "2016-2017"));
        detailList.add(new AlumniDetail(R.drawable.a2, "ABCD", "Webmaster", "2016-2017"));

        adapter= new AlumniDetailAdapter(this, detailList);
        recyclerView.setAdapter(adapter);
    }
}
