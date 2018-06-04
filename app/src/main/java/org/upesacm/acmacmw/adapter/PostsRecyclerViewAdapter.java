package org.upesacm.acmacmw.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.listener.OnLoadMoreListener;
import org.upesacm.acmacmw.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter {

    private OnLoadMoreListener onLoadMoreListener;
    private RecyclerView recyclerView;
    boolean isLoading=false;
    ArrayList<Post> posts;
    private String date;
    public PostsRecyclerViewAdapter(RecyclerView recyclerView,ArrayList<Post> posts) {
        this.posts=posts;
        this.recyclerView=recyclerView;
        addOnScrollListener();
    }
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("onCreateViewHolder : "+viewType);
        //NOTE : Apprently when inflating views inside constraint layout it is important to specify the root(constraint layout)
        //      Otherwise the inflator wont inflate it according to the parameters of constraint layoout
        //     Eg ; match_constraint is equivalent to 0dp in constaint layout
        //      But if root is not specified then, the inflator will inflate the child with 0dp such that its dimension(whichever
        //      we have set as match_constraint) is 0dp.
        View viewitem ;
        if(viewType==R.layout.post_layout) {
            viewitem = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new PostViewHolder(viewitem);
        }
        else  {
            viewitem = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new LoadingViewHolder(viewitem);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        System.out.println("onBinViewHolder Called");
        if(holder instanceof PostViewHolder)
            ((PostViewHolder) holder).bindData(posts.get(position));
        else if(holder instanceof LoadingViewHolder)
            ((LoadingViewHolder) holder).bindData();
    }

    @Override
    public int getItemCount() {
        if(posts==null)
            return 0;
        System.out.println("getItem Cont "+posts.size());
        return posts.size();
    }

    @Override
    public int getItemViewType(final int position) {
        if(posts.get(position)==null)
            return R.layout.loading_post_layout;
        return R.layout.post_layout;

    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView imageView;
        public PostViewHolder(View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
        }

        //This function has been defined to seperate the code of binding the data with the views
        //Othewise the data binding could be done inside the Adapter's onBindViewHolder function
        public void bindData(final Post post) {
            System.out.println("bindData called");
            username.setText(post.getImageUrl());
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar loadingProgressBar;
        LoadingViewHolder(View itemView) {
            super(itemView);
            loadingProgressBar=itemView.findViewById(R.id.progressBar);
        }
        public void bindData() {
            loadingProgressBar.setIndeterminate(true);
        }
    }



    public void removePost() {
        int pos=posts.size()-1;
        posts.remove(pos);
        notifyItemRemoved(pos);
    }

    public void addPost(Post post) {
        posts.add(post);
        notifyItemInserted(posts.size()-1);
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts=posts;
        notifyDataSetChanged();
    }

    public void addPosts(ArrayList<Post> posts) {
        if(posts!=null) {
            int prevLast = this.posts.size() - 1;
            this.posts.addAll(posts);
            notifyItemRangeInserted(prevLast + 1, posts.size());
        }
    }

    public void setOnLoadMoreListener(final OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener=onLoadMoreListener;
    }

    private void addOnScrollListener() {
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager=(LinearLayoutManager)recyclerView.getLayoutManager();
                int totalItemCount=linearLayoutManager.getItemCount();
                int visibleItemCount=linearLayoutManager.getChildCount();
                int scrolleditems=linearLayoutManager.findFirstVisibleItemPosition();
                if((scrolleditems+visibleItemCount)==totalItemCount) {
                    System.out.println("isloading : "+isLoading);
                    if(isLoading==false) { // to avoid unecessary calls to onLoadMore
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        else
                            System.out.println("no listener set for loading more data");
                    }
                    else
                        System.out.println("still loading");
                }
            }
        });
    }

    public void setLoading(boolean value) {
        isLoading=value;
    }
}
