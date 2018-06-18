package org.upesacm.acmacmw.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NewMember;
import org.upesacm.acmacmw.retrofit.MembershipClient;
import org.upesacm.acmacmw.util.RandomOTPGenerator;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment
        implements View.OnClickListener, Callback<Member> {

    public static final int SUCESSFULLY_SAVED_NEW_DATA=1;
    public static final int ACTION_CANCELLED_BY_USER=2;
    public static final int FAILED_TO_SAVE_NEW_DATA=3;

    MembershipClient membershipClient;
    Member member;
    EditText editTextName;
    EditText editTextContact;
    EditText editTextEmail;
    EditText editTextYear;
    EditText editTextBranch;
    Button buttonSave;
    Button buttonCancel;
    Button buttonPassChange;
    PasswordChangeDialogFragment passchangeFrag;
    FragmentInteractionListener listener;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance(MembershipClient membershipClient,Member member) {
        if(member == null)
            throw new IllegalStateException("Cannot access without login");
        EditProfileFragment fragment=new EditProfileFragment();
        fragment.membershipClient = membershipClient;
        fragment.member = member;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof FragmentInteractionListener) {
            listener=(FragmentInteractionListener)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context.toString()+" must implement" +
                    "FragmentInteraction Listener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        editTextName = view.findViewById(R.id.edit_text_edit_name);
        editTextContact = view.findViewById(R.id.edit_text_edit_contact);
        editTextEmail = view.findViewById(R.id.edit_text_edit_email);
        editTextYear = view.findViewById(R.id.edit_text_edit_year);
        editTextBranch = view.findViewById(R.id.edit_text_edit_branch);
        buttonCancel = view.findViewById(R.id.button_edit_cancel);
        buttonSave = view.findViewById(R.id.button_edit_save);
        buttonPassChange = view.findViewById(R.id.button_edit_passchange);

        editTextName.setText(member.getName());
        editTextContact.setText(member.getContact());
        editTextEmail.setText(member.getEmail());
        editTextYear.setText(member.getYear());
        editTextBranch.setText(member.getBranch());

        buttonCancel.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        buttonPassChange.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_edit_save) {
            member = modifyMember();
            if(member!=null) {
                membershipClient.createMember(member.getSap(), member)
                        .enqueue(this);
            }
        }
        else if(view.getId() == R.id.button_edit_cancel) {
            listener.onDataEditResult(this,ACTION_CANCELLED_BY_USER,member);
        }
        else if(view.getId() == R.id.button_edit_passchange) {
            passchangeFrag = PasswordChangeDialogFragment.newInstance(membershipClient,member);
            passchangeFrag.show(getChildFragmentManager(),getString(R.string.dialog_fragment_tag_pass_change));
        }
    }

    @Override
    public void onResponse(Call<Member> call, Response<Member> response) {
        System.out.println(response.message());
        listener.onDataEditResult(this,SUCESSFULLY_SAVED_NEW_DATA,member);
    }

    @Override
    public void onFailure(Call<Member> call, Throwable t) {
        t.printStackTrace();
        listener.onDataEditResult(this,FAILED_TO_SAVE_NEW_DATA,member);
    }

    public Member modifyMember() {
        String name=editTextName.getText().toString().trim();
        String email=editTextEmail.getText().toString().trim();
        String contact=editTextContact.getText().toString().trim();
       // String whatsapp=editTextWhatsappNo.getText().toString().trim();
        String branch=editTextBranch.getText().toString().trim();
        String year=editTextYear.getText().toString().trim();


        boolean isNameValid=Pattern.compile("[a-zA-Z\\s]+").matcher(name).matches();
        boolean isEmailValid=Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
                .matcher(email).matches();
        boolean isContactValid=Pattern.compile("[\\d]{10}").matcher(contact).matches();
      //  boolean isWhatsappNoValid=Pattern.compile("[\\d]{10}").matcher(whatsapp).matches();
        boolean isYearValid=Pattern.compile("[\\d]{1}").matcher(year).matches();

        String message="";
        if(isNameValid) {
            if(isContactValid) {
                if(isEmailValid) {
                    if (isYearValid) {
                        Member modifiedMember = new Member.Builder()
                                .setSAPId(member.getSap())
                                .setmemberId(member.getMemberId())
                                .setName(name)
                                .setEmail(email)
                                .setContact(contact)
                                .setYear(year)
                                .setBranch(branch)
                                .setPassword((passchangeFrag==null)?member.getPassword():
                                        passchangeFrag.getNewPass())
                                .build();
                                return modifiedMember;
                    } else
                        message = "Invalid Year";
                } else
                    message = "Invalid Email";
            } else
                message="Invalid Contact";
        } else
            message="Invalid Name";

        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
        return null;
    }

    public interface FragmentInteractionListener {
        void onDataEditResult(EditProfileFragment fragment,int resultcode,Member member);
    }

}
