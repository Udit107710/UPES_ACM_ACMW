package org.upesacm.acmacmw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.upesacm.acmacmw.model.NewMember;

public class EventRegistration extends AppCompatActivity {
    EditText ParticipantName,ParticipantSAPID,ParticipantBranch,Year,
            ParticipantEmail,ParticipantPhone,ParticipantWhatsapp,C_Name,
            C_Roll_No;
    Spinner Events;
    TextView University;
    RadioButton Upes,Other;
    Button Register,Cancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

    }
}
