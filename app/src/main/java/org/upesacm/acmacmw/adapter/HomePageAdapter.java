package org.upesacm.acmacmw.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.upesacm.acmacmw.fragment.PostsFragment;
import org.upesacm.acmacmw.fragment.QuizFragment;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.model.Question;
import org.upesacm.acmacmw.retrofit.PostClient;

import java.util.ArrayList;

import retrofit2.Retrofit;

public class HomePageAdapter extends FragmentPagerAdapter {
    ArrayList<Post> posts;
    ArrayList<Question> questions;
    Retrofit retrofit;
    public HomePageAdapter(FragmentManager fragmentManager, Retrofit retrofit,
                            ArrayList<Post> posts, ArrayList<Question> questions) {
        super(fragmentManager);
        this.posts=posts;
        this.questions=questions;
        this.retrofit=retrofit;
    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        Bundle args=new Bundle();
        if(position==0) {
            PostsFragment postsFragment=new PostsFragment();
            PostClient postClient= retrofit.create(PostClient.class);
            postsFragment.setPostClient(postClient);
            args.putParcelableArrayList("posts",posts);
            fragment=postsFragment;
        }
        else {
            QuizFragment quizFragment=new QuizFragment();
            args.putParcelableArrayList("questions",questions);
            fragment=quizFragment;
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0)
            return "Insta";
        else if(position==1)
            return "Quiz";
        return "Undefined";
    }
}
