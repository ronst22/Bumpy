package com.bumpy.bumpy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class DynamicActivity extends AppCompatActivity {
    public static final String STATE = "com.bumpy.bumpy.STATE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        Toolbar myToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(myToolbar);

        IState my_activity = StatesFactory.GetState((StatesFactory.STATES) getIntent().getSerializableExtra(STATE));

        final LinearLayout lm = (LinearLayout) findViewById(R.id.linear_layout);

        my_activity.InitActivity(this, lm, getResources());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
