package org.upesacm.acmacmw.fragment.homepage;

import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.Manifest;
import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.activity.HomeActivity;
import org.upesacm.acmacmw.adapter.PostsRecyclerViewAdapter;
import org.upesacm.acmacmw.fragment.GoogleSignInFragment;
import org.upesacm.acmacmw.fragment.ImageUploadFragment;
import org.upesacm.acmacmw.fragment.LoginDialogFragment;
import org.upesacm.acmacmw.listener.HomeActivityStateChangeListener;
import org.upesacm.acmacmw.listener.OnLoadMoreListener;
import org.upesacm.acmacmw.model.Member;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.model.TrialMember;
import org.upesacm.acmacmw.retrofit.HomePageClient;
import org.upesacm.acmacmw.util.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


public class PostsFragment extends Fragment
        implements OnLoadMoreListener,
        Callback<HashMap<String,Post>>,
        ValueEventListener,
        HomeActivityStateChangeListener,
        View.OnClickListener {

    static final int CHOOSE_FROM_GALLERY=2;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE = 3;

    FragmentManager childFm;
    HomePageClient homePageClient;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private int monthCount=-1;
    FirebaseDatabase database;
    private DatabaseReference postsReference;
    PostsRecyclerViewAdapter recyclerViewAdapter;
    FloatingActionButton floatingActionButton;

    Member signedInMember;
    TrialMember trialMember;
    private Uri imageUri;
    private Uri fileUri;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance(FirebaseDatabase database,HomePageClient homePageClient) {
        PostsFragment fragment = new PostsFragment();
        fragment.homePageClient = homePageClient;
        fragment.database = database;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate post fragment");
        childFm = getChildFragmentManager();

        Calendar calendar = Calendar.getInstance();
        if (database != null) {
            postsReference = database
                    .getReference("posts/" + "Y" + calendar.get(Calendar.YEAR) + "/"
                            + "M" + calendar.get(Calendar.MONTH));
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.posts_recyclerView);
        progressBar = view.findViewById(R.id.progress_bar_home);
        floatingActionButton = view.findViewById(R.id.cameraButton);
        floatingActionButton.setOnClickListener(this);
        recyclerViewAdapter=new PostsRecyclerViewAdapter(recyclerView,homePageClient,database);

        recyclerViewAdapter.setOnLoadMoreListener(this);



        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);

        /* ***************************Adding ValueEvent Listener******************************/
        postsReference.addValueEventListener(this);
        /* **********************************************************************************/

        progressBar.setVisibility(View.VISIBLE);

        ((HomeActivity)getActivity()).addOnHomeActivityStateChangeListener(this);

        return view;
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cameraButton) {
            System.out.println("cameraButton pressed"+trialMember);
            if (signedInMember != null ) {
                onCameraButtonClick(view);
            }
            else if(trialMember!=null) {
                long trialPeriod=30*24*60*60*(1000L);
                long elapsedTime = Calendar.getInstance().getTimeInMillis() - Long.parseLong(trialMember.getCreationTimeStamp());
                System.out.println("trialPerion : "+trialPeriod);
                System.out.println("elasped : "+elapsedTime);
                if(elapsedTime > trialPeriod) {
                    Toast.makeText(getContext(),"Your free trial is over",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), "time remainging : " + (trialPeriod - elapsedTime), Toast.LENGTH_LONG).show();
                    onCameraButtonClick(view);
                }
            }
            else {
                LoginDialogFragment loginDialogFragment =new LoginDialogFragment();
                loginDialogFragment.show(getActivity().getSupportFragmentManager(),getString(R.string.dialog_fragment_tag_login));
                Toast.makeText(getContext(), "Please Login First", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            System.out.println("unexpected on click callback");
        }
    }



    /* ************************** functions for taking picture ***********************************/
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("fail", "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
    private void dispatchTakePictureIntent() {
            try {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                    //                File imageFile = null;
//                try {
//                    imageFile = createImageFile();
//                }catch(IOException ioe) {
//                    ioe.printStackTrace();
//                }
//                if(imageFile !=null) {
//                    imageUri = FileProvider.getUriForFile(getContext(),
//                            "org.upesacm.acmacmw.fileprovider",imageFile);
//                    System.out.println("imageUri : "+imageUri);
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    // }
                }
            }
            catch (OutOfMemoryError error)
            {
                Toast.makeText(getContext(), "Unable to Capture image because"+error.getCause(), Toast.LENGTH_SHORT).show();

            }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "jpeg_"+timeStamp+"_"+".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        storageDir.mkdirs();
        File image = new File(storageDir,filename);
        return image;
    }

    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if(grantResults!=null) {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }
            else {
                Toast.makeText(getContext(),"Please grant camera and storage permission",Toast.LENGTH_LONG).show();
                Log.i("MainActivity", "onRequestPermissionsResult Permission denied\n");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("onActivityResult Called");
        Bitmap imageBitmap = null;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            System.out.println("request image capture");
                if(fileUri!=null)
                {
                    try {

                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
//                        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                        String uri=compressImage(fileUri.toString());
                        fileUri= Uri.parse(uri);
                        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                        byte[] byteArray = os.toByteArray();
                        System.out.println("byte array : " + byteArray);
                        for (int i = 0; i < byteArray.length; i++) {
                            System.out.print(byteArray[i]);
                        }
                        Bundle args = new Bundle();
                        args.putByteArray("image_data", byteArray);
                        this.onNewPostDataAvailable(args);
                    }
                    catch (OutOfMemoryError error)
                    {
                        Log.d("out of memory",""+error.getCause());
                        Toast.makeText(getContext(),"unable to upload image:"+error.getCause(),Toast.LENGTH_SHORT).show();

                    }

                }
//            getActivity().getContentResolver().notifyChange(imageUri, null);
//            ContentResolver cr = getActivity().getContentResolver();
//            Bitmap bitmap;
//            try
//            {
//                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
//                System.out.println("bitmap : "+bitmap);
//            }
//            catch (Exception e)
//            {
//                Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
//                Log.d("tag", "Failed to load", e);
//            }


            /*
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
             */



        } else if (requestCode == CHOOSE_FROM_GALLERY && resultCode == RESULT_OK && resultCode!=RESULT_CANCELED) {
            System.out.println("choose from gallery");
            Uri uri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                int nh = (int) ( imageBitmap.getHeight() * (1024.0 / imageBitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, 1024, nh, true);
                scaled.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Bundle args=new Bundle();
                args.putByteArray("image_data",byteArray);
                this.onNewPostDataAvailable(args);
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
                    if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED 
                            &&ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                        dispatchTakePictureIntent();
                    }
                    else {
                        System.out.println("Permission for camera or storage not granted. Requesting Permission");
                        ActivityCompat.requestPermissions(getActivity()
                                ,new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PostsFragment.CAMERA_AND_STORAGE_PERMISSION_REQUEST_CODE);
                    }

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
    /* ********************************************************************************************/




    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@ Overriden methods of ValueEventListener @@@@@@@@@@@@@@@@@@@@@ */
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        System.out.println("onDataChange method called");
        ArrayList<Post> posts=new ArrayList<>();
        for(DataSnapshot ds:dataSnapshot.getChildren()) {
            Post p=dataSnapshot.child(ds.getKey()).getValue(Post.class);
            posts.add(0,p);
        }
        progressBar.setVisibility(View.INVISIBLE);
        recyclerViewAdapter.setPosts(posts);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        System.out.println("Error is new fetching data");
    }
    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */




    /* ####################### Overriden methods of Callback<HashMap<String,Post>> ############### */
    @Override
    public void onResponse(Call<HashMap<String, Post>> call, Response<HashMap<String, Post>> response) {
        HashMap<String,Post> hashMap=response.body();
        monthCount--;
        if(hashMap!=null) {
            System.out.println("onResponse hashmap : "+hashMap);
            ArrayList<Post> posts = new ArrayList<>();
            for (String key : hashMap.keySet()) {
                posts.add(0,hashMap.get(key));
                System.out.println(hashMap.get(key));
            }
            recyclerViewAdapter.removePost();//remove the null post
            recyclerViewAdapter.addPosts(posts);
            recyclerViewAdapter.setLoading(false);
        }
        else {
            System.out.println("hashmap is null");
            //necesary to remove the null post when no changes are made to dataset
            Calendar c=Calendar.getInstance();
            c.add(Calendar.MONTH,monthCount);
            if (c.get(Calendar.YEAR)>=2018) {
                homePageClient.getPosts("Y"+c.get(Calendar.YEAR),
                        "M"+c.get(Calendar.MONTH))
                        .enqueue(this);
            }
            else {
                recyclerViewAdapter.removePost();
                recyclerViewAdapter.setLoading(false);
            }
        }
    }

    @Override
    public void onFailure(Call<HashMap<String, Post>> call, Throwable t) {
        System.out.println("failed");
        t.printStackTrace();
        recyclerViewAdapter.removePost();
        recyclerViewAdapter.setLoading(false);

    }
    /* ######################################################################################### */

    public void onNewPostDataAvailable(Bundle args) {
        System.out.println("on new post data available called");
        ((HomeActivity)getContext()).getSupportActionBar().hide();
        ((HomeActivity)getContext()).setDrawerEnabled(false);
        ImageUploadFragment imageUploadFragment=ImageUploadFragment.newInstance(homePageClient);

        String ownerName=null;
        String ownerSapId=null;
        if(signedInMember!=null) {
            ownerSapId=signedInMember.getSap();
            ownerName=signedInMember.getName();
        }
        else if(trialMember!=null) {
            ownerSapId=trialMember.getSap();
            ownerName=trialMember.getName();

            System.out.println("trial memeber : "+ownerSapId);
            System.out.println("trial member : "+ownerName);
        }
        args.putString(getString(R.string.post_owner_id_key),ownerSapId);
        args.putString(getString(R.string.post_owner_name_key),ownerName);
        imageUploadFragment.setArguments(args);

        FragmentTransaction ft=((HomeActivity)getContext()).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout,imageUploadFragment,getString(R.string.fragment_tag_image_upload));
        ft.commit();
    }



    @Override
    public void onLoadMore() {
        System.out.println("on load more");
        recyclerViewAdapter.setLoading(true);//keep this above the addPost
        recyclerViewAdapter.addPost(null);//place holder for the progress bar


        Calendar c=Calendar.getInstance();
        c.add(Calendar.MONTH,monthCount);

        homePageClient.getPosts("Y"+c.get(Calendar.YEAR),"M"+c.get(Calendar.MONTH))
                .enqueue(this);
    }


    @Override
    public void onSignedInMemberStateChange(@NonNull Member signedInMember) {
        System.out.println("postfragment onSignedInMemberStateChange : "+signedInMember);
        this.signedInMember=signedInMember;
        recyclerViewAdapter.setSignedInMember(signedInMember);
    }

    @Override
    public void onMemberLogout() {
        System.out.println("postfragment onMemberLogout : ");
        this.signedInMember=null;
        recyclerViewAdapter.setSignedInMember(null);
    }

    @Override
    public void onTrialMemberStateChange(TrialMember trialMember) {
        System.out.println("post fragment on google sign in callback called"+trialMember);
        this.trialMember=trialMember;
        recyclerViewAdapter.setTrialMember(trialMember);
    }

    @Override
    public void onGoogleSignOut() {
        System.out.println("post fragment on google sign out");
        this.trialMember=null;
        recyclerViewAdapter.setTrialMember(null);
    }
    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap=Bitmap.createBitmap(scaledBitmap,0,0,scaledBitmap.getWidth(),scaledBitmap.getHeight());
//
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContext().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

}
