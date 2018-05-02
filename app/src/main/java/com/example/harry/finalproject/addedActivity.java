package com.example.harry.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lib.Product;

import java.util.ArrayList;
import java.util.Arrays;

public class addedActivity extends AppCompatActivity {
    private static String TAG = "Added activity";

    private static double totalCal = 0.0;

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

        setList();

        ListView myListView = findViewById(R.id.listView);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                Log.d(TAG, "Selected");
            }
        });

        TextView total = findViewById(R.id.totalCal);
        total.setText(String.format("%.0f kCal", totalCal));
        total.setVisibility(View.VISIBLE);
    }

    /**
     * Go back to the home page.
     */
    protected void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Set up the list view.
     */
    protected void setList() {
        ListView myListView = findViewById(R.id.listView);
        String[] list = new String[] {"aa", "bb", "cc"};
        ArrayList<String> test = new ArrayList<>(Arrays.asList(list));

        ListAdapter adapter = new ListAdapter(this, MainActivity.allItem);
        myListView.setAdapter(adapter);
        totalCal = calculateTotalCal(MainActivity.allItem);
    }

    /**
     * Helper function that calculate the total cal of the ArrayList.
     * @param items - The ArrayList to search.
     * @return Total cal.
     */
    protected double calculateTotalCal(final ArrayList<Product> items) {
        int size = items.size();
        double totalCal = 0.0;

        for (int i = 0; i < size; i++) {
            Product item = items.get(i);
            totalCal += item.getCalories() * item.getAmount() / 100;
        }
        return totalCal;
    }
}
