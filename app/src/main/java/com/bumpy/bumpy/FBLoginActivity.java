package com.bumpy.bumpy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

public class FBLoginActivity extends BaseBumpyActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    private ProfileTracker mProfileTracker;
    public static String first_name = "";
    public static String last_name = "";

    public boolean isLoggedInToFacebook() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.setApplicationId("151468522166990");
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fblogin);
        super.initToolbar();

        if (isLoggedInToFacebook())
        {
            startActivity(new Intent(FBLoginActivity.this, MainActivity.class));
        }

        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Change the profile picture displayed
                ProfilePictureView profilePictureView;
                profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);
                profilePictureView.setProfileId(loginResult.getAccessToken().getUserId());

                Log.d("FB Logging", "Successful");

                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            Log.v("facebook - profile", currentProfile.getFirstName());
                            UpdateUser(currentProfile.getFirstName() + currentProfile.getLastName());
                            mProfileTracker.stopTracking();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getFirstName());
                    first_name = profile.getFirstName();
                    last_name = profile.getLastName();
                    UpdateUser(first_name + last_name);
                }
                startActivity(new Intent(FBLoginActivity.this, MainActivity.class));
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            }
        });

        // listener for when user logs out from facebook
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    ProfilePictureView profilePictureView;
                    profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);
                    profilePictureView.setProfileId(null);
                }
            }
        };

        accessTokenTracker.startTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UpdateUser(final String name) {
    Communication.GetData(getApplicationContext(), "/v1/user/" + name, new Response.Listener<JSONObject>()
    {
        @Override
        public void onResponse(JSONObject response) {
            // display response
            try {

                JSONObject jsonObj = response.getJSONObject("result");
                Log.d("Server received name: ", jsonObj.getString("name"));

                // Check if the user is not already logged in
                if (!name.equals(jsonObj.getString("name"))) {
                    Log.d("Info ", "User received from server is NOT equal to facebook user");

                    // Go to user setting page
                    Intent intent = new Intent(FBLoginActivity.this, UserDataActivity.class);
                    intent.putExtra(UserDataActivity.USER_NAME, name);
                    startActivity(intent);
                }
            } catch (Exception e)
            {
                Log.d("ERROR", e.toString());
            }
            Log.d("Response", response.toString());
        }
    },
    new Response.ErrorListener()
    {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("Error.Response", error.toString());
        }
    });
    }
}
