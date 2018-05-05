package com.bumpy.bumpy;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import android.Manifest;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.Driver;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OtherDriverActivity extends BaseBumpyActivity implements NfcAdapter.CreateNdefMessageCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;
    boolean got_response;
    NfcAdapter mNfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    TextView textView;
    private DatabaseReference mUserData;
    private ValueEventListener mUserDataListener;
    public static String TAG = "OtherDriverActivity";
    String mKey = null;
    private GoogleApiClient mGoogleApiClient;
    private LatLng location;
    public final int MY_REQ_CODE = 12345;
    private boolean got_location = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_other_driver);
        super.onCreate(savedInstanceState);
        super.initToolbar();
        context = this;
        got_response = false;
        location = new LatLng(0, 0);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
//            finish();
        }
        else if (!mNfcAdapter.isEnabled()) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setTitle("Info");
            alertbox.setMessage("Enable NFC");
            alertbox.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                }
            });
            alertbox.setNegativeButton("Close", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertbox.show();
//            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
        }
        else {
            // Register callback
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            Toast.makeText(this, "NFC Ready", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Log.d(TAG, "NFC Message transferring");
        String text = mAuth.getCurrentUser().getUid();
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { NdefRecord.createMime(
                        "plain/text", text.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                         *
                        */
//                        ,NdefRecord.createApplicationRecord("com.bumpy.bumpy.OtherDriverActivity")
                });

        return msg;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "OnResume of nfc message");
        super.onResume();

        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d(TAG, "Setting new intent after onResume");
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Log.d(TAG, "Recieved data from nfc!!!!!!!!!!!!!!!!!!!");
//        textView = (TextView) findViewById(R.id.textView);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String data = new String(msg.getRecords()[0].getPayload());

        mUserData = FirebaseDatabase.getInstance().getReference()
                .child("users").child(data);
        Log.d(TAG, "The user id is: " + data);

        mUserDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                DriverData driverData = dataSnapshot.getValue(DriverData.class);
                EditText dName = (EditText) findViewById(R.id.driverName);
                EditText dID = (EditText) findViewById(R.id.driverID);
                EditText cNum = (EditText) findViewById(R.id.carNum);
                EditText insuNum = (EditText) findViewById(R.id.insuranceNum);
                EditText driverLicense = (EditText) findViewById(R.id.driverLicense);

                dName.setText(driverData.driverName);
                dID.setText(driverData.driverId);
                cNum.setText(driverData.carNumber);
                insuNum.setText(driverData.insuranceNum);
                driverLicense.setText(driverData.driverLicenseNum);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mUserData.addValueEventListener(mUserDataListener);
//        textView.setText(new String(msg.getRecords()[0].getPayload()));
    }

    public void Cancel(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to cancel?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(OtherDriverActivity.this, MainActivity.class);
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
                    "Please insert the other driver name",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (dID.getText().length() <= 2)
        {
            Toast.makeText(getApplicationContext(),
                    "Please insert the other driver identification number",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (cNum.getText().length() <= 2)
        {
            Toast.makeText(getApplicationContext(),
                    "Please insert the other driver car number",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (insuNum.getText().length() <= 2)
        {
            Toast.makeText(getApplicationContext(),
                    "Please insert the other driver insurance number",
                    Toast.LENGTH_LONG).show();
            return;
        }
        carNumber = cNum.getText().toString();
        driverId = dID.getText().toString();
        driverName = dName.getText().toString();
        insuranceNum = insuNum.getText().toString();
        driverLicenseNum = driverLicense.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(OtherDriverActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        sendData();
        Log.i("OtherDriverActivity", "Send message of an accident");

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (got_response) {
                            // On complete call either onLoginSuccess or onLoginFailed
                            Intent intent = new Intent(OtherDriverActivity.this, TakingPicturesActivity.class);
                            intent.putExtra("accident_id", mKey);
                            startActivity(intent);
                        }
                        else {
                            new AlertDialog.Builder(OtherDriverActivity.this, R.style.Theme_AppCompat_Dialog)
                                    .setMessage("Failed to connect the server\r\n")
                                    .setCancelable(false)
                                    .setPositiveButton("Return Home", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(OtherDriverActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("Continue", null)
                                    .show();
                        }
                        progressDialog.dismiss();
                    }
                }, 5000);
    }

    public static String driverName;
    public static String driverId;
    public static String carNumber;
    public static String insuranceNum;
    public static String driverLicenseNum;

    public void sendData() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (got_location) {
                            writeAccident(Calendar.getInstance().getTime(), Ambulance.called_ambulance,
                                    PoliceActivity.called_police, location, driverName, driverId,
                                    carNumber, insuranceNum, driverLicenseNum);
                        }
                        else {
                            Log.d(TAG, "No location available");
                            writeAccident(Calendar.getInstance().getTime(), Ambulance.called_ambulance,
                                    PoliceActivity.called_police, location, driverName, driverId,
                                    carNumber, insuranceNum, driverLicenseNum);
                        }
                    }
                }, 5000);
//
//        JSONObject postparams = null;
//        try {
//            postparams = new JSONObject()
//                    .put("user_name", FBLoginActivity.first_name + FBLoginActivity.last_name)
//                    .put("with_ambulance", Ambulance.called_ambulance)
//                    .put("with_police", PoliceActivity.called_police)
//                    .put("other_name", driverName)
//                    .put("other_personal_id", driverId)
//                    .put("other_car_number", carNumber)
//                    .put("other_car_insurance", insuranceNum)
//                    .put("other_driver_license", driverLicenseNum);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.d("OtherDriverActivity", "Sending: " + postparams.toString());
//        Communication.SendData(getApplicationContext(), "/v1/accident", postparams, new Response.Listener() {
//                    @Override
//                    public void onResponse(Object response) {
//                        got_response = true;
//                        Log.d("OtherDriverActivity", "RESO: " + response.toString());
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        got_response = false;
//                        Log.d("OtherDriverActivity", "Error: " + error.toString());
//                        //Failure Callback
//
//                    }});
    }

//    protected void onResume(){
//        super.onResume();
////    Intent intent = getIntent();
////    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
////        Parcelable[] rawMessages = intent.getParcelableArrayExtra(
////                NfcAdapter.EXTRA_NDEF_MESSAGES);
////
////        NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
////        Toast.makeText(this, new String(message.getRecords()[0].getPayload()), Toast.LENGTH_LONG).show();
////
////    } else
////        Toast.makeText(this, "Waiting for NDEF Message", Toast.LENGTH_LONG).show();
//
//    }

    @Override
    public void onConnectionSuspended (int cause)
    {

    }

    @Override
    public void onConnectionFailed (ConnectionResult cause)
    {

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnection called waiting for location");
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No Permission");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                Log.d(TAG, "check if can ask for permision");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_REQ_CODE);

                Log.d(TAG, "Asking for permission");

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.d(TAG, "Getting the location!!!!!");
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.d(TAG, "Get last location: " + mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
                location = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                got_location = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_REQ_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                location = new LatLng(0, 0);
            }
        }
    }

    private void writeAccident(Date localDateTime, boolean called_ambulance, boolean called_police, LatLng location,
                               String driverName, String driverId, String carNumber, String insuranceNum, String driverLicenseNum) {
        mKey = mDatabase.child("accidents").push().getKey();
        Accident accident = new Accident(localDateTime, called_ambulance, called_police, location,
                                         new DriverData(driverName, driverId, carNumber, insuranceNum, driverLicenseNum));
        Map<String, Object> accidentValues = accident.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/accidents/" + mKey, accidentValues);
        childUpdates.put("/user-accidents/" + mAuth.getCurrentUser().getUid() + "/" + mKey, accidentValues);

        mDatabase.updateChildren(childUpdates);
        got_response = true;
    }

}