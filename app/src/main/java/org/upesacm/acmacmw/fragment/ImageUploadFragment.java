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

import com.google.android.gms.tasks.OnCanceledListener;
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
        OnPausedListener<UploadTask.TaskSnapshot>,
        OnCanceledListener{

    public static final int UPLOAD_SUCCESSFUL=1;
    public static final int UPLOAD_CANCELLED=2;
    public static final int UPLOAD_CANCEL_OPERATION_FAILED=4;
    public static final int UPLOAD_FAILED=3;

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
    String memberId;
    String yearId;
    String monthId;
    String postId;
    String day;
    String time;
    UploadTask uploadTask;
    public ImageUploadFragment() {
        // Required empty public constructor
    }

    public static ImageUploadFragment newInstance(HomePageClient homePageClient,String memberId) {
        ImageUploadFragment fragment = new ImageUploadFragment();
        fragment.homePageClient=homePageClient;
        fragment.memberId=memberId;

        System.out.println("fragment constructor : "+memberId);
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

        upload.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        System.out.println("on destroy view image upload frag");
        System.out.println("upload task : "+uploadTask);
        if(uploadTask!=null && !uploadTask.isComplete())
            if(!uploadTask.cancel())
                resultListener.onUpload(this,UPLOAD_CANCEL_OPERATION_FAILED);
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        System.out.println("upload button clicked");
        showProgress(true);
        Calendar calendar=Calendar.getInstance();
        yearId="Y"+calendar.get(Calendar.YEAR);
        monthId="M"+calendar.get(Calendar.MONTH);
        postId="ACM"+Calendar.getInstance().getTimeInMillis()+memberId.substring(3,memberId.length());
        day=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        String hour=String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        hour=(hour.length()==1)?("0"+hour):hour;
        String minute=String.valueOf(calendar.get(Calendar.MINUTE));
        minute = (minute.length()==1)?("0"+minute):minute;
        time=hour+":"+minute;

        System.out.println("postId : "+postId);

        storageRef = storageRef.child(memberId+"/"+postId+".png");
        uploadTask =  storageRef.putBytes(byteArray);
        uploadTask.addOnSuccessListener(this)
                .addOnFailureListener(this)
                .addOnProgressListener(this)
                .addOnPausedListener(this)
                .addOnCanceledListener(this);
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
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null) {
                    System.out.println("image url is : "+uri);
                    System.out.println("create the post object here");
                    post=new Post.Builder()
                            .setYearId(yearId)
                            .setMonthId(monthId)
                            .setPostId(postId)
                            .setImageUrl(uri.toString())
                            .setCaption(caption.getText().toString())
                            .setDay(day)
                            .setTime(time)
                            .setMemberId(memberId)
                            .build();
                    Call<Post> newPostCall= homePageClient.createPost(post.getYearId(),
                            post.getMonthId(),
                            post.getPostId(),
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

    @Override
    public void onCanceled() {
        System.out.println("on cancelled called");
        resultListener.onUpload(this,UPLOAD_CANCELLED);
    }


    @Override
    public void onResponse(Call<Post> call, Response<Post> response) {
        System.out.println("post save response : "+response.message());
        System.out.println("Post Metadata saved successfully");
        resultListener.onUpload(this,ImageUploadFragment.UPLOAD_SUCCESSFUL);
    }

    @Override
    public void onFailure(Call<Post> call, Throwable t) {
        t.printStackTrace();
        System.out.println("posting of meta data failed");
        resultListener.onUpload(this,ImageUploadFragment.UPLOAD_FAILED);
    }

    public interface UploadResultListener {
        void onUpload(ImageUploadFragment imageUploadFragment,int resultCode);
    }
}
