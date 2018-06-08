package org.upesacm.acmacmw.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.upesacm.acmacmw.fragment.homepage.viewpagerfragments.PostsFragment;
import org.upesacm.acmacmw.fragment.homepage.viewpagerfragments.QuizFragment;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.model.Question;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.util.ArrayList;

public class HomePageAdapter extends FragmentPagerAdapter {
    private ArrayList<Post> posts;
    private ArrayList<Question> questions;
    private HomePageClient homePageClient;
    private HomePageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        Bundle args=new Bundle();
        if(position==0) {
            PostsFragment postsFragment=new PostsFragment();
            postsFragment.setPostClient(homePageClient);
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

    public static class Builder {
        ArrayList<Post> posts;
        ArrayList<Question> questions;
        HomePageClient homePageClient;
        FragmentManager fragmentManager;

        public HomePageAdapter build() {
            HomePageAdapter homePageAdapter=new HomePageAdapter(fragmentManager);
            homePageAdapter.homePageClient = homePageClient;
            homePageAdapter.posts=posts;
            homePageAdapter.questions=questions;

            return homePageAdapter;
        }

        public Builder setPosts(ArrayList<Post> posts) {
            this.posts = posts;
            return this;
        }

        public Builder setQuestions(ArrayList<Question> questions) {
            this.questions = questions;
            return this;
        }

        public Builder setHomePageClient(HomePageClient homePageClient) {
            this.homePageClient = homePageClient;
            return this;
        }


        public Builder setFragmentManager(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
            return this;
        }
    }
}
