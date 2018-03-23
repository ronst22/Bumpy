package com.bumpy.bumpy;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ronst on 3/23/2018.
 */

@IgnoreExtraProperties
public class DriverData {
    public String driverName;
    public String driverId;
    public String carNumber;
    public String insuranceNum;
    public String driverLicenseNum;

    public DriverData() {
    }

    public DriverData(String driverName, String driverId, String carNumber, String insuranceNum, String driverLicenseNum) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.carNumber = carNumber;
        this.insuranceNum = insuranceNum;
        this.driverLicenseNum = driverLicenseNum;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("driverName", driverName);
        result.put("driverId", driverId);
        result.put("carNumber", carNumber);
        result.put("insuranceNum", insuranceNum);
        result.put("driverLicenseNum", driverLicenseNum);

        return result;
    }

    public String toString()
    {
        return driverName + " | " + driverId + " | " + carNumber + " | " + insuranceNum + " | " + driverLicenseNum;
    }

}
