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
import org.upesacm.acmacmw.model.Post;

import java.util.ArrayList;

public class PostsFragment extends Fragment {

    RecyclerView recyclerView;
    PostsRecyclerViewAdapter recyclerViewAdapter;
    ArrayList<Post> posts;
    public PostsFragment() {
        // Required empty public constructor
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

        recyclerViewAdapter=new PostsRecyclerViewAdapter(posts);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }
}
