package com.example.news_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Use Glide for image loading

public class NewsDetailsActivity extends AppCompatActivity {

    private TextView newsTitle, newsDescription, newsContent;
    private ImageView newsImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        // Initialize views
        newsTitle = findViewById(R.id.newsTitle);
        newsDescription = findViewById(R.id.newsDescription);
        newsContent = findViewById(R.id.newsContent);
        newsImage = findViewById(R.id.newsImage);

        // Get the data passed from NewsListActivity
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String content = intent.getStringExtra("content");
        String imageUrl = intent.getStringExtra("imageUrl");

        // Set the data to the views
        newsTitle.setText(title);
        newsDescription.setText(description);
        newsContent.setText(content);

        // Load the image using Glide (or Picasso)
        Glide.with(this)
                .load(imageUrl)
                .into(newsImage);
    }
}
