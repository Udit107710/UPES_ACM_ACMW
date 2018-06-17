package org.upesacm.acmacmw.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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


public class MemberRegistrationFragment extends Fragment implements View.OnClickListener,
                         Callback<NewMember>{

    public static final int NEW_MEMBER_ALREADY_PRESENT=1;
    public static final int DATA_SAVE_SUCCESSFUL=2;
    public static final int DATA_SAVE_FAILED=3;
    public static final int ALREADY_PART_OF_ACM=4;

    MembershipClient membershipClient;
    Toolbar toolbar;
    EditText editTextName,editTextSap,editTextContact,editTextEmail,
            editTextYear,editTextBranch,editTextWhatsappNo;
    RadioGroup radioGroupMembership;
    Button buttonRegister;
    NewMember newMember;
    RegistrationResultListener resultListener;
    View contentHolder;
    ProgressBar progressBar;
    public MemberRegistrationFragment() {
        // Required empty public constructor
    }
    public static MemberRegistrationFragment newInstance(MembershipClient membershipClient,
                                                         Toolbar toolbar) {
        MemberRegistrationFragment fragment = new MemberRegistrationFragment();
        fragment.membershipClient=membershipClient;
        fragment.toolbar=toolbar;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof RegistrationResultListener) {
            resultListener=(RegistrationResultListener)context;
            super.onAttach(context);
        }
        else
            throw new IllegalStateException(context.toString()+" must implement " +
                    "RegistrationCompleteListener");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view=inflater.inflate(R.layout.fragment_member_registration, container, false);
       contentHolder=view.findViewById(R.id.scroll_bar_container);
       progressBar=view.findViewById(R.id.progress_bar_registration);
       editTextName=view.findViewById(R.id.editText_name);
       editTextSap=view.findViewById(R.id.editText_sap);
       editTextEmail=view.findViewById(R.id.editText_email);
       editTextContact=view.findViewById(R.id.editText_contact);
       editTextYear=view.findViewById(R.id.editText_year);
       editTextBranch=view.findViewById(R.id.editText_branch);
       editTextWhatsappNo=view.findViewById(R.id.editText_whatsappno);

       radioGroupMembership = view.findViewById(R.id.radio_group_membership);
       radioGroupMembership.check(R.id.radio_button_premium);

       buttonRegister=view.findViewById(R.id.button_register);
       buttonRegister.setOnClickListener(this);
       return view;
    }

    @Override
    public void onResume() {
        toolbar.setTitle("New Member Registration");
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        System.out.println("register button clicked");
        newMember=createNewMember();
        if(newMember!=null) {
            contentHolder.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            Call<NewMember> call = membershipClient.getNewMemberData(newMember.getSapId());
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<NewMember> call, Response<NewMember> response) {
        NewMember nm=response.body();
        if(nm==null) {
            Call<Member> memberCall=membershipClient.getMember(newMember.getSapId());
            memberCall.enqueue(new Callback<Member>() {
                @Override
                public void onResponse(Call<Member> call, Response<Member> response) {
                    if(response.body()==null) {
                        membershipClient.saveNewMemberData(newMember.getSapId(), newMember)
                                .enqueue(new Callback<NewMember>() {
                                    @Override
                                    public void onResponse(Call<NewMember> call, Response<NewMember> response) {
                                        if(response.code()==200) {
                                            MemberRegistrationFragment.this.saveSignUpInfoLocally();
                                            resultListener.onRegistrationDataSave(DATA_SAVE_SUCCESSFUL,newMember);
                                            resetRegistrationPage();
                                        }
                                        else {
                                            resultListener.onRegistrationDataSave(DATA_SAVE_FAILED, newMember);
                                            resetRegistrationPage();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<NewMember> call, Throwable t) {
                                        resultListener.onRegistrationDataSave(DATA_SAVE_FAILED,newMember);
                                        resetRegistrationPage();
                                    }
                                });
                    }
                    else {
                        resultListener.onRegistrationDataSave(ALREADY_PART_OF_ACM,newMember);
                        resetRegistrationPage();
                    }
                }

                @Override
                public void onFailure(Call<Member> call, Throwable t) {
                    Toast.makeText(MemberRegistrationFragment.this.getContext(),"Failed",Toast.LENGTH_SHORT);
                    resultListener.onRegistrationDataSave(DATA_SAVE_FAILED,newMember);
                }
            });
        }
        else {
            resetRegistrationPage();
            resultListener.onRegistrationDataSave(NEW_MEMBER_ALREADY_PRESENT,newMember);
        }
    }

    @Override
    public void onFailure(Call<NewMember> call, Throwable t) {
        System.out.println("Failed to authenticate");
        resetRegistrationPage();
        resultListener.onRegistrationDataSave(DATA_SAVE_FAILED,newMember);
    }

    public NewMember createNewMember() {
        String sap=editTextSap.getText().toString().trim();
        String name=editTextName.getText().toString().trim();
        String email=editTextEmail.getText().toString().trim();
        String contact=editTextContact.getText().toString().trim();
        String whatsapp=editTextWhatsappNo.getText().toString().trim();
        String branch=editTextBranch.getText().toString().trim();
        String year=editTextYear.getText().toString().trim();
        boolean premium=(radioGroupMembership.getCheckedRadioButtonId()==R.id.radio_button_premium);

        boolean isSapValid= Pattern.compile("5000[\\d]{5}").matcher(sap).matches();
        boolean isNameValid=Pattern.compile("[a-zA-Z\\s]+").matcher(name).matches();
        boolean isEmailValid=Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
                .matcher(email).matches();
        boolean isContactValid=Pattern.compile("[\\d]{10}").matcher(contact).matches();
        boolean isWhatsappNoValid=Pattern.compile("[\\d]{10}").matcher(whatsapp).matches();
        boolean isYearValid=Pattern.compile("[\\d]{1}").matcher(year).matches();

        String message="";
        if(isSapValid) {
            if(isNameValid) {
                if(isContactValid) {
                    if(isWhatsappNoValid) {
                        if(isYearValid) {
                            if (isEmailValid) {
                                String otp = RandomOTPGenerator.generate(Integer.parseInt(sap),  6);
                                System.out.println("generated otp : " + otp);
                                NewMember newMember = new NewMember.Builder()
                                        .setSapId(sap)
                                        .setFullName(name)
                                        .setEmail(email)
                                        .setPhoneNo(contact)
                                        .setYear(year)
                                        .setBranch(branch)
                                        .setOtp(otp)
                                        .setPremium(premium)
                                        .build();
                                return newMember;
                            } else
                                message = "Invalid Email";
                        } else
                            message = "Invalid year";
                    } else
                        message = "Invalid Whatsapp no";
                }
                else
                    message="Invalid Contact";
            }
            else
                message="Invalid Name";
        }
        else
            message="Invalid SAP ID";

        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
        return null;
    }

    void saveSignUpInfoLocally() {
        System.out.println("saveSignUpInfoLoally : "+newMember.getSapId());
        SharedPreferences preferences=getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        System.out.println(getString(R.string.new_member_sap_key));
        editor.putString(getString(R.string.new_member_sap_key),newMember.getSapId());
        editor.commit();
    }

    void resetRegistrationPage() {
        editTextEmail.setText("");
        editTextName.setText("");
        editTextSap.setText("");
        editTextBranch.setText("");
        editTextContact.setText("");
        editTextWhatsappNo.setText("");
        editTextYear.setText("");
        editTextBranch.setText("");

        contentHolder.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public interface RegistrationResultListener {
        public void onRegistrationDataSave(int resultCode,NewMember newMember);
    }
}
