package com.bumpy.bumpy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Ambulance extends CallActivity {
    public final static String AmbulanceNumber = "tel:0546500125";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ambulance);
        super.onCreate(savedInstanceState);
        super.initToolbar();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            MakeACall(AmbulanceNumber);
        }
    }

    public void Yes(View view) {
        if (ActivityCompat.checkSelfPermission(Ambulance.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Ambulance.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    1);
            return;
        }
        else {
            MakeACall(AmbulanceNumber);
        }
    }

    public void No(View view) {
        Intent intent = new Intent(this, PoliceActivity.class);
        startActivity(intent);
    }
}
