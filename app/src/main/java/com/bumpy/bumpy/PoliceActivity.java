package com.bumpy.bumpy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kofigyan.stateprogressbar.StateProgressBar;

public class PoliceActivity extends CallActivity {

    public final static String PoliceNumber = "tel:0508823116";
    public static boolean called_police = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_police);
        super.onCreate(savedInstanceState);
        super.initToolbar();

        called_police = false;

        overridePendingTransition(0, 0);

        String[] descriptionData = {"Ambulance", "Police"};
        StateProgressBar stateProgressBar = (StateProgressBar) findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            MakeACall(PoliceNumber );
        }
    }


    public void Yes(View view) {
        if (ActivityCompat.checkSelfPermission(PoliceActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PoliceActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    1);
        }
        else {
            MakeACall(PoliceNumber );
            called_police = true;
        }
    }

    public void No(View view) {
        Intent intent = new Intent(this, OtherDriverActivity.class);
        startActivity(intent);
    }
}
