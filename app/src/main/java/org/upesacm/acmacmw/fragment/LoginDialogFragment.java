package org.upesacm.acmacmw.fragment;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.upesacm.acmacmw.R;

public class LoginDialogFragment extends DialogFragment implements View.OnClickListener{
    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonLogin;
    Button buttonCancel;
    Button buttonSignup;
    
    private String username,password;
    InteractionListener interactionListener;


    public static LoginDialogFragment newInstance() {
        return new LoginDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof InteractionListener) {
            interactionListener=(InteractionListener)context;
            super.onAttach(context);
        }

        else
            throw new IllegalStateException(context.toString()+
                          " must implement OnLoginResultListener");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_login_dialog,null);
        editTextUsername=view.findViewById(R.id.editText_username);
        editTextPassword=view.findViewById(R.id.editText_password);
        buttonLogin=view.findViewById(R.id.button_login);
        buttonCancel=view.findViewById(R.id.button_cancel);
        buttonSignup=view.findViewById(R.id.button_signup);

        buttonSignup.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        username=editTextUsername.getText().toString().trim();
        password=editTextPassword.getText().toString().trim();
        if(view.getId()==R.id.button_login) {
            interactionListener.onLoginPressed(this);
        }
        else if(view.getId()==R.id.button_signup){
            interactionListener.onSignUpPressed(this);
        }
        else {
            interactionListener.onCancelPressed(this);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public interface InteractionListener {
        void onLoginPressed(LoginDialogFragment loginDialogFragment);
        void onSignUpPressed(LoginDialogFragment loginDialogFragment);
        void onCancelPressed(LoginDialogFragment loginDialogFragment);
    }
}
