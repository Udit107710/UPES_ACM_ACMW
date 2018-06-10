package org.upesacm.acmacmw.fragment;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.upesacm.acmacmw.R;

public class LoginDialogFragment extends DialogFragment implements View.OnClickListener{
    EditText username;
    EditText password;
    Button login;
    Button cancel;

    LoginResultListener loginResultListener;

    public static LoginDialogFragment newInstance() {
        return new LoginDialogFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_login_dialog,null);
        username=view.findViewById(R.id.username);
        password=view.findViewById(R.id.password);
        login=view.findViewById(R.id.button_login);
        cancel=view.findViewById(R.id.button_cancel);

        login.setOnClickListener(this);
        cancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button_login) {

        }
        else {

        }
    }

    public interface LoginResultListener {
        public void onLoginSuccess();
        public void onLoginFail();
    }
}
