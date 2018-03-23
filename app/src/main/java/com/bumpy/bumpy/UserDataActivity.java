package com.bumpy.bumpy;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

public class UserDataActivity extends BaseBumpyActivity {

    public static final String USER_NAME = "com.bumpy.bumpy.UserDataActivity.USER_NAME";
    public static String name;
    public static String insuranceNum;
    public static String carNum;
    public static String driverID;
    public static String licenseID;

    public static void UpdateDataFromServer()
    {
        name = FBLoginActivity.first_name + " " + FBLoginActivity.last_name;
        insuranceNum = "1234";
        carNum = "2222";
        driverID = "dpofpsd";
        licenseID = "34342";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_data);
        super.onCreate(savedInstanceState);
        super.initToolbar();

        // TODO: Download the information from the server
        UpdateDataFromServer();
        String username = FBLoginActivity.first_name + " " + FBLoginActivity.last_name;
        EditText name = findViewById(R.id.driverName);
        name.setText(username);
        name.setFocusable(false);
    }

    public void confirm(View view) {
        EditText etName = findViewById(R.id.driverName);
        EditText etInsurance = findViewById(R.id.insuranceNum);
        EditText etCarNum = findViewById(R.id.carNum);
        EditText etDriverID = findViewById(R.id.driverID);
        EditText etLicenseID = findViewById(R.id.driverLicense);

        JSONObject postparams = null;
        try {
            postparams = new JSONObject()
                    .put("name", etName.getText())
                    .put("car_number", etInsurance.getText())
                    .put("car_insurance", etCarNum.getText())
                    .put("license_id", etLicenseID.getText())
                    .put("user_personal_id", etDriverID.getText());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Communication.SendData(getApplicationContext(), "/v1/user", postparams, new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d("UserDataActivity", "RESO about the user: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("UserDataActivity", "ERROR: " + error.toString());
                        //Failure Callback

                    }});

        Intent intent = new Intent(this, FBLoginActivity.class);
        startActivity(intent);
    }
}
