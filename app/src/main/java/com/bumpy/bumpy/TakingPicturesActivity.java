package com.bumpy.bumpy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class TakingPicturesActivity extends BaseBumpyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_taking_pictures);
        super.onCreate(savedInstanceState);
        super.initToolbar();
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
            BitmapDrawable drawable = new BitmapDrawable(getResources(), imageBitmap);
            img.setBackground(drawable);
        }
    }

    private String m_lastTag;
}
