package com.example.news_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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
        Button buttonClearFavorites = findViewById(R.id.buttonClearFavorites);

        buttonClearFavorites.setOnClickListener(v -> clearAllFavorites());
        new Thread(() -> {
            List<NewsArticleEntity> favoriteEntities = database.newsArticleDao().getAllFavorites();

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

            runOnUiThread(() -> {
                newsAdapter = new NewsAdapter(FavoritesActivity.this, favorites);
                recyclerView.setAdapter(newsAdapter);
            });
        }).start();
    }

    private void clearAllFavorites() {
        new Thread(() -> {
            // Clear all favorites from the database
            database.newsArticleDao().clearFavorites();

            // After clearing, fetch the updated list of favorites from the database
            List<NewsArticleEntity> favoriteEntities = database.newsArticleDao().getAllFavorites();

            // Update UI on the main thread
            runOnUiThread(() -> {
                // Update the RecyclerView to show no items
                if (newsAdapter != null) {
                    newsAdapter.setNewsList(new ArrayList<>());  // Empty list
                    newsAdapter.notifyDataSetChanged();
                }

                // Optionally, you could inform the user that the favorites are cleared
                Toast.makeText(FavoritesActivity.this, "All favorites cleared", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}
