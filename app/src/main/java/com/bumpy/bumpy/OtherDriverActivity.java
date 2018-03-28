package com.bumpy.bumpy;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class OtherDriverActivity extends BaseBumpyActivity implements NfcAdapter.CreateNdefMessageCallback {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_other_driver);
        super.onCreate(savedInstanceState);
        super.initToolbar();
        context = this;
        got_response = false;

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
//            finish();
        }
        else if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
        }
        else {
            // Register callback
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            Toast.makeText(this, "Waiting for nfc", Toast.LENGTH_LONG).show();
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

        sendData();
        Log.i("OtherDriverActivity", "Send message of an accident");

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (got_response) {
                            // On complete call either onLoginSuccess or onLoginFailed
                            Intent intent = new Intent(OtherDriverActivity.this, ViewAccidentsActivity.class);
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
                }, 3000);
    }

    public static String driverName;
    public static String driverId;
    public static String carNumber;
    public static String insuranceNum;
    public static String driverLicenseNum;

    public void sendData() {
        writeAccident(Calendar.getInstance().getTime(), Ambulance.called_ambulance, PoliceActivity.called_police,
                      driverName, driverId, carNumber, insuranceNum, driverLicenseNum);
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
    
    private void writeAccident(Date localDateTime, boolean called_ambulance, boolean called_police,
                               String driverName, String driverId, String carNumber, String insuranceNum, String driverLicenseNum) {
        String key = mDatabase.child("accidents").push().getKey();
        Accident accident = new Accident(localDateTime, called_ambulance, called_police,
                                         new DriverData(driverName, driverId, carNumber, insuranceNum, driverLicenseNum));
        Map<String, Object> accidentValues = accident.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/accidents/" + key, accidentValues);
        childUpdates.put("/user-accidents/" + mAuth.getCurrentUser().getUid() + "/" + key, accidentValues);

        mDatabase.updateChildren(childUpdates);
        got_response = true;
    }

}