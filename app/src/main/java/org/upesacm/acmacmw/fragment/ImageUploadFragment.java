package org.upesacm.acmacmw.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Post;
import org.upesacm.acmacmw.retrofit.HomePageClient;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageUploadFragment extends Fragment implements
        View.OnClickListener,
        Callback<Post>,
        OnSuccessListener<UploadTask.TaskSnapshot>,
        OnFailureListener,
        OnProgressListener<UploadTask.TaskSnapshot>,
        OnPausedListener<UploadTask.TaskSnapshot>{

    public static final int UPLOAD_SUCCESSFUL=1;
    public static final int UPLOAD_FAILED=2;

    HomePageClient homePageClient;
    byte[] byteArray;
    Post post;

    EditText caption;
    FloatingActionButton upload;
    ProgressBar progressBar;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    UploadResultListener resultListener;
    TextView textViewUpload;
    public ImageUploadFragment() {
        // Required empty public constructor
    }

    public static ImageUploadFragment newInstance(HomePageClient homePageClient) {
        ImageUploadFragment fragment = new ImageUploadFragment();
        fragment.homePageClient=homePageClient;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof UploadResultListener) {
            resultListener=(UploadResultListener)context;
            super.onAttach(context);
        }
        else {
            throw new IllegalStateException(context.toString()+" must " +
                    "implement UploadResultListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_image_upload,container,false);

        byteArray = getArguments().getByteArray("image_data");
        System.out.println("image data : "+byteArray);

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView imageView=view.findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);
        caption=view.findViewById(R.id.editText_caption);
        upload=view.findViewById(R.id.fab_upload);
        progressBar = view.findViewById(R.id.progress_bar_upload);
        textViewUpload = view.findViewById(R.id.text_view_upload);

        final Calendar calendar=Calendar.getInstance();

        upload.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        System.out.println("upload button clicked");
        showProgress(true);
        storageRef = storageRef.child(caption.getText().toString()+".png");
        UploadTask uploadTask =  storageRef.putBytes(byteArray);
        uploadTask.addOnSuccessListener(this)
                .addOnFailureListener(this)
                .addOnProgressListener(this)
                .addOnPausedListener(this);
    }

    public void showProgress(boolean show) {
        if(show) {
            caption.setVisibility(View.INVISIBLE);
            upload.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            textViewUpload.setVisibility(View.VISIBLE);
        } else {
            caption.setVisibility(View.VISIBLE);
            upload.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            textViewUpload.setVisibility((View.INVISIBLE));
        }
    }

    @Override
    public void onResponse(Call<Post> call, Response<Post> response) {
        System.out.println("Post Metadata saved successfully");
        Toast.makeText(getContext(),"New Post uploaded",Toast.LENGTH_LONG).show();
        resultListener.onUpload(this,ImageUploadFragment.UPLOAD_SUCCESSFUL);
    }

    @Override
    public void onFailure(Call<Post> call, Throwable t) {
        System.out.println("posting of meta data failed");
        Toast.makeText(getContext(),"Post upload failed",Toast.LENGTH_LONG).show();
        resultListener.onUpload(this,ImageUploadFragment.UPLOAD_FAILED);
    }


    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null) {
                    System.out.println("image url is : "+uri);
                    System.out.println("create the post object here");
                    post=new Post.Builder()
                            .setImageUrl(uri.toString())
                            .setCaption(caption.getText().toString())
                            .setMemberId("ACM1001")
                            .build();
                    Calendar calendar=Calendar.getInstance();
                    Call<Post> newPostCall= homePageClient.createPost("Y"+calendar.get(Calendar.YEAR),
                            "M"+calendar.get(Calendar.MONTH),
                            post.getMemberId()+Calendar.getInstance().getTimeInMillis(),
                            post);

                    newPostCall.enqueue(ImageUploadFragment.this);
                    System.out.println("initiated the post meta data upload process");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("failed to get the download uri");
            }
        });
    }

    @Override
    public void onFailure(@NonNull Exception e) {
            System.out.println("upload task failed");
            e.printStackTrace();
    }


    @Override
    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        progressBar.setIndeterminate(false);
        int progressPercent=(int)((100*taskSnapshot.getBytesTransferred())
                /(taskSnapshot.getTotalByteCount()));

        textViewUpload.setText("Uploading... "+progressPercent+"% complete");
        progressBar.setProgress(progressPercent);
    }

    @Override
    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
        progressBar.setIndeterminate(true);
    }

    public interface UploadResultListener {
        public void onUpload(ImageUploadFragment imageUploadFragment,int resultCode);
    }
}
