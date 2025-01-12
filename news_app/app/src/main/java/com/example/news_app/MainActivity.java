package com.example.news_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonTechnology, buttonSports, buttonPolitics, buttonEntertainment, buttonFavorites;

    private BroadcastReceiver batteryLevelReceiver;
    private BatteryLevelReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if app was closed by a previous intent with the "EXIT" flag
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finishAffinity(); // Close the activity
        }

        buttonTechnology = findViewById(R.id.buttonTechnology);
        buttonSports = findViewById(R.id.buttonSports);
        buttonPolitics = findViewById(R.id.buttonPolitics);
        buttonEntertainment = findViewById(R.id.buttonEntertainment);
        buttonFavorites = findViewById(R.id.buttonFavorites);

        // Register the battery level receiver
        receiver = new BatteryLevelReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }

        buttonTechnology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewsListActivity("technology");
            }
        });

        buttonSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewsListActivity("sports");
            }
        });

        buttonPolitics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewsListActivity("politics");
            }
        });

        buttonEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewsListActivity("entertainment");
            }
        });

        buttonFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFavoritesActivity();
            }
        });
    }


    private void openNewsListActivity(String category) {
        Intent intent = new Intent(MainActivity.this, com.example.news_app.NewsListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void openFavoritesActivity() {
        Intent intent = new Intent(MainActivity.this, com.example.news_app.FavoritesActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    public void closeApp() {
        // Method 1: Close all activities
        finishAffinity();

    }

}
