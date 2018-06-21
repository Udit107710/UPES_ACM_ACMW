package org.upesacm.acmacmw.adapter;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.upesacm.acmacmw.R;

import java.util.ArrayList;

public class RecepientsAdapter extends RecyclerView.Adapter {
    ArrayList<String> recepients;
    FirebaseDatabase database;
    InteractionListener listener;

    public RecepientsAdapter(ArrayList<String> recepients,FirebaseDatabase database,InteractionListener listener) {
        this.recepients = recepients;
        this.database = database;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(viewType, parent,
                false);
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType == R.layout.recepient_layout) {
            viewHolder = new RecepientViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof RecepientViewHolder) {
            ((RecepientViewHolder) holder).bindData(recepients.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return recepients.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.recepient_layout;
    }

    class RecepientViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView textViewRecepientSap;
        DatabaseReference recepientReference;
        String recepientSap;
        RecepientViewHolder(View view) {
            super(view);
            textViewRecepientSap = view.findViewById(R.id.text_view_recepient_sap);
        }

        public void bindData(String recepientSap) {
            this.recepientSap = recepientSap;
            recepientReference = database.getReference("Heirarchy/"+recepientSap);
            textViewRecepientSap.setText(recepientSap);
            textViewRecepientSap.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onRecepientSelect(recepientSap);
        }
    }

    public interface InteractionListener {
        void onRecepientSelect(String recepientSap);
    }
}
