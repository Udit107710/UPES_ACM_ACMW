package org.upesacm.acmacmw.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.upesacm.acmacmw.asynctask.OTPSender;
import org.upesacm.acmacmw.fragment.AboutFragment;
import org.upesacm.acmacmw.fragment.AdminConsoleFragment;
import org.upesacm.acmacmw.fragment.AlumniFragment;
import org.upesacm.acmacmw.fragment.EditProfileFragment;
import org.upesacm.acmacmw.fragment.GoogleSignInFragment;
import org.upesacm.acmacmw.fragment.ImageUploadFragment;
import org.upesacm.acmacmw.fragment.LoginDialogFragment;
import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.HomePageFragment;
import org.upesacm.acmacmw.fragment.MemberRegistrationFragment;
import org.upesacm.acmacmw.fragment.OTPVerificationFragment;
import org.upesacm.acmacmw.fragment.OngoingProjectFragment;
import org.upesacm.acmacmw.fragment.PasswordChangeDialogFragment;
import org.upesacm.acmacmw.fragment.StudyMaterialFragment;
import org.upesacm.acmacmw.fragment.UserProfileFragment;
import org.upesacm.acmacmw.fragment.homepage.PostsFragment;
import org.upesacm.acmacmw.listener.HomeActivityStateChangeListener;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NewMember;
import org.upesacm.acmacmw.model.TrialMember;
import org.upesacm.acmacmw.retrofit.HomePageClient;
import org.upesacm.acmacmw.retrofit.MembershipClient;
import org.upesacm.acmacmw.util.MemberIDGenerator;

import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        LoginDialogFragment.InteractionListener,
        MemberRegistrationFragment.RegistrationResultListener,
        OTPVerificationFragment.OTPVerificationResultListener,
        ImageUploadFragment.UploadResultListener,
        View.OnClickListener,
        UserProfileFragment.FragmentInteractioListener,
        EditProfileFragment.FragmentInteractionListener,
        PasswordChangeDialogFragment.PasswordChangeListener,
        GoogleSignInFragment.GoogleSignInListener{

    private static final String BASE_URL="https://acm-acmw-app-6aa17.firebaseio.com/";
    private static final int ADMIN_CONSOLE_MENU_ID = 1;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private FragmentManager fragmentManager;
    private NavigationView navigationView;
    private Retrofit retrofit;
    private HomePageClient homePageClient;
    private MembershipClient membershipClient;
    private Member signedInMember;
    private TrialMember trialMember;
    private View headerLayout;
    private String newMemberSap;

    private HomePageFragment homePageFragment;
    private HomeActivityStateChangeListener defaultStateChangeListener;
    private ArrayList<HomeActivityStateChangeListener> stateChangeListeners=new ArrayList<>();

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
        navigationView=findViewById(R.id.nav_view);
        defaultStateChangeListener = new HomeActivityStateChangeListener() {
            @Override
            public void onMemberLogin(Member member) {
                System.out.println("Default onMemberLogin");
            }

            @Override
            public void onMemberLogout() {
                System.out.println("Default onMemberLogout");
            }

            @Override
            public void onGoogleSignIn(TrialMember member) {
                System.out.println("Default onMemberLogout google sign in");
            }

            @Override
            public void onGoogleSignOut() {
                System.out.println("default on google sigon out ");
            }
        };
        stateChangeListeners.add(defaultStateChangeListener);

        /* *************************Setting the the action bar *****************************/
        setSupportActionBar(toolbar);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.drawer_opened, R.string.drawer_closed) ;
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /* **********************************************************************************/

        /* *****************Setting up home page fragment ***********************/
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        homePageFragment = HomePageFragment.newInstance(homePageClient,
                this);
        fragmentTransaction.replace(R.id.frame_layout,homePageFragment,"homepage");
        fragmentTransaction.commit();
        /* *********************************************************************************/

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.action_home);
        headerLayout=navigationView.getHeaderView(0);
        Button signin=headerLayout.findViewById(R.id.button_sign_in);
        signin.setOnClickListener(this);


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            signedInMember = (Member)bundle.get(getString(R.string.logged_in_member_details_key));
            if(signedInMember!=null)
                setUpMemberProfile(signedInMember);
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        SharedPreferences preferences=getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        final String trialMemberSap=preferences.getString(getString(R.string.trial_member_sap),null);
        if(account!=null && trialMemberSap!=null) {
            homePageClient.getTrialMember(trialMemberSap)
                    .enqueue(new Callback<TrialMember>() {
                        @Override
                        public void onResponse(Call<TrialMember> call, Response<TrialMember> response) {
                            trialMember = response.body();
                            System.out.println("get trial member  : "+trialMember);
                            for(HomeActivityStateChangeListener listener:stateChangeListeners) {
                                listener.onGoogleSignIn(trialMember);
                            }
                        }

                        @Override
                        public void onFailure(Call<TrialMember> call, Throwable t) {

                        }
                    });
        }
        else if(account!=null) {
            signOutFromGoogle();
        }
        System.out.println("signedInMember : "+signedInMember);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println("onNaviagationItemSelected");
        FragmentTransaction ft=fragmentManager.beginTransaction();
        if(item.getItemId() == R.id.action_home) {
            ft.replace(R.id.frame_layout,homePageFragment,getString(R.string.fragment_tag_homepage));
        }
        else if(item.getItemId()==R.id.action_projects) {
                ft.replace(R.id.frame_layout, new OngoingProjectFragment());
        }
        else if(item.getItemId() == R.id.action_studymaterial) {
            ft.replace(R.id.frame_layout, new StudyMaterialFragment());
        }
        else if(item.getItemId()==R.id.action_alumni) {
            ft.replace(R.id.frame_layout, new AlumniFragment());
        }
        else if(item.getItemId() == R.id.action_about) {
            ft.replace(R.id.frame_layout,new AboutFragment());
        }
        else if(item.getItemId() == ADMIN_CONSOLE_MENU_ID) {
            ft.replace(R.id.frame_layout,new AdminConsoleFragment());
        }

        ft.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button_sign_in) {
            LoginDialogFragment loginDialogFragment =new LoginDialogFragment();
            loginDialogFragment.show(fragmentManager,getString(R.string.dialog_fragment_tag_login));
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(view.getId() == R.id.image_button_profile_pic) {
            UserProfileFragment userProfileFragment=UserProfileFragment.newInstance(signedInMember);
            FragmentTransaction ft=fragmentManager.beginTransaction();
            ft.replace(R.id.frame_layout,userProfileFragment);
            ft.commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            getSupportActionBar().hide();
            setDrawerEnabled(false);
        }
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
        ft.addToBackStack("homepage");
        ft.commit();
    }

    void setUpMemberProfile(@NonNull Member member){
        System.out.println("setting up member profile");
        /* ************************** Saving sign in info in locallly *********************  */
        SharedPreferences.Editor editor=getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE).edit();
        editor.clear(); // clear the trial member data if any
        editor.putString(getString(R.string.logged_in_member_key),member.getSap());
        editor.commit();
        /* ************************************************************************************/

        /* *********************** Clearing the trial member data before loggin in ***********/
        this.trialMember=null;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null) {
            signOutFromGoogle();
        }
        /* *************************************************************************************/
        this.signedInMember=member;
        customizeNavigationDrawer(true);

        for(HomeActivityStateChangeListener listener:stateChangeListeners) {
            System.out.println("calling statechange listener callbacks");
            listener.onMemberLogin(signedInMember);
        }
    }

    @Override
    public void onBackPressed() {
        System.out.println("back button pressed");
        if(isVisible(getString(R.string.fragment_tag_homepage))) {
            System.out.println("homepage is visible");
            new AlertDialog.Builder(this)
                    .setMessage("Do you wan to close\nthe Application?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            HomeActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("user did not exit from the application");
                        }
                    })
                    .create()
                    .show();
        }
        else if(isVisible((getString(R.string.fragment_tag_image_upload)))) {
            System.out.println("back pressed image upload fragment ");
            final AlertDialog alertDialog=new AlertDialog.Builder(this)
                    .setMessage("Cancel the Upload. Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            displayHomePage();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("user did not cancel the upload");
                        }
                    })
                    .create();
            alertDialog.show();
        }
        else {
            displayHomePage();
        }
    }

    synchronized boolean isVisible(String tag) {
        Fragment fragment=fragmentManager.findFragmentByTag(tag);
        if(fragment!=null)
            return fragment.isVisible();
        return false;
    }

    void displayHomePage() {
        FragmentTransaction ft=fragmentManager.beginTransaction();
        ft.replace(R.id.frame_layout,homePageFragment,getString(R.string.fragment_tag_homepage));
        ft.commit();

        getSupportActionBar().show();
        setDrawerEnabled(true);
        navigationView.setCheckedItem(R.id.action_home);
    }

    void customizeNavigationDrawer(boolean signedin) {
        navigationView.removeHeaderView(headerLayout);
        Menu navDrawerMenu = navigationView.getMenu();
        navDrawerMenu.clear();
        getMenuInflater().inflate(R.menu.navigationdrawer,navDrawerMenu);
        if(signedin) {
            headerLayout = navigationView.inflateHeaderView(R.layout.signed_in_header);
            /* *********************************Setting the new header components**************************/
            ImageButton imageButtonProfile=headerLayout.findViewById(R.id.image_button_profile_pic);
            imageButtonProfile.setOnClickListener(this);

            TextView textViewUsername = headerLayout.findViewById(R.id.text_view_username);
            textViewUsername.setText(signedInMember.getName());
            /* *****************************************************************************************/

            /* ************ Adding the personalized corner *********************************************/
            Menu submenu = navDrawerMenu.addSubMenu(Menu.NONE,Menu.NONE,Menu.FIRST,"Personalized Corner");
            submenu.add(Menu.NONE,ADMIN_CONSOLE_MENU_ID,Menu.NONE,"Admin Console")
                    .setCheckable(true);
            /* ************************************************************************************************/
        }
        else {
            headerLayout = navigationView.inflateHeaderView(R.layout.nav_drawer_header);
            Button signin=headerLayout.findViewById(R.id.button_sign_in);
            signin.setOnClickListener(HomeActivity.this);
        }
        navigationView.invalidate();
    }

    public void addOnHomeActivityStateChangeListener(HomeActivityStateChangeListener listener) {
        System.out.println("addOnHomeActivityStateChangeListener");
        stateChangeListeners.add(listener);
        //call the listener once after intially adding it
        listener.onMemberLogin(signedInMember);
        listener.onGoogleSignIn(trialMember);
    }

    void signOutFromGoogle() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,signInOptions);
        signInClient.signOut()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for(HomeActivityStateChangeListener listener:stateChangeListeners) {
                            System.out.println("calling state change listeners onGoogleSignout");
                            listener.onGoogleSignOut();
                        }
                        System.out.println("Successfully signed out from google");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        System.out.println("failed to sign out from google");
                    }
                });
    }



    /* $$$$$$$$$$$$$$$$$$$$$$$$ Callbacks of LoginDialogFragment $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ */
    @Override
    public void onLoginPressed(LoginDialogFragment loginDialogFragment) {
        System.out.println("login button pressed");
        final String username=loginDialogFragment.getUsername();
        final String password=loginDialogFragment.getPassword();
        System.out.println("login user name : "+username);
        System.out.println("login password : "+password);

        Call<Member> memberCall=membershipClient.getMember(username);
        memberCall.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                Member member=response.body();
                String msg="";
                if(member!=null) {
                    if(member.getPassword().equals(password)) {
                        setUpMemberProfile(member);
                        msg="Successfully signed in";
                    }
                    else {
                        msg="Incorrect Username or password";
                    }
                }
                else {
                    msg="Incorrect Username or password";
                }
                Toast.makeText(HomeActivity.this,msg,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Toast.makeText(HomeActivity.this,"Unable to verify",Toast.LENGTH_SHORT).show();
            }
        });
        loginDialogFragment.dismiss();
    }

    @Override
    public void onSignUpPressed(LoginDialogFragment loginDialogFragment) {
        System.out.println("sign up button pressed");
        loginDialogFragment.dismiss();

        /* **************** obtaining stored sap(if any)************************************* */
        SharedPreferences preferences=getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        newMemberSap=preferences.getString(getString(R.string.new_member_sap_key),null);
        /* **************************************************************************************/
        System.out.println("stored sap id : "+newMemberSap);
        if(newMemberSap==null) {

            /* *****************Open the new member registration fragment here *************** */
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.frame_layout, MemberRegistrationFragment.newInstance(membershipClient, toolbar),
                    getString(R.string.fragment_tag_new_member_registration));
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
    /* $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ */




    /* ********************** Callback from MemberRegistrationFragment ************************ */
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

    /* ******************************************************************************************/




    /* ########################### Callback from OTPVerificationFragment ######################## */
    @Override
    public void onSuccessfulVerification(final OTPVerificationFragment otpVerificationFragment) {
        System.out.println("successfully verified");
        NewMember verifiedNewMember=otpVerificationFragment.getVerifiedNewMember();
        final Member member=new Member.Builder()
                .setmemberId(MemberIDGenerator.generate(verifiedNewMember.getSapId()))
                .setName(verifiedNewMember.getFullName())
                .setPassword("somepassword")
                .setSAPId(verifiedNewMember.getSapId())
                .setBranch(verifiedNewMember.getBranch())
                .setEmail(verifiedNewMember.getEmail())
                .setContact(verifiedNewMember.getPhoneNo())
                .setYear(verifiedNewMember.getYear())
                .build();
        Call<Member> memberCall=membershipClient.createMember(verifiedNewMember.getSapId(),member);
        memberCall.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                System.out.println("new acm acm w member added");
                /* ********************Adding log in info locally ************************/
                SharedPreferences.Editor editor=getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE).edit();
                editor.putString(getString(R.string.logged_in_member_key),member.getSap());
                editor.commit();
                /* ************************************************************************* */
                Toast.makeText(HomeActivity.this,"Welocme to ACM/ACM-W",Toast.LENGTH_LONG).show();
                setUpMemberProfile(member);
                displayHomePage();
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                System.out.println("failed to add new acm acmw member");
                displayHomePage();
            }
        });
    }

    @Override
    public void onMaxTriesExceed(OTPVerificationFragment otpVerificationFragment) {
        System.out.println("Max tries exceed");
        displayHomePage();
    }
    /* ###########################################################################################*/




    /* !!!!!!!!!!!!!!!!!!!!!! Callback from ImageUploadFragment !!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
    @Override
    public void onUpload(ImageUploadFragment imageUploadFragment,int resultCode) {
        String msg=null;
        if(resultCode == ImageUploadFragment.UPLOAD_SUCCESSFUL)
            msg="New Post Uploaded";
        else if(resultCode == ImageUploadFragment.UPLOAD_CANCELLED)
            msg="Upload Cancelled";
        else if(resultCode == ImageUploadFragment.UPLOAD_FAILED)
            msg="Upload Failed";
        else if(resultCode == ImageUploadFragment.UPLOAD_CANCEL_OPERATION_FAILED)
            msg="Upload cancel failed";
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
        if(isVisible((getString(R.string.fragment_tag_image_upload)))) {
            displayHomePage();
        }
        getSupportActionBar().show();
        setDrawerEnabled(true);
    }
    /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/




    /* %%%%%%%%%%%%%%%%%%%%%%%%%%Callback from UserProfileFragment %%%%%%%%%%%%%%%%%%%%%%%%%%%% */
    @Override
    public void onSignOutClicked(final UserProfileFragment userProfileFragment) {
        System.out.println("onSignOutclicked called");

        AlertDialog alertDialog=new AlertDialog.Builder(this)
                .setMessage(getString(R.string.logout_confirmation))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* ******************* Clear the member data from the app ***********************/
                        signedInMember=null;
                        SharedPreferences.Editor editor=getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE).edit();
                        editor.clear();
                        editor.commit();
                        /* **************************************************************************/

                        customizeNavigationDrawer(false);
                        displayHomePage();
                        for(HomeActivityStateChangeListener listener:stateChangeListeners) {
                            System.out.println("calling statechange listener callbacks logout");
                            listener.onMemberLogout();
                        }
                        Toast.makeText(HomeActivity.this,"Successfully Logged Out",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("user canceled the logout action");
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void onEditClicked(UserProfileFragment fragment) {
        System.out.println("on edit clicked");
        fragmentManager.beginTransaction()
                .detach(fragment)
                .commit();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.frame_layout,EditProfileFragment.newInstance(membershipClient,signedInMember));
        ft.addToBackStack(null);
        ft.commit();
    }
    /* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% */




    /* &&&&&&&&&&&&&&&&&&&&&&&Callback from EditProfileFragment&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*/
    @Override
    public void onDataEditResult(EditProfileFragment fragment, int resultCode,Member member) {
        String msg="";
        switch (resultCode) {
            case EditProfileFragment.SUCESSFULLY_SAVED_NEW_DATA : {
                msg="Saved";
                signedInMember = member;
                setUpMemberProfile(member);
                break;
            }

            case EditProfileFragment.FAILED_TO_SAVE_NEW_DATA : {
                msg="Some error occured. please Try again later";
                break;
            }

            case EditProfileFragment.ACTION_CANCELLED_BY_USER : {
                fragmentManager.popBackStackImmediate();
                msg="Cancelled";
            }

        }
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
        displayHomePage();
    }
    /* &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*/


    @Override
    public void onPasswordChange(PasswordChangeDialogFragment fragment,int resultCode) {
        String msg;
        if(resultCode==PasswordChangeDialogFragment.PASSWORD_SUCCESSSFULLY_CHANGED) {
            Member modifiedMember = new Member.Builder()
                    .setmemberId(signedInMember.getMemberId())
                    .setContact(signedInMember.getContact())
                    .setName(signedInMember.getName())
                    .setBranch(signedInMember.getBranch())
                    .setYear(signedInMember.getYear())
                    .setEmail(signedInMember.getEmail())
                    .setPassword(fragment.getNewPass())
                    .setSAPId(signedInMember.getSap())
                    .build();
            signedInMember = modifiedMember;
            msg="Password Successfully Changed";
        }
        else if(resultCode == PasswordChangeDialogFragment.INCORRECT_OLD_PASSWORD) {
            msg="Incorrect Old Password";
        }
        else if(resultCode == PasswordChangeDialogFragment.ACTION_CANCELLED_BY_USER)
            msg="cancelled";
        else if(resultCode == PasswordChangeDialogFragment.PASSWORD_CHANGE_FAILED)
            msg="Some error occured while changing password";
        else
            msg="unexpected resultcode";

        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onGoogleSignIn(final String sap,GoogleSignInAccount account) {
        if(account!=null) {
            final TrialMember trialMember = new TrialMember.Builder(String.valueOf(Calendar.getInstance().getTimeInMillis()))
                    .setEmail(account.getEmail())
                    .setName(account.getDisplayName())
                    .setSap(sap)
                    .build();
            homePageClient.getTrialMember(sap)
                    .enqueue(new Callback<TrialMember>() {
                        @Override
                        public void onResponse(Call<TrialMember> call, Response<TrialMember> response) {
                            if(response.body()==null) {
                                homePageClient.createTrialMember(sap,trialMember)
                                        .enqueue(new Callback<TrialMember>() {
                                            @Override
                                            public void onResponse(Call<TrialMember> call, Response<TrialMember> response) {
                                                System.out.println("createTrialMember response : "+response.message());
                                                SharedPreferences.Editor editor=getSharedPreferences(getString(R.string.preference_file_key),
                                                        Context.MODE_PRIVATE).edit();
                                                editor.putString(getString(R.string.trial_member_sap),sap);
                                                editor.commit();

                                                HomeActivity.this.trialMember=trialMember;
                                                System.out.println("inside home activity onGoogleSignIn"+trialMember);

                                                for(HomeActivityStateChangeListener listener:stateChangeListeners) {
                                                    System.out.println(trialMember);
                                                    listener.onGoogleSignIn(trialMember);
                                                }
                                                Toast.makeText(HomeActivity.this, "trial member created", Toast.LENGTH_LONG).show();
                                                onBackPressed();
                                            }

                                            @Override
                                            public void onFailure(Call<TrialMember> call, Throwable t) {
                                                t.printStackTrace();
                                                Toast.makeText(HomeActivity.this, "unable to create trial member", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                            else {
                                HomeActivity.this.trialMember=response.body();
                                for(HomeActivityStateChangeListener listener:stateChangeListeners) {
                                    System.out.println(trialMember);
                                    listener.onGoogleSignIn(trialMember);
                                }
                                Toast.makeText(HomeActivity.this, "trial member present", Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }

                        }

                        @Override
                        public void onFailure(Call<TrialMember> call, Throwable t) {
                            Toast.makeText(HomeActivity.this, "unable to verify trial member", Toast.LENGTH_LONG).show();
                        }
                    });

        }
        else {
            Toast.makeText(this, "unable to sign in", Toast.LENGTH_LONG).show();
        }
    }
}
