package org.upesacm.acmacmw.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Question;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class QuestionsRecyclerViewAdapter extends RecyclerView.Adapter{
    ArrayList<Question> questions;
    public QuestionsRecyclerViewAdapter(ArrayList<Question> questions) {
        this.questions = questions;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(viewType,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bindData(questions.get(position));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.question_layout;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        ViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.question_textView);
        }

        public void bindData(Question question) {
            textView.setText(question.getQuestion());
        }
    }
}
