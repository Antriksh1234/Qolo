package com.example.qolo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {
    Button button1,button2,create;
    EditText editText;
    ImageView imageView;
    VideoView videoView;
    FirebaseFirestore firestore;
    String Message;
    String uuid= UUID.randomUUID().toString()+".jpg";
    FirebaseStorage storage;
    final private int Qolo_Image=9778;
    final private int Qolo_Video=9779;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        initialize();
        create=findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storage=FirebaseStorage.getInstance();
                final StorageReference storageReference=storage.getReference().child("images");
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] data=baos.toByteArray();
                UploadTask uploadTask=storageReference.child(uuid).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Upload Failed",Toast.LENGTH_SHORT);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url=storageReference.child(uuid).getDownloadUrl().toString();
                        Log.i("URL",url);
                        Toast.makeText(getApplicationContext(),"Upload Success",Toast.LENGTH_SHORT);
                        // Access a Cloud Firestore instance from your Activity
                        Map<String,String> image=new HashMap<>();
                        image.put("ImageUrl",url);
                        firestore.collection("Images").add(image).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getApplicationContext(),"Firestore Success",Toast.LENGTH_SHORT);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Firestore Failed",Toast.LENGTH_SHORT);
                            }
                        });
                    }
                });
            }
        });
    }
    public void initialize(){
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        imageView=findViewById(R.id.imageView);
        videoView=findViewById(R.id.videoView);
        videoView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        editText=findViewById(R.id.Message);
        firestore=FirebaseFirestore.getInstance();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults!=null && grantResults[0]== PackageManager.PERMISSION_GRANTED && requestCode==1){
            if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), Qolo_Image);
            }
        }
        if (grantResults!=null && grantResults[0]== PackageManager.PERMISSION_GRANTED && requestCode==2){
            if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI), Qolo_Video);
            }
        }
    }

    public void image(View view){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), Qolo_Image);
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }
    public void video(View view){

        if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI), Qolo_Video);
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Qolo_Image :
                    getImage(data);
                break;
            case Qolo_Video :
                    getVideo(data);
                break;
            default:
                break;
        }
    }
    public void getImage(Intent data){
        imageView.setVisibility(View.VISIBLE);
        Uri uri=data.getData();
        Bitmap bitmap;
        try {
            bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            imageView.setImageBitmap(bitmap);
            button2.setEnabled(false);
            button1.setEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getVideo(Intent data){
        videoView.setVisibility(View.VISIBLE);
        Uri uri=data.getData();
        videoView.setVideoURI(uri);
        MediaController mediaController=new MediaController(getApplicationContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        button1.setEnabled(false);
        button2.setEnabled(false);
    }
}