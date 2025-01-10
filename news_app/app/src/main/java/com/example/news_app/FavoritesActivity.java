package com.example.news_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = AppDatabase.getInstance(this);

        new Thread(() -> {
            // Get favorite articles from the database
            List<NewsArticleEntity> favoriteEntities = database.newsArticleDao().getAllFavorites();

            // Convert NewsArticleEntity to NewsArticle
            List<NewsArticle> favorites = new ArrayList<>();
            for (NewsArticleEntity entity : favoriteEntities) {
                favorites.add(new NewsArticle(
                        entity.getTitle(),
                        entity.getDescription(),
                        entity.getContent(),
                        entity.getImageUrl(),
                        "a",
                        "a"
                ));
            }

            // Update UI on the main thread
            runOnUiThread(() -> {
                newsAdapter = new NewsAdapter(FavoritesActivity.this, favorites);
                recyclerView.setAdapter(newsAdapter);
            });
        }).start();
    }
}
