package com.bumpy.bumpy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        if (mAuth.getCurrentUser() != null) {
            profilePictureView.setProfileId(token.getUserId());
            check_if_user_info_exists();
        }
        else
        {
            LoginManager.getInstance().logOut();
            Intent loginIntent = new Intent(this, FBLoginActivity.class);
            startActivity(loginIntent);
        }
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

    public void check_if_user_info_exists()
    {
        // Check if the user has already entered user info
        DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mAuth.getCurrentUser().getUid());

        // Attach a listener to read the data at our posts reference
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If the result is null, it means the user did not enter his info yet
                if ((String) dataSnapshot.child("driverId").getValue() == null)
                {
                    new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Dialog)
                            .setMessage("Before you continue, please insert user info\r\n")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(MainActivity.this, UserDataActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
