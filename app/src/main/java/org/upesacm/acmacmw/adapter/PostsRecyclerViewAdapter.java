package org.upesacm.acmacmw.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.listener.OnLoadMoreListener;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.util.ArrayList;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter {

    private OnLoadMoreListener onLoadMoreListener;
    private RecyclerView recyclerView;
    boolean isLoading=false;
    ArrayList<Post> posts;
    HomePageClient homePageClient;
    public PostsRecyclerViewAdapter(RecyclerView recyclerView, HomePageClient homePageClient) {
        this.recyclerView=recyclerView;
        this.homePageClient = homePageClient;
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
        View viewitem = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if(viewType==R.layout.post_layout)
            return new PostViewHolder(viewitem);
        else
            return new LoadingViewHolder(viewitem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        System.out.println("onBinViewHolder Called");
        System.out.println(posts.get(position));
        if(holder instanceof PostViewHolder) {
            Post post=posts.get(position);
            ((PostViewHolder) holder).bindData(post);
        }
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
        if(posts.get(position)==null && isLoading)
                return R.layout.loading_post_layout;
        return R.layout.post_layout;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView textViewCaption;
        private ImageView imageView;
        private ImageButton imageButtonLike;
        private TextView textViewLikeCount;
        private Post post;
        public PostViewHolder(View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.text_view_post_username);
            textViewCaption = itemView.findViewById(R.id.text_view_post_caption);
            imageView=itemView.findViewById(R.id.image_view_post);
            imageButtonLike = itemView.findViewById(R.id.image_button_post_like);
            textViewLikeCount = itemView.findViewById(R.id.text_view_post_likecount);
        }

        //This function has been defined to seperate the code of binding the data with the views
        //Othewise the data binding could be done inside the Adapter's onBindViewHolder function
        public void bindData(final Post post) {
            System.out.println("bindData called");
            this.post=post;
            username.setText(post.getMemberId());
            textViewCaption.setText(post.getCaption());
            Glide.with(recyclerView)
                    .load(post.getImageUrl())
                    .into(imageView);
            textViewLikeCount.setText("in progress");
        }

//        @Override
//        public void onClick(View view) {
//            Post modifiedPost=new Post.Builder()
//                    .setMemberId(post.getMemberId())
//                    .setCaption(post.getCaption())
//                    .setPostId(post.getPostId())
//                    .setImageUrl(post.getImageUrl())
//                    .setLikesCount(post.getLikesCount()+1)
//                    .setMonthId(post.getMonthid())
//                    .setYearId(post.getPostId())
//                    .build();
//            homePageClient.createPost(modifiedPost.
//        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
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
        System.out.println("set Posts called : "+posts.size());
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
