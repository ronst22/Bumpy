package com.bumpy.bumpy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class UserDataActivity extends BaseBumpyActivity {

    public static final String USER_NAME = "com.bumpy.bumpy.UserDataActivity.USER_NAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_data);
        super.onCreate(savedInstanceState);
        super.initToolbar();

        String username = getIntent().getStringExtra(USER_NAME);
        EditText name = findViewById(R.id.driverName);
        name.setText(username);
        name.setFocusable(false);
    }
}
