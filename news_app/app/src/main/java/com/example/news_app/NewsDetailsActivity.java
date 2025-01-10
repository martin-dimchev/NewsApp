package com.example.news_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Use Glide for image loading

public class NewsDetailsActivity extends AppCompatActivity {

    private Button buttonFavorite;
    private AppDatabase database;
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
        buttonFavorite = findViewById(R.id.buttonFavorite);

        database = AppDatabase.getInstance(this);

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

        new Thread(() -> {
            NewsArticleEntity favorite = database.newsArticleDao().getFavoriteByTitle(title);
            runOnUiThread(() -> {
                if (favorite != null) {
                    buttonFavorite.setText("Remove from Favorites");
                }
            });
        }).start();

        // Handle favorite button click
        buttonFavorite.setOnClickListener(v -> {
            new Thread(() -> {
                NewsArticleEntity article = new NewsArticleEntity();
                article.setTitle(title);
                article.setDescription(description);
                article.setContent(content);
                article.setImageUrl(imageUrl);

                NewsArticleEntity existing = database.newsArticleDao().getFavoriteByTitle(title);
                if (existing != null) {
                    database.newsArticleDao().deleteFavorite(existing);
                    runOnUiThread(() -> {
                        buttonFavorite.setText("Add to Favorites");
                        Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    database.newsArticleDao().insertFavorite(article);
                    runOnUiThread(() -> {
                        buttonFavorite.setText("Remove from Favorites");
                        Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });
    }
}

