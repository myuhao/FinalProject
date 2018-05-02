package com.example.harry.finalproject;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.lib.ReadJson;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static com.example.lib.ReadJson.getNDB;

/**
 * Class that handle the first API call to search the product.
 */
public class Tasks {

    private static final String TAG = "--------TASK---------";

    static class APICalling extends AsyncTask<String, Integer, String> {

        private WeakReference<MainActivity> activityWeakReference;

        private RequestQueue requestQueue;

        private String upcCode;

        APICalling(final MainActivity context, final RequestQueue setRequestQueue) {
            activityWeakReference = new WeakReference<>(context);
            requestQueue = setRequestQueue;
        }

        //Set up the progress bar first, and set the UPC code.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = activityWeakReference.get();
            upcCode = activity.upcCode;
            if (upcCode == null) {
                return;
            }
            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String beforeUPC = "https://api.nal.usda.gov/ndb/search/?format=json&q=";
            String afterUPC = "&sort=n&max=25&offset=0&api_key=eLDsea1EUSgu1EOIbm9xR15mOAtVWgo9gVFKMUWM";
            String url = beforeUPC + strings[0] + afterUPC;
            try {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                Log.d(TAG, response.toString());
                                MainActivity activity = activityWeakReference.get();
                                activity.NDBCode = ReadJson.getNDB(response.toString());
                                activity.startGetProductInfo();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.w(TAG, error.toString());
                        MainActivity activity = activityWeakReference.get();
                        Toast.makeText(activity, "We cannot find this product in " +
                                        "USDA database, sorry!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }

}
