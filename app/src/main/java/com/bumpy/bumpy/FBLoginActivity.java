package com.bumpy.bumpy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class FBLoginActivity extends BaseBumpyActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    private ProfileTracker mProfileTracker;
    public static String first_name = "";
    public static String last_name = "";
    ProgressDialog mProgDial;
    LoginResult mLresult;
    public static String TAG = "FBLoginActivity";

    public boolean isLoggedInToFacebook() {
        return mAuth.getCurrentUser() != null;
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("FBLoginActivity", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(FBLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.setApplicationId("151468522166990");
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fblogin);
        super.initToolbar();

        mProgDial = new ProgressDialog(FBLoginActivity.this,
                R.style.Theme_AppCompat_Dialog);
        mProgDial.setIndeterminate(true);
        mProgDial.setMessage("Authenticating...");

        if (isLoggedInToFacebook()) {
            mProgDial.show();
            handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
//            first_name = Profile.getCurrentProfile().getFirstName();
//            last_name = Profile.getCurrentProfile().getLastName();
//            startActivity(new Intent(FBLoginActivity.this, MainActivity.class));
        }

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mProgDial.show();
                mLresult = loginResult;
                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            Log.v(TAG, currentProfile.getFirstName());
                            mProfileTracker.stopTracking();
                            handleFacebookAccessToken(mLresult.getAccessToken());
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v(TAG, profile.getFirstName());
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }
            }


            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Facebook login error: " + error.toString());
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

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            // Make sure the user is disconnected
            // Alert the user about it
//            new android.os.Handler().postDelayed(
//                    new Runnable() {
//                        public void run() {
//                                new AlertDialog.Builder(FBLoginActivity.this, R.style.Theme_AppCompat_Dialog)
//                                        .setMessage("Failed to login\r\n")
//                                        .setCancelable(false)
//                                        .setPositiveButton("Return", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//                                                FBLoginActivity.this.Logout();
//                                            }
//                                        })
//                                        .show();
//                    }}, 3000);
            this.Logout();
            return;
        }

        // Change the profile picture displayed
        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);
        AccessToken token = AccessToken.getCurrentAccessToken();
        profilePictureView.setProfileId(token.getUserId());
        FBLoginActivity.first_name = Profile.getCurrentProfile().getFirstName();
        FBLoginActivity.last_name = Profile.getCurrentProfile().getLastName();
        Log.d("FB Logging", "Successful");
        mProgDial.dismiss();

        startActivity(new Intent(FBLoginActivity.this, MainActivity.class));
    }
}