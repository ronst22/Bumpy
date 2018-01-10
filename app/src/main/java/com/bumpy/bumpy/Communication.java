package com.bumpy.bumpy;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by ronst on 1/10/2018.
 */

public class Communication {
    public final static int MY_SOCKET_TIMEOUT_MS = 5000;
    public static String URL = "http://192.168.1.56:65432";

    public static void SendData(final Context context, String url_ext, JSONObject data, Response.Listener<JSONObject> onResp, Response.ErrorListener onError)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = URL + url_ext;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, data, onResp, onError);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjReq);
    }

    public static void GetData(final Context context, String url_ext, Response.Listener<JSONObject> onResp, Response.ErrorListener onError)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = URL + url_ext;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, onResp, onError);
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(getRequest);
    }
}
