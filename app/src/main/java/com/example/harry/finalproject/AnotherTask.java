package com.example.harry.finalproject;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.lib.ReadJson;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Class that handle another API call using the results from the last API call.
 */
public class AnotherTask {
    public static final String TAG = "----Another task----";

    static class GetProductInfo extends AsyncTask<String, Integer, String> {
        private WeakReference<MainActivity> activityWeakReference;

        private RequestQueue requestQueue;

        private String NDBCode;

        GetProductInfo(final MainActivity context, final RequestQueue setRequestQueue) {
            activityWeakReference = new WeakReference<>(context);
            requestQueue = setRequestQueue;
        }

        @Override
        protected void onPreExecute() {
             super.onPreExecute();
             MainActivity activity =activityWeakReference.get();
             NDBCode = activity.NDBCode;
             if (NDBCode == "") {
                 return;
             }
             ProgressBar progressBar = activity.findViewById(R.id.progressBar);
             progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String afterDNB = "&type=f&api_key=eLDsea1EUSgu1EOIbm9xR15mOAtVWgo9gVFKMUWM";
            String beforeNDB = "https://api.nal.usda.gov/ndb/reports/?ndbno=";
            String url = beforeNDB + strings[0] + afterDNB;

            try {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                MainActivity activity = activityWeakReference.get();
                                activity.productName = ReadJson.getName(response.toString());
                                activity.nutVal = ReadJson.getCalPer100g(response.toString(),208);
                                activity.updateDetailList();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.w(TAG, error.toString());
                        MainActivity activity = activityWeakReference.get();
                        Toast.makeText(activity, "Something went wrong",
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
