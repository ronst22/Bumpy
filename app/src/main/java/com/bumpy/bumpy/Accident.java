package com.bumpy.bumpy;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    public ArrayList<String> images;
    public LatLng location;

    public Accident() {
        this.localDateTime = new Date();
        this.driverData = new DriverData();
        this.location = new LatLng(0,0);
    }

//    public Accident(Date localDateTime, boolean called_ambulance, boolean called_police, LatLng location, DriverData driverData) {
//        this.localDateTime = localDateTime;
//        this.called_ambulance = called_ambulance;
//        this.called_police = called_police;
//        this.driverData = driverData;
//        this.location = location;
//    }
//
//    public Accident(Date localDateTime, boolean called_ambulance, boolean called_police, DriverData driverData) {
//        this.localDateTime = localDateTime;
//        this.called_ambulance = called_ambulance;
//        this.called_police = called_police;
//        this.driverData = driverData;
//        this.location = new LatLng(0, 0);
//    }

    public Accident(Date localDateTime, boolean called_ambulance, boolean called_police, DriverData driverData, LatLng location) {
        this.localDateTime = localDateTime;
        this.called_ambulance = called_ambulance;
        this.called_police = called_police;
        this.driverData = driverData;
        this.location = location;
    }

    public Accident(Date localDateTime, boolean called_ambulance, boolean called_police, DriverData driverData, LatLng location, ArrayList<String> images) {
        this.localDateTime = localDateTime;
        this.called_ambulance = called_ambulance;
        this.called_police = called_police;
        this.driverData = driverData;
        this.location = location;
        this.images = new ArrayList<String>(images);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("datetime", localDateTime);
        result.put("called_ambulance", called_ambulance);
        result.put("called_police", called_police);
        result.put("latitude", this.location.latitude);
        result.put("longtitude", this.location.longitude);
        driverData.toMap(result);

        return result;
    }

    static Accident CreateFromDB(DataSnapshot snapshot) {
        Date date = new Date();
        HashMap<String, Long> map = (HashMap<String, Long>) snapshot.child("datetime").getValue();
        date.setTime(map.get("time"));
        HashMap<String, String> image_map = (HashMap<String, String>) snapshot.child("images").getValue();

        ArrayList<String> tmp_list = new ArrayList<String>();

        if (image_map != null) {
            for (String value : image_map.values()) {
                tmp_list.add(value);
            }
        }
        LatLng location = new LatLng(0.0, 0.0);
        if (snapshot.hasChild("latitude") && snapshot.hasChild("longtitude")) {
            try {
                location = new LatLng((Double) snapshot.child("latitude").getValue(), (Double) snapshot.child("longtitude").getValue());
            } catch(Exception msg) {
                location = new LatLng(0.0, 0.0);
            }
        }

        return new Accident(date,
                (boolean) snapshot.child("called_ambulance").getValue(),
                (boolean) snapshot.child("called_police").getValue(),
                    DriverData.CreateFromDB(snapshot),
                location,
                tmp_list);
    }

    public String toString() {
        return localDateTime.toString();
    }
}