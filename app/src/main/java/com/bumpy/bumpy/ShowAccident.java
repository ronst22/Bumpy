package com.bumpy.bumpy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShowAccident.this, ViewAccidentsActivity.class);
        startActivity(intent);
    }
}
