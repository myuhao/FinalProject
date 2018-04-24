package com.example.harry.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "Main Activity";

    /** Constant to perform a read file request. */
    private static final int READ_REQUEST_CODE = 42;

    /** Constant to request an image capture. */
    private static final int IMAGE_CAPTURE_REQUEST_CODE = 1;

    /** Constant to request permission to write to the external storage device. */
    private static final int REQUEST_WRITE_STORAGE = 112;

    /** Can I write to the public storage? */
    private boolean canWriteToPublicStorage = false;

    private static Map list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button goToAdded = (Button) findViewById(R.id.added);
        goToAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Added button clicked-----------------------");
                goToAdded();
            }
        });

        final ImageButton takePicture = (ImageButton) findViewById(R.id.takePicture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "DO something with the take picture button-----------------------");
                takePicture();
            }
        });

        final ImageButton openFile = (ImageButton) findViewById(R.id.loadFromLocal);
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "DO something with the take load button------------------------");
                getLocalPictures();
            }
        });

        final ImageButton startCall = (ImageButton) findViewById(R.id.startCalling);
        startCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "DO something with the start API calling button------------------");
            }
        });

//        ProgressBar progressBar = findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.INVISIBLE);


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


    protected void takePicture() {
        if (pictureRequestActive) {
            Log.w(TAG, "Opening camera in progress");
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currenPictureFile = getSaveFilename();
        if (takePictureIntent.resolveActivity(getPackageManager()) == null
                || currenPictureFile == null) {
            Toast.makeText(getApplicationContext(), "Can't take picture",
                    Toast.LENGTH_SHORT).show();
            return;
        }

//        Uri pictureURI = FileProvider.getUriForFile(this,
//                "com.example.harry.finalproject.fileprovider", currenPictureFile);
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureURI);
        pictureRequestActive = true;
        startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE);
    }


    protected void getLocalPictures() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
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
        BarcodeDetector detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(0)
                        .build();
        if (!detector.isOperational()) {
            Log.d(TAG, "Detector was not operational ---------");
            return null;
        }
        Log.d(TAG, "Detector setup successful");

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        Barcode thisCode = barcodes.valueAt(0);
        return thisCode.rawValue;
    }
}
