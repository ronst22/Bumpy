package com.bumpy.bumpy;

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
        date.setText(accident.driverData.driverName.toString());
    }
}
