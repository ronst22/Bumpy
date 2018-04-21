package com.bumpy.bumpy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class TakingPicturesActivity extends BaseBumpyActivity {
    private DatabaseReference mAccidentReference;
    private ValueEventListener accidentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_taking_pictures);
        super.onCreate(savedInstanceState);
        super.initToolbar();

        String key= getIntent().getStringExtra("accident_id");
        Log.d("Taking pic", "shit 123");

        mAccidentReference = FirebaseDatabase.getInstance().getReference()
                .child("user-accidents").child(mAuth.getCurrentUser().getUid()).child(key).child("images");
        Log.d("Taking pic", "shit 321");

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void TakeImage(View view) {

        // Save the tag for later use
        m_lastTag = view.getTag().toString();

        // Take the picture
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Get the wanted imageButton
        ImageButton img = (ImageButton)this.findViewById(android.R.id.content).findViewWithTag(m_lastTag);

        // Get the picture
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Rotate the image (I dont know why its rotated in the first place)
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth(),
                                                            imageBitmap.getHeight(),true);
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(),
                                                       scaledBitmap .getHeight(), matrix, true);

            // Put the image in the button
            BitmapDrawable drawable = new BitmapDrawable(getResources(), rotatedBitmap);
            img.setBackground(drawable);

            // Create a reference to the file name
            final Long time = System.currentTimeMillis();
            StorageReference storageImageRef = mStorageRef.child("" + time);

            // Store the file in the storage
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rotatedBitmap .compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data2 = baos.toByteArray();

            UploadTask uploadTask = storageImageRef.putBytes(data2);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });

            // Save the file info in the db
            accidentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String,Object> accident_map = (Map<String,Object>) dataSnapshot.getValue();

                    // If this is the first pic
                    if (accident_map == null)
                    {
                        accident_map = new HashMap<String,Object>();
                    }
                    accident_map.put(time.toString(), time.toString());
                    mAccidentReference.updateChildren(accident_map);
                }

//            listView.setOnItemClickListener(this);

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };

            mAccidentReference.addValueEventListener(accidentListener);
        }
    }

    public void Finish(View view) {
        Intent intent = new Intent(this, ViewAccidentsActivity.class);
        startActivity(intent);
    }

    private String m_lastTag;
}
