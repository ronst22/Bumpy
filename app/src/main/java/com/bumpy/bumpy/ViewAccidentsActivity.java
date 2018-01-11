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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;

public class ViewAccidentsActivity extends BaseBumpyActivity {

    String[] datesArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_accidents);
        super.initToolbar();


        JSONObject json;
        try {
            json = new JSONObject("{result : [a, b, ladlf, lfo]}");
            JSONArray jsonDates = json.getJSONArray("result");
            datesArray = new String[jsonDates.length()];
            for (int i = 0; i < datesArray.length; i++) {
                datesArray[i] = jsonDates.optString(i);
            }

        } catch (JSONException e) {
            Log.d("ERROR", e.getMessage());
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.accident_item, datesArray);

        ListView listView = (ListView) findViewById(R.id.accidents);
        listView.setAdapter(adapter);
    }
}
