package org.upesacm.acmacmw.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Post;

import java.util.List;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter {
    List<Post> posts;
    public PostsRecyclerViewAdapter(List<Post> posts) {
        this.posts=posts;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("onCreateViewHolder : "+viewType);
        //NOTE : Apprently when inflating views inside constraint layout it is important to specify the root(constraint layout)
        //      Otherwise the inflator wont inflate it according to the parameters of constraint layoout
        //     Eg ; match_constraint is equivalent to 0dp in constaint layout
        //      But if root is not specified then, the inflator will inflate the child with 0dp such that its dimension(whichever
        //      we have set as match_constraint) is 0dp.
        View viewitem= LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false);
        return new ViewHolder(viewitem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        System.out.println("onBinViewHolder Called");
        System.out.println("data : "+posts.get(position).getImageUrl());
        ((ViewHolder) holder).bindData(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.post_layout;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView imageView;
        public ViewHolder(View itemView) {
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
}
