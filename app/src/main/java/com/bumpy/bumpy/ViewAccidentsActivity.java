package com.bumpy.bumpy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewAccidentsActivity extends AppCompatActivity {

    String[] stringArray = {"c", "b", "c"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_accidents);
        Toolbar myToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.accident_item, stringArray);

        ListView listView = (ListView) findViewById(R.id.accidents);
        listView.setAdapter(adapter);
    }
}
