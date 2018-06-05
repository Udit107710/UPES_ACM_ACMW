package org.upesacm.acmacmw;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
    public void onBindViewHolder(DetailViewHolder holder, int position) {

        AlumniDetail alumniDetail= detailList.get(position);
        holder.textViewName.setText(alumniDetail.getName());
        holder.textViewDesignation.setText(alumniDetail.getDesignation());
        holder.textViewSession.setText(alumniDetail.getSession());
        holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(alumniDetail.getImage()));
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

         public DetailViewHolder(View itemView) {
             super(itemView);

             imageView= itemView.findViewById(R.id.imageView);
             textViewName= itemView.findViewById(R.id.textViewName);
             textViewDesignation= itemView.findViewById(R.id.textViewDesignation);
             textViewSession= itemView.findViewById(R.id.textViewSession);
         }
     }
}
