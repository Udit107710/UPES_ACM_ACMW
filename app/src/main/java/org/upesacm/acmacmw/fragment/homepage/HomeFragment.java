package org.upesacm.acmacmw.fragment.homepage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.CaptureImageActivity;
import org.upesacm.acmacmw.asynctask.HomePageDataDownloader;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager homePager;
    FragmentManager childFm;
    HomePageClient homePageClient;
    ProgressBar progressBar;
    HomePageDataDownloader downloader;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(HomePageClient homePageClient) {
        HomeFragment fragment = new HomeFragment();
        fragment.homePageClient = homePageClient;
        return fragment;
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
        /* *******************Downloading data for homepage********************/
        downloader = new HomePageDataDownloader();
        downloader.execute(homePageClient, childFm, homePager, progressBar);
        /* ***********************************************************************/
        return view;
    }

    /************************taking picture************/
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap imageBitmap = null;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Intent intent = new Intent(getActivity(), CaptureImageActivity.class);
            intent.putExtra("data", byteArray);
            startActivity(intent);
        } else if (requestCode == 2 && resultCode == RESULT_OK && resultCode!=RESULT_CANCELED) {
            Uri uri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Intent intent = new Intent(getActivity(), CaptureImageActivity.class);
                intent.putExtra("data", byteArray);
                startActivity(intent);

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
                    startActivityForResult(Intent.createChooser(intent, "Select Photo"), 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    /************************taking picture************/
}
