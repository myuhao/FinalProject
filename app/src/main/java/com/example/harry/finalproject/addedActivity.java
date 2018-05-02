package com.example.harry.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class addedActivity extends AppCompatActivity {
    private static String TAG = "Added activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Button goToHome = (Button) findViewById(R.id.button6);
        goToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "HOME Buttom clocked");
                goToHome();
            }
        });
    }

    protected void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected void setList(final String item) {
        TextView list = findViewById(R.id.textView5);
        list.setText(item);
        list.setVisibility(View.VISIBLE);
        return;
    }
}
