package com.bumpy.bumpy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ShowAccident extends BaseBumpyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_accident);
        super.initToolbar();

        Accident accident = ViewAccidentsActivity.currAccident;
        EditText date = (EditText) findViewById(R.id.datetime);
        EditText dName = (EditText) findViewById(R.id.driverName);
        EditText dID = (EditText) findViewById(R.id.driverID);
        EditText cNum = (EditText) findViewById(R.id.carNum);
        EditText insuNum = (EditText) findViewById(R.id.insuranceNum);
        EditText driverLicense = (EditText) findViewById(R.id.driverLicense);

        date.setText(accident.localDateTime.toString());
        dName.setText(accident.driverData.driverName.toString());
        dID.setText(accident.driverData.driverId);
        cNum.setText(accident.driverData.carNumber);
        insuNum.setText(accident.driverData.insuranceNum);
        driverLicense.setText(accident.driverData.driverLicenseNum);

        final LinearLayout linLay = (LinearLayout)findViewById(R.id.linear_layout);

        //add image to imageview
        for (String num : accident.images) {
            final ImageView image = new ImageView(this);
            Log.d("found image", num);
            mStorageRef.child(num).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL
                    Log.d("the url", uri.toString());
                    Glide.with(getApplicationContext()).load(uri).into(image);
                    image.getLayoutParams().height = 2000;
                    image.getLayoutParams().width = 1400;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            linLay.addView(image);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShowAccident.this, ViewAccidentsActivity.class);
        startActivity(intent);
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("shit", "Error getting bitmap", e);
        }
        return bm;
    }
}
