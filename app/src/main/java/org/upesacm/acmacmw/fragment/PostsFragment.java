package org.upesacm.acmacmw.fragment;

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
    private Date currentDate;
    private int dayCount=-1;
    private SimpleDateFormat dateFormat;
    public PostsFragment() {
        // Required empty public constructor
        dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        currentDate=Calendar.getInstance().getTime();
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
        System.out.println("success");
        HashMap<String,Post> map=response.body();
        if(map!=null) {
            ArrayList<Post> posts = new ArrayList<>();
            for (String key : map.keySet()) {
                posts.add(map.get(key));
                System.out.println(map.get(key));
            }
            recyclerViewAdapter.removePost();//remove the null post
            recyclerViewAdapter.addPosts(posts);
            dayCount--;
        }
        else {
            //necesary to remove the null post when no changes are made to dataset
            recyclerViewAdapter.removePost();
        }
        recyclerViewAdapter.setLoading(false);
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

        /* *********Getting the date for the new set of posts ********************* */
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE,dayCount);
        String dateId=dateFormat.format(c.getTime());
        System.out.println("dateId : "+dateId);
        /* ******************************************************************************/



        /* ************************do the download operation here********************** */
        Call<HashMap<String,Post>> call= homePageClient.getPosts(dateId);
        call.enqueue(PostsFragment.this);
        /* **************************************************************************** */
    }
}
