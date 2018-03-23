package com.bumpy.bumpy;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.sql.Driver;
import java.util.ArrayList;

public class ViewAccidentsActivity extends BaseBumpyActivity {

    String[] datesArray;
    private DatabaseReference mAccidentReference;
    private ValueEventListener accidentListener;
    private ArrayList<DriverData> driverDataArray;
    private String TAG = "ViewAccidentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_accidents);
        super.initToolbar();

        mAccidentReference = FirebaseDatabase.getInstance().getReference()
                .child("accidents").child(mAuth.getCurrentUser().getUid());

//
//        JSONObject json;
//        try {
//            json = new JSONObject("{\"result\" : [\"24/05/2017 10:57:13\", \"12/11/2017 14:12:40\", \"01/01/2018 19:40:40\"]}");
//            JSONArray jsonDates = json.getJSONArray("result");
//            datesArray = new String[jsonDates.length()];
//            for (int i = 0; i < datesArray.length; i++) {
//                datesArray[i] = jsonDates.optString(i);
//            }
//
//        } catch (JSONException e) {
//            Log.d("ERROR", e.getMessage());
//        }

//        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.accident_item, datesArray);
//
//        ListView listView = (ListView) findViewById(R.id.accidents);
//        listView.setAdapter(adapter);

        driverDataArray = new ArrayList<DriverData>();
        accidentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                DriverData driverData = dataSnapshot.getValue(DriverData.class);
                Log.d(TAG, "The driver data is: " + driverData);
                driverDataArray.add(driverData);
                ArrayAdapter adapter = new ArrayAdapter<DriverData>(ViewAccidentsActivity.this, R.layout.accident_item, driverDataArray);

                ListView listView = (ListView) findViewById(R.id.accidents);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mAccidentReference.addValueEventListener(accidentListener);

    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mAccidentReference != null) {
            mAccidentReference.removeEventListener(accidentListener);
        }
    }
}
