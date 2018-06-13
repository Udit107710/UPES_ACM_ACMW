package org.upesacm.acmacmw;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

class AlumniViewHolder extends RecyclerView.ViewHolder {

        ImageView contactim;
        ImageView linkedinim;
        public AlumniViewHolder(View itemView) {
        super(itemView);
        contactim= itemView.findViewById(R.id.contactim);
        linkedinim= itemView.findViewById(R.id.linkedinim);
    }

    public void setName(String Name) {
        TextView post_name = (TextView) itemView.findViewById(R.id.textViewName);
        post_name.setText(Name);
    }

    public void setPosition(String Position) {
        TextView post_position = (TextView) itemView.findViewById(R.id.textViewDesignation);
        post_position.setText(Position);
    }

    public void setSession(String Session) {
        TextView post_session = (TextView) itemView.findViewById(R.id.textViewSession);
        post_session.setText(Session);
    }

    public void setImage(Context ctx, String Image) {
        ImageView post_image = (ImageView) itemView.findViewById(R.id.imageView);
        Picasso.get().load(Image).into(post_image);
    }
}
