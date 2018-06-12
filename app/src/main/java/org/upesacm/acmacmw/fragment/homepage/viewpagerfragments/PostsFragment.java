package org.upesacm.acmacmw.fragment.homepage.viewpagerfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.upesacm.acmacmw.adapter.PostsRecyclerViewAdapter;
import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.listener.OnLoadMoreListener;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsFragment extends Fragment implements  OnLoadMoreListener,
        Callback<HashMap<String,Post>> {

    RecyclerView recyclerView;
    PostsRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Post> posts;
    HomePageClient homePageClient;
    private int dayCount=0;
    private int monthCount=-1;
    private SimpleDateFormat dateFormat;
    public PostsFragment() {
        // Required empty public constructor
        dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_posts,null);
        recyclerView=view.findViewById(R.id.posts_recyclerView);

        /* ************************Retrieving Aruguement*********************************/
        Bundle args=getArguments();
        posts=args.getParcelableArrayList("posts");
        /* ****************************************************************************/

        recyclerViewAdapter=new PostsRecyclerViewAdapter(recyclerView,posts);

        /* ********************** Setting OnLoadMoreListener ************************************/
        recyclerViewAdapter.setOnLoadMoreListener(this);
        /* **************************************************************************************/

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }

    public Fragment setPostClient(HomePageClient homePageClient) {
        this.homePageClient = homePageClient;
        return this;
    }

    @Override
    public void onResponse(Call<HashMap<String, Post>> call, Response<HashMap<String, Post>> response) {
        HashMap<String,Post> hashMap=response.body();
        monthCount--;
        if(hashMap!=null) {
            System.out.println("onResponse hashmap : "+hashMap);
            ArrayList<Post> posts = new ArrayList<>();
            for (String key : hashMap.keySet()) {
                posts.add(hashMap.get(key));
                System.out.println(hashMap.get(key));
            }
            recyclerViewAdapter.removePost();//remove the null post
            recyclerViewAdapter.addPosts(posts);
            recyclerViewAdapter.setLoading(false);
        }
        else {
            System.out.println("hashmap is null");
            //necesary to remove the null post when no changes are made to dataset
            Calendar c=Calendar.getInstance();
            c.add(Calendar.MONTH,monthCount);
            if (c.get(Calendar.YEAR)>=2018) {

                homePageClient.getPosts("Y"+c.get(Calendar.YEAR),
                        "M"+c.get(Calendar.MONTH))
                        .enqueue(this);
            }
            else {
                recyclerViewAdapter.removePost();
                recyclerViewAdapter.setLoading(false);
            }
        }
    }

    @Override
    public void onFailure(Call<HashMap<String, Post>> call, Throwable t) {
        System.out.println("failed");
        t.printStackTrace();
        recyclerViewAdapter.removePost();
        recyclerViewAdapter.setLoading(false);

    }

    @Override
    public void onLoadMore() {
        System.out.println("on load more");
        recyclerViewAdapter.setLoading(true);//keep this above the addPost
        recyclerViewAdapter.addPost(null);//place holder for the progress bar


        Calendar c=Calendar.getInstance();
        c.add(Calendar.MONTH,monthCount);

        homePageClient.getPosts("Y"+c.get(Calendar.YEAR),"M"+c.get(Calendar.MONTH))
                .enqueue(this);
    }
}
