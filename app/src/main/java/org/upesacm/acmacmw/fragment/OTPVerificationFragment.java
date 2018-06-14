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
import org.upesacm.acmacmw.model.NewMember;
import org.upesacm.acmacmw.retrofit.MembershipClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPVerificationFragment extends Fragment implements View.OnClickListener, Callback<NewMember> {
    static final int MAX_TRIES=10;
    EditText editTextOTP;
    EditText editTextSap;
    Button buttonVerify;
    Button buttonNewSap;
    String otp;
    String sap;
    MembershipClient membershipClient;
    NewMember newMember;
    private int failureCount=0;
    private OTPVerificationResultListener resultListener;
    public OTPVerificationFragment() {
        // Required empty public constructor
    }

    public static OTPVerificationFragment newInstance(MembershipClient membershipClient,String sap) {
        OTPVerificationFragment fragment = new OTPVerificationFragment();
        fragment.membershipClient=membershipClient;
        fragment.sap=sap;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof OTPVerificationResultListener) {
            super.onAttach(context);
            resultListener=(OTPVerificationResultListener)context;
        }
        else
            throw new IllegalStateException(context.toString()+" must implement " +
                    "OnVerificationResult Listener");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("inside oncreate view of otpverification fragment");
        View view=inflater.inflate(R.layout.fragment_otpverification, container, false);
        editTextOTP=view.findViewById(R.id.editText_otp);
        editTextSap = view.findViewById(R.id.editText_sap_verify);
        buttonVerify=view.findViewById(R.id.button_verify);
        buttonNewSap = view.findViewById(R.id.button_newsap);

        buttonVerify.setOnClickListener(this);
        buttonNewSap.setOnClickListener(this);


        Bundle args=getArguments();
        if(args!=null) {
            newMember = getArguments().getParcelable(getString(R.string.new_member_key));
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        /* when verify button is pressed */
        if(view.getId() == R.id.button_verify) {
            otp = editTextOTP.getText().toString().trim();
            System.out.println("OTP Entered by user : " + otp);
            if(editTextSap.getVisibility() == View.VISIBLE) {
                String newsap= editTextSap.getText().toString().trim();
                membershipClient.getNewMemberData(newsap)
                        .enqueue(this);
            }
            else {
                if (newMember == null) {
                    membershipClient.getNewMemberData(sap)
                            .enqueue(this);
                } else {
                    verify();
                }
            }
        }
        else if(view.getId() == R.id.button_newsap) {
            editTextSap.setVisibility(View.VISIBLE);
        }
    }

    public NewMember getVerifiedNewMember() {
        return newMember;
    }

    @Override
    public void onResponse(Call<NewMember> call, Response<NewMember> response) {
        System.out.println("Successfully fetched unconfirmed member data");
        newMember=response.body();
        if(newMember==null) {
            Toast.makeText(getContext(),"No data availabe for "+sap,Toast.LENGTH_LONG).show();
        }
        verify();
    }

    @Override
    public void onFailure(Call<NewMember> call, Throwable t) {
        System.out.println("failed to fetch unconfirmed member data");
        t.printStackTrace();
    }

    void verify() {
        String msg;
        boolean verified=otp.equals(newMember.getOtp());
        if(verified) {
            msg="Successfully verified";
            resultListener.onSuccessfulVerification(this);
        }
        else {
            failureCount++;
            if(failureCount==MAX_TRIES) {
                msg="Maximum Tries exceeded Please Contact ACM Membership Team";
                resultListener.onMaxTriesExceed(this);
            }
            else
                msg="Failed to verify "+(MAX_TRIES-failureCount)+" tries left";
        }
        System.out.println(msg);
        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
    }

    public interface OTPVerificationResultListener {
        void onSuccessfulVerification(OTPVerificationFragment otpVerificationFragment);

        void onMaxTriesExceed(OTPVerificationFragment otpVerificationFragment);
    }
}
