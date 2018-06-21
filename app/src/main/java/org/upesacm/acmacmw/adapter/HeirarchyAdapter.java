package org.upesacm.acmacmw.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.HeirarchyModel;

import java.util.List;

public class HeirarchyAdapter extends RecyclerView.Adapter<HeirarchyAdapter.HeirarchyViewHolder> {
    private List<HeirarchyModel> heirarchyModels;
    private Context context;

    public HeirarchyAdapter(List<HeirarchyModel> heirarchyModels) {
        this.heirarchyModels = heirarchyModels;
    }

    @NonNull
    @Override
    public HeirarchyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.heirarchy_holder, parent, false);
        HeirarchyViewHolder heirarchyViewHolder = new HeirarchyViewHolder(view);
        return heirarchyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final HeirarchyViewHolder holder, final int position) {
         context=holder.itemView.getContext();
        if(heirarchyModels.get(position)!=null)
       {
           holder.name.setText(heirarchyModels.get(position).getName());
           holder.position.setText(heirarchyModels.get(position).getPostion());
           holder.about.setText(heirarchyModels.get(position).getAbout());
           holder.whatsapp.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Uri uri = Uri.parse("smsto:"+heirarchyModels.get(position).getWhatsapp());
                   Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                   i.setPackage("com.whatsapp");
                   context.startActivity(i);
               }
           });
           holder.linkedin.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   String temp= heirarchyModels.get(position).getLinkedin();
                   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(temp));
                   final PackageManager packageManager = context.getPackageManager();
                   final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                   if (list.isEmpty()) {
                       intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://you"));
                   }
                   context.startActivity(intent);
               }
           });
           holder.contact.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent callIntent = new Intent(Intent.ACTION_DIAL);
                   String temp="tel:"+heirarchyModels.get(position).getContact();
                   callIntent.setData(Uri.parse(temp));
                   context.startActivity(callIntent);
               }
           });
           if(heirarchyModels.get(position).getAvailableInCampus()==0)
           {
               holder.availabeInCampus.setImageResource(R.drawable.ic_cancel_grey_24dp);
           }
           else if(heirarchyModels.get(position).getAvailableInCampus()==1)
           {
               holder.availabeInCampus.setImageResource(R.drawable.ic_check_circle_green_24dp);
           }
               Glide.with(context)
                       .load(heirarchyModels.get(position).getImage())
                       .into(holder.image);
       }
    }
    public class HeirarchyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView position;
        TextView about;
        ImageView whatsapp;
        ImageView linkedin;
        ImageView contact;
        ImageView availabeInCampus;
        public HeirarchyViewHolder(View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            name=itemView.findViewById(R.id.name);
            position=itemView.findViewById(R.id.position);
            about=itemView.findViewById(R.id.about);
            whatsapp=itemView.findViewById(R.id.whatsapp);
            linkedin=itemView.findViewById(R.id.linkedin);
            contact=itemView.findViewById(R.id.contact);
            availabeInCampus=itemView.findViewById(R.id.availabe_in_campus);
        }
    }

    @Override
    public int getItemCount() {
        return heirarchyModels.size();
    }

}
