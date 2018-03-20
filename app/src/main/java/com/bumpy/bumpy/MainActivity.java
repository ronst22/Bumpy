package com.bumpy.bumpy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends BaseBumpyActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.setApplicationId("151468522166990");
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.initToolbar();

        // If we got to this activity, it means we are logged in to facebook,
        // so the access token should exist
        AccessToken token = AccessToken.getCurrentAccessToken();

        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);
        profilePictureView.setProfileId(token.getUserId());
    }

    public void accident(View view) {
//        Intent intent = new Intent(this, DynamicActivity.class);
        Intent intent = new Intent(this, Ambulance.class);
        intent.putExtra(DynamicActivity.STATE, StatesFactory.STATES.AMBULANCE);
        startActivity(intent);
    }

    public void view_accidents(View view) {
//        Intent intent = new Intent(this, DynamicActivity.class);
        Intent intent = new Intent(this, ViewAccidentsActivity.class);
        startActivity(intent);
    }
}
