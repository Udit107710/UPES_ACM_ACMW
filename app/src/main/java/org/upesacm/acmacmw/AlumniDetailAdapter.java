package org.upesacm.acmacmw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class AlumniDetailAdapter extends RecyclerView.Adapter<AlumniDetailAdapter.DetailViewHolder>  {

    private Context mCtx;
    private List<AlumniDetail> detailList;

    public AlumniDetailAdapter(Context mCtx, List<AlumniDetail> detailList) {
        this.mCtx = mCtx;
        this.detailList = detailList;
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(mCtx);
        View view= inflater.inflate(R.layout.alumni_card_layout, null);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder,final int position) {

        AlumniDetail alumniDetail= detailList.get(position);
        holder.textViewName.setText(alumniDetail.getName());
        holder.textViewDesignation.setText(alumniDetail.getPosition());
        holder.textViewSession.setText(alumniDetail.getSession());
        holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(Integer.parseInt(alumniDetail.getImage())));

        holder.contactim.setOnClickListener( new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                System.out.println("Write on click");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                String temp="tel:"+detailList.get(position).getContact();
                //callIntent.setData(Uri.parse(temp));
                mCtx.startActivity(callIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder
     {
         ImageView imageView;
         TextView textViewName;
         TextView textViewDesignation;
         TextView textViewSession;
         ImageView contactim;
         ImageView linkedinim;

         public DetailViewHolder(View itemView) {
             super(itemView);

             imageView= itemView.findViewById(R.id.imageView);
             textViewName= itemView.findViewById(R.id.textViewName);
             textViewDesignation= itemView.findViewById(R.id.textViewDesignation);
             textViewSession= itemView.findViewById(R.id.textViewSession);
             contactim= itemView.findViewById(R.id.contactim);
             linkedinim= itemView.findViewById(R.id.linkedinim);
         }
     }
}
