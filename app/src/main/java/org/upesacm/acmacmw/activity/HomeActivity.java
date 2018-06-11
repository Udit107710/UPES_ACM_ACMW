package org.upesacm.acmacmw.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.upesacm.acmacmw.asynctask.OTPSender;
import org.upesacm.acmacmw.fragment.LoginDialogFragment;
import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.HomeViewPagerAdapter;
import org.upesacm.acmacmw.fragment.HomePageFragment;
import org.upesacm.acmacmw.fragment.MemberRegistrationFragment;
import org.upesacm.acmacmw.fragment.OTPVerificationFragment;
import org.upesacm.acmacmw.model.NewMember;
import org.upesacm.acmacmw.retrofit.HomePageClient;
import org.upesacm.acmacmw.retrofit.MembershipClient;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        LoginDialogFragment.InteractionListener,
        MemberRegistrationFragment.RegistrationResultListener,
        OTPVerificationFragment.OTPVerificationResultListener{
    private static final String BASE_URL="https://acm-acmw-app-6aa17.firebaseio.com/";

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    FragmentManager fragmentManager;
    NavigationView navigationView;
    Retrofit retrofit;
    HomePageClient homePageClient;
    MembershipClient membershipClient;
    String signedInMemberID;

    String newMemberSap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        toolbar = findViewById(R.id.my_toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        fragmentManager=getSupportFragmentManager();

        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        homePageClient =retrofit.create(HomePageClient.class);
        membershipClient=retrofit.create(MembershipClient.class);


        /* *****************Setting up home page fragment ***********************/
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,HomePageFragment.newInstance(homePageClient,
                this),"homepage");
        fragmentTransaction.commit();
        /* *********************************************************************************/

        navigationView=findViewById(R.id.nav_view);

        /* *************************Setting the the action bar *****************************/
        setSupportActionBar(toolbar);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.drawer_opened, R.string.drawer_closed) ;
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /* **********************************************************************************/


        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout=navigationView.getHeaderView(0);
        Button signin=headerLayout.findViewById(R.id.button_sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginDialogFragment loginDialogFragment =new LoginDialogFragment();
                loginDialogFragment.show(fragmentManager,"fragment_login");
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        System.out.println("onNaviagationItemSelected");
        return true;
    }

    @Override
    public void onLoginPressed(LoginDialogFragment loginDialogFragment) {
        System.out.println("login button pressed");
        System.out.println("login user name : "+loginDialogFragment.getUsername());
        System.out.println("login password : "+loginDialogFragment.getPassword());

    }

    @Override
    public void onSignUpPressed(LoginDialogFragment loginDialogFragment) {
        System.out.println("sign up button pressed");
        loginDialogFragment.dismiss();

        /* **************** obtaining stored sap(if any)************************************* */
        SharedPreferences preferences=getPreferences(Context.MODE_PRIVATE);
        newMemberSap=preferences.getString(getString(R.string.new_member_sap_key),null);
        /* **************************************************************************************/
        System.out.println("stored sap id : "+newMemberSap);
        if(newMemberSap==null) {

            /* *****************Open the new member registration fragment here *************** */
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.addToBackStack("homepage");
            ft.replace(R.id.frame_layout, MemberRegistrationFragment.newInstance(membershipClient, toolbar),
                    "member_registration_fragment");
            ft.commit();

            /* ******************************************************************************/
        }
        else {
            startOTPVerificationPage(null);
        }
        setDrawerEnabled(false);
    }

    @Override
    public void onCancelPressed(LoginDialogFragment loginDialogFragment) {
        System.out.println("Cancel button pressed");
        loginDialogFragment.dismiss();
    }

    public void setDrawerEnabled(boolean enable) {
        int lockMode=enable?DrawerLayout.LOCK_MODE_UNLOCKED:DrawerLayout.
                LOCK_MODE_LOCKED_CLOSED;
        drawerLayout.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enable);

    }

    public void setActionBarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onRegistrationDataSave(int resultCode, NewMember newMember) {
        System.out.println("result code is : "+resultCode);
        String msg="";
        if(resultCode==MemberRegistrationFragment.DATA_SAVE_SUCCESSFUL) {
            msg="Data Saved Successfully";
            String mailBody="name : "+newMember.getFullName()+"\n"
                            +"Email : "+newMember.getEmail()+"\n"
                            +"OTP : "+newMember.getOtp();
            OTPSender sender=new OTPSender();
            sender.execute(mailBody,"arkk.abhi1@gmail.com");
            startOTPVerificationPage(newMember);
        }
        else if(resultCode==MemberRegistrationFragment.NEW_MEMBER_ALREADY_PRESENT) {
            msg="New member data already present";
        }
        else if(resultCode==MemberRegistrationFragment.ALREADY_PART_OF_ACM) {
            msg="Alread a part of ACM";
        }
        else
            msg="Data save Failed. Please check your connection";

        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void startOTPVerificationPage(NewMember newMember) {
        OTPVerificationFragment fragment;
        if(newMember!=null) {
            fragment=OTPVerificationFragment.newInstance(membershipClient, newMember.getSapId());
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.new_member_key), newMember);
            fragment.setArguments(bundle);
        }
        else {
            fragment=OTPVerificationFragment.newInstance(membershipClient,newMemberSap);
        }
        FragmentTransaction ft=fragmentManager.beginTransaction();
        ft.replace(R.id.frame_layout,fragment,"otp_verifiction");
        ft.commit();
    }

    @Override
    public void onSuccessfulVerification() {
        System.out.println("successfully verified");

    }

    @Override
    public void onMaxTriesExceed() {
        System.out.println("Max tries exceed");
    }
}
