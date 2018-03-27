package com.bumpy.bumpy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserDataActivity extends BaseBumpyActivity {

    public static final String USER_NAME = "com.bumpy.bumpy.UserDataActivity.USER_NAME";
    public static String name;
    public static String insuranceNum;
    public static String carNum;
    public static String driverID;
    public static String licenseID;
    boolean got_response;

    public void UpdateDataFromServer() {
        name = FBLoginActivity.first_name + " " + FBLoginActivity.last_name;

        // Check if the user has already entered user info
        DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mAuth.getCurrentUser().getUid());

        // Attach a listener to read the data at our posts reference
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DriverData driverData = DriverData.CreateFromDB(dataSnapshot);

                EditText dID = (EditText) findViewById(R.id.driverID);
                EditText cNum = (EditText) findViewById(R.id.carNum);
                EditText insuNum = (EditText) findViewById(R.id.insuranceNum);
                EditText driverLicense = (EditText) findViewById(R.id.driverLicense);

                dID.setText(driverData.driverId);
                cNum.setText(driverData.carNumber);
                insuNum.setText(driverData.insuranceNum);
                driverLicense.setText(driverData.driverLicenseNum);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_data);
        super.onCreate(savedInstanceState);
        super.initToolbar();

        UpdateDataFromServer();
        String username = FBLoginActivity.first_name + " " + FBLoginActivity.last_name;
        EditText name = findViewById(R.id.driverName);
        name.setText(username);
        name.setFocusable(false);
    }

    public void Cancel(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to cancel?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(UserDataActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void Confirm(View view) {
        EditText dName = (EditText) findViewById(R.id.driverName);
        EditText dID = (EditText) findViewById(R.id.driverID);
        EditText cNum = (EditText) findViewById(R.id.carNum);
        EditText insuNum = (EditText) findViewById(R.id.insuranceNum);
        EditText driverLicense = (EditText) findViewById(R.id.driverLicense);
        if (dName.getText().length() <= 2)
        {
            Toast.makeText(getApplicationContext(),
                    "Please insert your name",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (dID.getText().length() <= 2)
        {
            Toast.makeText(getApplicationContext(),
                    "Please insert your identification number",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (cNum.getText().length() <= 2)
        {
            Toast.makeText(getApplicationContext(),
                    "Please insert your car number",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (insuNum.getText().length() <= 2)
        {
            Toast.makeText(getApplicationContext(),
                    "Please insert your insurance number",
                    Toast.LENGTH_LONG).show();
            return;
        }
        carNum = cNum.getText().toString();
        driverID = dID.getText().toString();
        name = dName.getText().toString();
        insuranceNum = insuNum.getText().toString();
        licenseID = driverLicense.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(UserDataActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        sendData();
        Log.i("UserDataActivity", "Update user info");

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (got_response) {
                            // On complete call either onLoginSuccess or onLoginFailed
                            Intent intent = new Intent(UserDataActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            new AlertDialog.Builder(UserDataActivity.this, R.style.Theme_AppCompat_Dialog)
                                    .setMessage("Failed to connect the server\r\n")
                                    .setCancelable(false)
                                    .setPositiveButton("Return Home", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(UserDataActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("Continue", null)
                                    .show();
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void sendData() {
        writeUserInfo(name, driverID, carNum, insuranceNum, licenseID);
    }

    private void writeUserInfo(String driverName, String driverId, String carNumber, String insuranceNum, String driverLicenseNum) {
        DriverData driverData = new DriverData(driverName, driverId, carNumber, insuranceNum, driverLicenseNum);
        Map<String, Object> userValues = driverData.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + mAuth.getCurrentUser().getUid(), userValues);

        mDatabase.updateChildren(childUpdates);
        got_response = true;
    }
}
