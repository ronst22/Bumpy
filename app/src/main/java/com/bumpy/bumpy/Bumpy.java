package com.bumpy.bumpy;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ronst on 3/23/2018.
 */

public class Bumpy extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Make the database persistent so any change to it stays even if the user is offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Log.i("Bumpy", "Set DataBase persistence");
    }
}
