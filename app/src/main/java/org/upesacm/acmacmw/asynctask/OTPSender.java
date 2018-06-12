package org.upesacm.acmacmw.asynctask;


import android.os.AsyncTask;
import android.util.Log;

import org.upesacm.acmacmw.mail.GMailSender;

import java.util.Random;


public class OTPSender extends AsyncTask<String, Void, String> {

    private String mailBody;
    private String recipientMail;
    private final static String ACM_EMAIL = "appdev.upesacmacmw@gmail.com";               //ACM's gmail address
    private final static String ACM_PASSWORD = "appdev2018-2019";                      //ACM's gmsil sccount'd password


    @Override
    protected String doInBackground(String... params) {
        mailBody=params[0];
        recipientMail=params[1];

        try {

            GMailSender sender = new GMailSender(ACM_EMAIL, ACM_PASSWORD);          //Constructor call to LogIn
            sender.sendMail("This is a testing mail",mailBody,ACM_EMAIL,   //Include Subject, body, Sender's gmail and recipient's email
                    recipientMail);

        } catch (Exception e) {
            Log.d("error", e.getMessage(), e);
            return "Email Not Sent";
        }
        return "Email Sent";
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("OTPSender",result+"");
    }

}