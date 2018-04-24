//package com.example.harry.finalproject;
//
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import android.view.View;
//import android.widget.ProgressBar;
//
//import com.android.volley.RequestQueue;
//
//import java.lang.ref.WeakReference;
//
//import static com.example.lib.ReadJson.getNDB;
//
//public class Tasks {
//
//    private static final String TAG = "TASK";
//
//    static class ProcessImageTask extends AsyncTask<Bitmap, Integer, Integer> {
//
//        private static final String API_KEY = "eLDsea1EUSgu1EOIbm9xR15mOAtVWgo9gVFKMUWM";
//
//        private static final String API_URL = "https://api.nal.usda.gov/ndb/reports/?";
//
//        private RequestQueue requestQueue;
//
//        private WeakReference<MainActivity> activityReference;
//
//        ProcessImageTask(final MainActivity context, final RequestQueue setRequestQueue) {
//            activityReference = new WeakReference<>(context);
//            requestQueue = setRequestQueue;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            MainActivity activity = activityReference.get();
//            if (activity == null || activity.isFinishing()) {
//                return;
//            }
//            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
//            progressBar.setVisibility(View.VISIBLE);
//        }
//
//        protected Integer doInBackground(final String upcCode) {
//            String ndbNo = getNDB(upcCode);
//        }
//
//
//
//    }
//
//
//}
