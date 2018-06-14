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

import org.upesacm.acmacmw.asynctask.OTPSender;
import org.upesacm.acmacmw.fragment.AboutFragment;
import org.upesacm.acmacmw.fragment.AdminConsoleFragment;
import org.upesacm.acmacmw.fragment.AlumniFragment;
import org.upesacm.acmacmw.fragment.EditProfileFragment;
import org.upesacm.acmacmw.fragment.ImageUploadFragment;
import org.upesacm.acmacmw.fragment.LoginDialogFragment;
import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.HomePageFragment;
import org.upesacm.acmacmw.fragment.MemberRegistrationFragment;
import org.upesacm.acmacmw.fragment.OTPVerificationFragment;
import org.upesacm.acmacmw.fragment.OngoingProjectFragment;
import org.upesacm.acmacmw.fragment.StudyMaterialFragment;
import org.upesacm.acmacmw.fragment.UserProfileFragment;
import org.upesacm.acmacmw.fragment.homepage.PostsFragment;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.NewMember;
import org.upesacm.acmacmw.retrofit.HomePageClient;
import org.upesacm.acmacmw.retrofit.MembershipClient;
import org.upesacm.acmacmw.util.MemberIDGenerator;

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
        PostsFragment.HomeFragmentInteractionListener,
        ImageUploadFragment.UploadResultListener,
        View.OnClickListener,
        UserProfileFragment.FragmentInteractioListener,
        EditProfileFragment.FragmentInteractionListener{
    private static final String BASE_URL="https://acm-acmw-app-6aa17.firebaseio.com/";
    private static final int ADMIN_CONSOLE_MENU_ID = 1;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    FragmentManager fragmentManager;
    NavigationView navigationView;
    Retrofit retrofit;
    HomePageClient homePageClient;
    MembershipClient membershipClient;
    Member signedInMember;
    View headerLayout;
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
        headerLayout=navigationView.getHeaderView(0);
        Button signin=headerLayout.findViewById(R.id.button_sign_in);
        signin.setOnClickListener(this);

        SharedPreferences preferences=getPreferences(Context.MODE_PRIVATE);
        String signedInMemberSap=preferences.getString(getString(R.string.logged_in_member_key),null);
        if(signedInMemberSap!=null) {
            membershipClient.getMember(signedInMemberSap)
                    .enqueue(new Callback<Member>() {
                        @Override
                        public void onResponse(Call<Member> call, Response<Member> response) {
                            signedInMember=response.body();
                            if(signedInMember!=null) {
                                setUpMemberProfile(signedInMember);
                            }
                        }

                        @Override
                        public void onFailure(Call<Member> call, Throwable t) {
                            System.out.println("failed to fetch signed in member details");
                        }
                    });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println("onNaviagationItemSelected");
        FragmentTransaction ft=fragmentManager.beginTransaction();
        if(item.getItemId() == R.id.action_home) {
            if(!fragmentManager.popBackStackImmediate()) {
                ft.replace(R.id.frame_layout, HomePageFragment.newInstance(homePageClient, this));
            }
        }
        else {
            if(item.getItemId()==R.id.action_projects) {
                ft.add(R.id.frame_layout, new OngoingProjectFragment());
            }
            else if(item.getItemId() == R.id.action_studymaterial) {
                ft.add(R.id.frame_layout, new StudyMaterialFragment());
            }
            else if(item.getItemId()==R.id.action_alumni) {
                ft.add(R.id.frame_layout, new AlumniFragment());
            }
            else if(item.getItemId() == R.id.action_about) {
                ft.add(R.id.frame_layout,new AboutFragment());
            }
            else if(item.getItemId() == ADMIN_CONSOLE_MENU_ID) {
                ft.add(R.id.frame_layout,new AdminConsoleFragment());
            }
        }
        ft.commit();

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button_sign_in) {
            LoginDialogFragment loginDialogFragment =new LoginDialogFragment();
            loginDialogFragment.show(fragmentManager,"fragment_login");
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(view.getId() == R.id.image_button_profile_pic) {
            UserProfileFragment userProfileFragment=UserProfileFragment.newInstance(signedInMember);
            FragmentTransaction ft=fragmentManager.beginTransaction();
            ft.addToBackStack("homepage");
            ft.add(R.id.frame_layout,userProfileFragment);
            ft.commit();
            drawerLayout.closeDrawer(GravityCompat.START);
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

    void setUpMemberProfile(Member member){
        System.out.println("setting up member profile");
        /* ************************** Saving sign in info in locallly *********************  */
        SharedPreferences.Editor editor=getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(getString(R.string.logged_in_member_key),member.getSap());
        editor.commit();
        /* ************************************************************************************/
        this.signedInMember=member;

        /* ******* Change the header layout ********* */
        navigationView.removeHeaderView(headerLayout);
        navigationView.inflateHeaderView(R.layout.signed_in_header);
        /* ******************************************* */

        /* Setting the new header components*/
        headerLayout=navigationView.getHeaderView(0);
        ImageButton imageButtonProfile=headerLayout.findViewById(R.id.image_button_profile_pic);
        imageButtonProfile.setOnClickListener(this);

        TextView textViewUsername = headerLayout.findViewById(R.id.text_view_username);
        textViewUsername.setText(member.getName());
        /* ***********************************************************/


        /* *************** Adding personal corner for signed in members ***************************/
        Menu navdrawerMenu = navigationView.getMenu();
        Menu submenu = navdrawerMenu.addSubMenu(Menu.NONE,Menu.NONE,Menu.FIRST,"Personalized Corner");
        submenu.add(Menu.NONE,ADMIN_CONSOLE_MENU_ID,Menu.NONE,"Admin Console")
                .setCheckable(true);
        navigationView.invalidate();
        /* ******************************************************************************************************/
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
        fragmentManager.beginTransaction()
                .detach(otpVerificationFragment)
                .commit();
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
                SharedPreferences.Editor editor=getPreferences(Context.MODE_PRIVATE).edit();
                editor.putString(getString(R.string.logged_in_member_key),member.getSap());
                editor.commit();
                /* ************************************************************************* */
                Toast.makeText(HomeActivity.this,"Welocme to ACM/ACM-W",Toast.LENGTH_LONG).show();
                setUpMemberProfile(member);
                fragmentManager.beginTransaction()
                        .detach(otpVerificationFragment)
                        .commit();
                fragmentManager.popBackStackImmediate();
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                System.out.println("failed to add new acm acmw member");
                fragmentManager.beginTransaction()
                        .detach(otpVerificationFragment)
                        .commit();
                fragmentManager.popBackStackImmediate();
            }
        });
    }

    @Override
    public void onMaxTriesExceed(OTPVerificationFragment otpVerificationFragment) {
        System.out.println("Max tries exceed");
        fragmentManager.beginTransaction()
                .detach(otpVerificationFragment)
                .commit();
    }
    /* ###########################################################################################*/




    /* @@@@@@@@@@@@@@@@@@@@@@@@Callback from PostFragment @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    @Override
    public void onNewPostDataAvailable(Bundle args) {
        System.out.println("on new post data available called");
        ImageUploadFragment imageUploadFragment=ImageUploadFragment.newInstance(homePageClient);
        imageUploadFragment.setArguments(args);

        FragmentTransaction ft=fragmentManager.beginTransaction();
        ft.addToBackStack("posts_fragment");
        ft.add(R.id.frame_layout,imageUploadFragment,"image_upload_fragment");
        ft.commit();
    }
    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/




    /* !!!!!!!!!!!!!!!!!!!!!! Callback from ImageUploadFragment !!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
    @Override
    public void onUpload(ImageUploadFragment imageUploadFragment,int resultCode) {
            fragmentManager.beginTransaction().detach(imageUploadFragment).commit();
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
                        SharedPreferences.Editor editor=getPreferences(Context.MODE_PRIVATE).edit();
                        editor.clear();
                        editor.commit();
                        /* **************************************************************************/

                        /* ***Change the header layout and add again add the listener to sign in button  ******/
                        navigationView.removeHeaderView(headerLayout);
                        headerLayout = navigationView.inflateHeaderView(R.layout.nav_drawer_header);
                        Button signin=headerLayout.findViewById(R.id.button_sign_in);
                        signin.setOnClickListener(HomeActivity.this);
                        /* ************************************************************************************/

                        /* *************** Adding the logged header and menu **************************/
                        Menu navdrawerMenu = navigationView.getMenu();
                        navdrawerMenu.clear();
                        getMenuInflater().inflate(R.menu.navigationdrawer,navdrawerMenu);
                        navigationView.invalidate();
                        /* ******************************************************************************/

                        FragmentTransaction ft=fragmentManager.beginTransaction();
                        ft.detach(userProfileFragment);
                        ft.commit();

                        fragmentManager.popBackStackImmediate();

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
        fragmentManager.beginTransaction().detach(fragment).commit();
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
    }
    /* &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*/
}
