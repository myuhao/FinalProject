package com.example.harry.finalproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lib.ReadJson;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "---------------------Main Activity------------------";

    /** Constant to perform a read file request. */
    private static final int READ_REQUEST_CODE = 42;

    /** Constant to request an image capture. */
    private static final int IMAGE_CAPTURE_REQUEST_CODE = 1;

    /** Constant to request permission to write to the external storage device. */
    private static final int REQUEST_WRITE_STORAGE = 112;

    /** Can I write to the public storage? */
    private boolean canWriteToPublicStorage = false;

    //Picture of the UPC code to be looked up
    private Bitmap currentBitmap;

    //Copied from MP6 and LAB11
    private static RequestQueue requestQueue;

    //Global variable that represet the barcode from the bitmap object
    public String upcCode;

    private boolean requestFinished = false;

    private static Map list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        final Button goToAdded = (Button) findViewById(R.id.added);
        goToAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Added button clicked");
                goToAdded();
            }
        });

        final ImageButton takePicture = (ImageButton) findViewById(R.id.takePicture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "DO something with the take picture button");
                takePicture();
            }
        });

        final ImageButton openFile = (ImageButton) findViewById(R.id.loadFromLocal);
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "DO something with the take load button");
                getLocalPictures();
            }
        });

        final ImageButton startCall = (ImageButton) findViewById(R.id.startCalling);
        startCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "DO something with the start API calling button");
                sendAPIButton();
            }
        });

        final Button homeButton = (Button) findViewById(R.id.home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //readUPC(currentBitmap);
                Log.d(TAG, "Gloval vaiable upcCode is " + upcCode);
                Log.d(TAG, "THe name of the product is " + productName);
                Log.d(TAG, "The calories /100g is " + Integer.toString(nutVal));
            }
        });

        //First hide progress bar, made visibale later
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        //Hide the picture until readi to display.
        ImageView img = (ImageView) findViewById(R.id.ImageView);
        img.setVisibility(View.INVISIBLE);

        /*
         * Here we check for permission to write to external storage and request it if necessary.
         * Normally you would not want to do this on ever start, but we want to be persistent
         * since it makes development a lot easier.
         */
        canWriteToPublicStorage = (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        Log.d(TAG, "Do we have permission to write to external storage: "
                + canWriteToPublicStorage);
        if (!canWriteToPublicStorage) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

        list = new HashMap();
    }

    @Override
    public void onActivityResult(final int requestCode,  int resultCode,
                                  final Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG,"onActivityResult with code " + requestCode + " failed");
            if (resultCode == IMAGE_CAPTURE_REQUEST_CODE) {
                pictureRequestActive = false;
            }
            return;
        }

        Uri currentPictureURI;
        if (requestCode == READ_REQUEST_CODE) {
            currentPictureURI = data.getData();
        } else if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
            Log.d(TAG, "The picture is detected");
            currentPictureURI = Uri.fromFile(currenPictureFile);
            pictureRequestActive = false;
            if (canWriteToPublicStorage) {
                Log.w(TAG, "we can do something here");
            }
        } else {
            Log.d(TAG, "Unhandled activityResult with code " + requestCode);
            return;
        }

        // Now load the photo into the view
        Log.d(TAG, "Photo selection produced URI " + currentPictureURI);
        loadPhoto(currentPictureURI);
    }

    /**
     * Go to the added list view.
     */
    protected void goToAdded() {
        Intent intent = new Intent(this, addedActivity.class);
        startActivity(intent);
    }

    /** The picture that is being processed. */
    private File currenPictureFile = null;


    File getSaveFilename() {
        String imageFileName = "finalproject_" + new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.US).format(new Date());
        File storageDir;
        if (canWriteToPublicStorage) {
            storageDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        } else {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        try {
            return File.createTempFile(imageFileName, "jpg", storageDir);
        } catch (IOException e) {
            Log.w(TAG, "Problem saving files: " + e.toString());
            return null;
        }
    }

    /**
     * IS the camera being used?
     */
    private boolean pictureRequestActive = false;

    /**
     * Open the camera to take picture.
     * Copied from MP6
     */
    protected void takePicture() {
        if (pictureRequestActive) {
            Log.w(TAG, "Opening camera in progress");
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currenPictureFile = getSaveFilename();
        if (takePictureIntent.resolveActivity(getPackageManager()) == null
                || currenPictureFile == null) {
            Toast.makeText(getApplicationContext(), "Can't take picture",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Uri pictureURI = FileProvider.getUriForFile(this,
                "com.example.harry.finalproject.fileprovider", currenPictureFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureURI);
        pictureRequestActive = true;
        startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE);
    }

    /**
     * Get the picture.
     */
    protected void getLocalPictures() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /**
     * Get am img and convert it to Bitmap object for handling.
     * @param currentPictureURI - No idea, copied from MP6.
     */
    private void loadPhoto(final Uri currentPictureURI) {

        if (currentPictureURI == null) {
            Toast.makeText(getApplicationContext(), "No image seleted",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String uriScheme = currentPictureURI.getScheme();

        /**Code copied from MP6 to get image data.
         * Get the byte[] of imageData, whoch will be convered to bitmap
         */
        byte[] imageData;
        try {
            switch (uriScheme) {
                case "file":
                    imageData = FileUtils.readFileToByteArray(new File(currentPictureURI.getPath()));
                    break;
                case "content":
                    InputStream inputStream = getContentResolver().openInputStream(currentPictureURI);
                    assert inputStream != null;
                    imageData = IOUtils.toByteArray(inputStream);
                    inputStream.close();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Unknown scheme " + uriScheme,
                            Toast.LENGTH_LONG).show();
                    return;
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error processing file",
                    Toast.LENGTH_LONG).show();
            Log.w(TAG, "Error processing file: " + e);
            return;
        }

        currentBitmap = BitmapFactory.decodeByteArray(imageData, 0,imageData.length);

        updateCurrentBitmap(currentBitmap, true);
    }

    /**
     * Update the current bitmap object.
     * Also update the global UPC code variable using readUPC().
     * @param setBitmap - New bitmap to set.
     * @param restInfo - Not used. Copied from MP6.
     */
    void updateCurrentBitmap(final Bitmap setBitmap, final boolean restInfo) {
        currentBitmap = setBitmap;
        ImageView imgView = findViewById(R.id.ImageView);
        imgView.setImageBitmap(currentBitmap);
        imgView.setVisibility(View.VISIBLE);
        requestFinished = false;
        readUPC(currentBitmap);
        Log.d(TAG, "The upc code is " + readUPC(currentBitmap));

    }

    /**
     * Called when the upload button is pushed.
     */
    private void startAPICall() {
//        if (currentBitmap == null) {
//            return;
//        }

        //Use LAb 11 API call codes here to test
        requestNDBNumber(upcCode);
        //requestNutrientValues("45085820");
        Log.d(TAG, "Start API call finished");
    }


    public String NDBCode;

    /**
     * From the UPC code, serach database to get the NDB number.
     * Then call requestNutrientValues().
     * @param lookUPCCode - UPC code to look up.
     */
    protected void requestNDBNumber(final String lookUPCCode) {
        String beforeUPC = "https://api.nal.usda.gov/ndb/search/?format=json&q=";
        String afterUPC = "&sort=n&max=25&offset=0&api_key=eLDsea1EUSgu1EOIbm9xR15mOAtVWgo9gVFKMUWM";
        String url = beforeUPC + lookUPCCode + afterUPC;

        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.d(TAG, response.toString());
                            NDBCode = ReadJson.getNDB(response.toString());
                            Log.d(TAG, NDBCode);

                            Log.d(TAG, "Finished Searching for NDB code, starting" +
                                    "seach for food");
                            requestNutrientValues(NDBCode);
                            Log.d(TAG, "NDB request finished");


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, error.toString());
                    Toast.makeText(getApplicationContext(),
                            "Cannot find this product in the database, please try again",
                            Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Something went wrong",
                    Toast.LENGTH_LONG).show();
        }
    }

    public int nutVal = 0;
    public String productName = "";

    /**
     * Look up specific nutrient value per 100 g and store in nutVal.
     * Called during the NDB request.
     * @param NDBCode - The NDB code.
     */
    protected void requestNutrientValues(final String NDBCode) {

        String afterDNB = "&type=f&api_key=eLDsea1EUSgu1EOIbm9xR15mOAtVWgo9gVFKMUWM";
        String beforeNDB = "https://api.nal.usda.gov/ndb/reports/?ndbno=";
        String url = beforeNDB + NDBCode + afterDNB;

        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            productName = ReadJson.getName(response.toString());
                            nutVal = ReadJson.getCalPer100g(response.toString(),208);
                            Log.d(TAG, "Nutrient value found!");
                            requestFinished = true;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void showResults(final String jsonResult) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonResult);
        String prettyJsonString = gson.toJson(jsonElement);
        Log.d(TAG, "The response json String is: " + prettyJsonString);
    }

    /**
     * Read the bit map (whatever that is) of the UPC code and return the number as String.
     * @param bitmap The bitmap object of the pucture.
     * @return THe string of the nunber on the UPC.
     */
    protected String readUPC(Bitmap bitmap) {
        if (bitmap == null) {
            return "No input yet";
        }
        BarcodeDetector detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(0)
                        .build();
        if (!detector.isOperational()) {
            Log.d(TAG, "Detector was not operational");
            return "Invalid!";
        }
        Log.d(TAG, "Detector setup successful");

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        //plot the fram as a bitmap for debugg
        Bitmap frameBitmap = frame.getBitmap();
        Log.d(TAG, frame.toString());
        try {
            Barcode thisCode = barcodes.valueAt(0);
            upcCode = thisCode.rawValue;
            return thisCode.rawValue.toString();
        } catch (ArrayIndexOutOfBoundsException e) {
            upcCode = "";
            Toast.makeText(getApplicationContext(),"No Barcode found, try again?",
                    Toast.LENGTH_LONG).show();
            return e.toString();
        }

    }

    private void showDebugPic(final Bitmap bitmap) {
        ImageView view = findViewById(R.id.imageDebug);
        view.setImageBitmap(bitmap);
        Log.d(TAG, "Debug picture displatede");
    }

    /**
     * Initiate an async task in Task.java. Use the upc code from scanner.
     */
    public void sendAPIButton() {
        new Tasks.APICalling(MainActivity.this, requestQueue).execute(upcCode);
        Log.d(TAG, "DId I get the NDB number?" + NDBCode);
        //new Tasks.GetNameAndOther(MainActivity.this, requestQueue).execute(NDBCode);
//        startAPICall();
////                while (requestFinished == false) {
////                    ProgressBar progressBar = findViewById(R.id.progressBar);
////                    progressBar.setVisibility(View.VISIBLE);
////                }
//        ProgressBar progressBar = findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.INVISIBLE);
//        updateDetailList();
//        Log.d(TAG, "After 3s, the name is " + productName);
    }

    /**
     * Initiate the second API call in AnotherTask.java.
     * Get called in Task.java and use NDB code.
     */
    public void startGetProductInfo() {
        new AnotherTask.GetProductInfo(MainActivity.this,
                requestQueue).execute(NDBCode);
    }

    /**
     * Update the textView.
     * Get called in the AnotherTask.java.
     */
    protected void updateDetailList() {
        TextView detailInfo = (TextView) findViewById(R.id.detailInfo);
        detailInfo.setText(productName + " : " + nutVal + "kcal/100g");
        detailInfo.setVisibility(View.VISIBLE);
    }

}
