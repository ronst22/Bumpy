package com.bumpy.bumpy;

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
    boolean called_ambulance;
    boolean called_police;
    public DriverData driverData;

    public Accident() {
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
        result.putAll(this.driverData.toMap());

        return result;
    }

    public String toString()
    {
        return localDateTime.toString();
    }
}
