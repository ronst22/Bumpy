package com.bumpy.bumpy;

import android.app.DownloadManager;
import android.os.Looper;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ronst on 12/29/2017.
 */

public class SessionData {
    public static String driverName;
    public static String driverId;
    public static String carNumber;
    public static String insuranceNum;

    public static void sendData(DynamicActivity dya)
    {
        JSONObject postparams = null;
        try {
            postparams = new JSONObject()
                    .put("user_name", "ron")
                    .put("with_ambulance", false)
                    .put("with_police", false)
                    .put("other_name", driverName)
                    .put("other_personal_id", driverId)
                    .put("other_car_number", carNumber)
                    .put("other_car_insurance", insuranceNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(dya);
        String url ="http://192.168.1.39:65432/v1/accident";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, postparams,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        System.out.println("RESO: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERROR: " + error.toString());
                        //Failure Callback

                    }
                });

        queue.add(jsonObjReq);

// Adding the request to the queue along with a unique string tag
//        .getInstance().addToRequestQueue(jsonObjectReq,"postRequest");

//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(dya);
//        String url ="http://192.168.1.39:65432/v1/accident";
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//                        // response
//                        System.out.println("Response: " + response.toString());
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // error
//                        System.out.println("Error.Response: " + error.toString());
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String>  params = new HashMap<String, String>();
//                    params.put("user_name", "ron");
//                    params.put("with_ambulance", "false");
//                    params.put("with_police", "false");
//                    params.put("other_name", driverName);
//                    params.put("other_personal_id", driverId);
//                    params.put("other_car_number", carNumber);
//                    params.put("other_car_insurance", insuranceNum);
//
//                return params;
//            }
//        };
//        queue.add(postRequest);
    }
}
