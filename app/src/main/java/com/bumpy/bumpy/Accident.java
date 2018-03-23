package com.bumpy.bumpy;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ronst on 3/23/2018.
 */

@IgnoreExtraProperties
public class Accident {
    public Date localDateTime ;
    public boolean called_ambulance;
    public boolean called_police;
    public DriverData driverData;

    public Accident() {
        this.localDateTime = new Date();
        this.driverData = new DriverData();
    }

    public Accident(Date localDateTime, boolean called_ambulance, boolean called_police, DriverData driverData) {
        this.localDateTime = localDateTime;
        this.called_ambulance = called_ambulance;
        this.called_police = called_police;
        this.driverData = driverData;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("datetime", localDateTime);
        result.put("called_ambulance", called_ambulance);
        result.put("called_police", called_police);
        driverData.toMap(result);

        return result;
    }

    static Accident CreateFromDB(DataSnapshot snapshot) {
        Date date = new Date();
        HashMap<String, Long> map = (HashMap<String, Long>) snapshot.child("datetime").getValue();
        date.setTime(map.get("time"));
        return new Accident(date,
                (boolean) snapshot.child("called_ambulance").getValue(),
                (boolean) snapshot.child("called_police").getValue(),
                    DriverData.CreateFromDB(snapshot));
    }

    public String toString()
    {

        return localDateTime.toString();
    }
}
