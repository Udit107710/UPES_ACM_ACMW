package org.upesacm.acmacmw.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.QuestionsRecyclerViewAdapter;
import org.upesacm.acmacmw.model.Question;

import java.util.ArrayList;


public class QuizFragment extends Fragment {
    RecyclerView recyclerView;
    QuestionsRecyclerViewAdapter recyclerViewAdapter;
    ArrayList<Question> questions;
   public QuizFragment() {
       //required Default Constructor
   }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_quiz,null);
        recyclerView=view.findViewById(R.id.questions_recyclerView);
        /* *******************Retrieving Arguments**********************************/
        Bundle args=getArguments();
        questions=args.getParcelableArrayList("questions");
        /* *************************************************************************/

        recyclerViewAdapter = new QuestionsRecyclerViewAdapter(questions);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }
}


