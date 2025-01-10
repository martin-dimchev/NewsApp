package com.example.news_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private Button buttonTechnology, buttonSports, buttonPolitics, buttonEntertainment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonTechnology = findViewById(R.id.buttonTechnology);
        buttonSports = findViewById(R.id.buttonSports);
        buttonPolitics = findViewById(R.id.buttonPolitics);
        buttonEntertainment = findViewById(R.id.buttonEntertainment);

        buttonTechnology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewsListActivity("Technology");
            }
        });

        buttonSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewsListActivity("Sports");
            }
        });

        buttonPolitics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewsListActivity("Politics");
            }
        });

        buttonEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewsListActivity("Entertainment");
            }
        });
    }

    private void openNewsListActivity(String category) {
        Intent intent = new Intent(MainActivity.this, com.example.news_app.NewsListActivity.class);
        intent.putExtra("category", category);  // Прехвърляме категорията към NewsListActivity
        startActivity(intent);
    }
}
