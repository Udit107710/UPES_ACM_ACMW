package org.upesacm.acmacmw.fragment.homepage;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.adapter.HomeViewPagerAdapter;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.model.Question;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {

    static final int CHOOSE_FROM_GALLERY=2;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    TabLayout tabLayout;
    ViewPager homePager;
    FragmentManager childFm;
    HomePageClient homePageClient;
    ProgressBar progressBar;


    HomeFragmentInteractionListener interactionListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(HomePageClient homePageClient) {
        HomeFragment fragment = new HomeFragment();
        fragment.homePageClient = homePageClient;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof HomeFragmentInteractionListener) {
            interactionListener=(HomeFragmentInteractionListener)context;
            super.onAttach(context);
        }
        else
            throw new IllegalStateException(context.toString()+" must implement" +
                    "HomeFragmentInteractionListener");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        childFm=getChildFragmentManager();
        System.out.println("onCreate home page fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        homePager = (ViewPager) view.findViewById(R.id.viewPager);
        progressBar = view.findViewById(R.id.progress_bar_home);

        tabLayout.setupWithViewPager(homePager);

        homePager.setVisibility(View.INVISIBLE);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.cameraButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCameraButtonClick(view);
            }
        });

        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter.Builder()
                .setFragmentManager(getChildFragmentManager())
                .setPosts(new ArrayList<Post>())
                .setQuestions(new ArrayList<Question>())
                .setHomePageClient(homePageClient)
                .build();
        homePager.setAdapter(homeViewPagerAdapter);

        homePager.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        return view;
    }

    /************************taking picture************/

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("onActivityResult Called");
        Bitmap imageBitmap = null;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            System.out.println("request image capture");
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] byteArray = stream.toByteArray();
            Bundle args=new Bundle();
            args.putByteArray("data",byteArray);
            interactionListener.onNewPostDataAvailable(args);

        } else if (requestCode == CHOOSE_FROM_GALLERY && resultCode == RESULT_OK && resultCode!=RESULT_CANCELED) {
            System.out.println("choose from gallery");
            Uri uri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Bundle args=new Bundle();
                args.putByteArray("image_data",byteArray);
                interactionListener.onNewPostDataAvailable(args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onCameraButtonClick(View view) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    dispatchTakePictureIntent();
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Photo"), CHOOSE_FROM_GALLERY);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    /************************taking picture************/

    public interface HomeFragmentInteractionListener{
        void onNewPostDataAvailable(Bundle args);
    }

}
