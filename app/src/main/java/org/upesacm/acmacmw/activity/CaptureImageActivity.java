package org.upesacm.acmacmw.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.Post;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CaptureImageActivity extends AppCompatActivity {
EditText caption;
FloatingActionButton upload;
Uri url=null;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private SimpleDateFormat dateFormat;
    Post post;
    byte[] byteArray;
    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        byteArray = getIntent().getByteArrayExtra("data");
        post=new Post();
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView imageView=findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);
        caption=findViewById(R.id.caption);
        upload=findViewById(R.id.upload);
        dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        final Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);
        final ProgressDialog progressDialog=new ProgressDialog(this);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageRef=storageRef.child(caption.getText().toString()+".png");
                progressDialog.setMessage("Uploading...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(true);
                post.setCaption(caption.getText().toString());
                final UploadTask uploadTask = storageRef.putBytes(byteArray);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        StorageException storageException=(StorageException)e;
                        e.printStackTrace();
                        Toast.makeText(CaptureImageActivity.this, "Unable to upload image"+storageException.getHttpResultCode(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        url=taskSnapshot.getDownloadUrl();
                        if(url!=null)
                        {   myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                 count=(int)dataSnapshot.child("postCount").getValue();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                            post.setImageUrl(url.toString());
                            myRef.child("Posts").child("Y"+calendar.get(Calendar.YEAR)).child("M"+calendar.get(Calendar.MONTH)).child("post"+count).setValue(post);
                            count++;
                            myRef.child("postCount").setValue(count);
                            progressDialog.dismiss();
                            Intent intent=new Intent(CaptureImageActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }
}