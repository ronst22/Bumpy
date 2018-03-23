package com.bumpy.bumpy;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class ViewAccidentsActivity extends BaseBumpyActivity implements AdapterView.OnItemClickListener {

    String[] datesArray;
    private DatabaseReference mAccidentReference;
    private ValueEventListener accidentListener;
    private ArrayList<Accident> accidentArray;
    private String TAG = "ViewAccidentActivity";
    private ListView listView;
    public static Accident currAccident;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_accidents);
        super.initToolbar();

        mAccidentReference = FirebaseDatabase.getInstance().getReference()
                .child("user-accidents").child(mAuth.getCurrentUser().getUid());
        Log.d(TAG, "The user id is: " + mAuth.getCurrentUser().getUid());

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

        listView = (ListView) findViewById(R.id.accidents);
        listView.setOnItemClickListener(this);
        accidentArray = new ArrayList<>();
        accidentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Accident accident = Accident.CreateFromDB(childDataSnapshot);
                    Log.d(TAG, "Number of values from the db " + dataSnapshot.getChildrenCount());
                    Log.d(TAG, "Accident ambulance " + accident.called_ambulance);
                    Log.d(TAG, "Accident driver data " + accident.driverData);
                    Log.d(TAG, "The accident is: " + accident);
                    accidentArray.add(accident);
                }

                ArrayAdapter adapter = new ArrayAdapter<>(ViewAccidentsActivity.this, R.layout.accident_item, accidentArray);

                listView.setAdapter(adapter);
            }

//            listView.setOnItemClickListener(this);

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mAccidentReference.addValueEventListener(accidentListener);

    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        // Then you start a new Activity via Intent
        Intent intent = new Intent();
        intent.setClass(this, ShowAccident.class);
        currAccident = (Accident) l.getItemAtPosition(position);
        // Or / And
        startActivity(intent);
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
