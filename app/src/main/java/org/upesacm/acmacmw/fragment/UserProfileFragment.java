package org.upesacm.acmacmw.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Member;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment implements
        View.OnClickListener{

    FragmentInteractioListener listener;

    ImageView imageViewProfilePic;
    TextView textViewName;
    TextView textViewYear;
    TextView textViewBranch;
    TextView textViewSap;
    TextView textViewContact;

    FloatingActionButton fabEdit;
    FloatingActionButton fabLogout;

    Member member;
    private TextView memberId;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(Member member) {
        if(member ==  null) {
            throw new IllegalStateException("Member not signed in");
        }
        UserProfileFragment fragment=new UserProfileFragment();
        fragment.member = member;

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof FragmentInteractioListener) {
            listener=(FragmentInteractioListener)context;
            super.onAttach(context);
        }
        else
            throw new IllegalStateException(context.toString()+" must implement" +
                    "UserProfileFragment.FragmentInteractionListener");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        imageViewProfilePic = view.findViewById(R.id.image_view_profile_pic);
        textViewName = view.findViewById(R.id.text_view_profile_name);
        textViewYear = view.findViewById(R.id.text_view_profile_year);
        textViewBranch = view.findViewById(R.id.text_view_profile_branch);
        textViewSap = view.findViewById(R.id.text_view_profile_sap);
        textViewContact = view.findViewById(R.id.text_view_profile_contact);
        fabEdit = view.findViewById(R.id.fab_profile_edit);
        fabLogout = view.findViewById(R.id.fab_profile_logout);
        memberId=view.findViewById(R.id.memberId);
        textViewName.setText(member.getName());
        textViewYear.setText(member.getYear());
        textViewBranch.setText(member.getBranch());
        textViewSap.setText(member.getSap());
        textViewContact.setText(member.getContact());
        memberId.setText(member.getMemberId());
        fabEdit.setOnClickListener(this);
        fabLogout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab_profile_logout) {
            listener.onSignOutClicked(this);
        }
        else if(view.getId() == R.id.fab_profile_edit) {
            listener.onEditClicked(this);
        }
    }

    public interface FragmentInteractioListener {
        void onSignOutClicked(UserProfileFragment fragment);
        void onEditClicked(UserProfileFragment fragment);
    }



}
