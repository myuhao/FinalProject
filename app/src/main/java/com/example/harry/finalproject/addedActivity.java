package com.example.harry.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

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

        setList("unde");
    }

    protected void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected void setList(final String item) {
        ListView myListView = findViewById(R.id.listView);
        String[] list = new String[] {"aa", "bb", "cc"};
        ArrayList<String> test = new ArrayList<>(Arrays.asList(list));

        ListAdapter adapter = new ListAdapter(this, MainActivity.allItem);
        myListView.setAdapter(adapter);

    }
}
