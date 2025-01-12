package com.example.news_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = AppDatabase.getInstance(this);
        Button buttonClearFavorites = findViewById(R.id.buttonClearFavorites);

        buttonClearFavorites.setOnClickListener(v -> clearAllFavorites());

        // Initialize GestureDetector
        gestureDetector = new GestureDetector(this, new GestureListener());

        // Add touch listener to the RecyclerView
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                // Not needed
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // Not needed
            }
        });

        loadFavorites();
    }

    private void loadFavorites() {
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
            database.newsArticleDao().clearFavorites();
            List<NewsArticleEntity> favoriteEntities = database.newsArticleDao().getAllFavorites();

            runOnUiThread(() -> {
                if (newsAdapter != null) {
                    newsAdapter.setNewsList(new ArrayList<>());
                    newsAdapter.notifyDataSetChanged();
                }
                Toast.makeText(FavoritesActivity.this, "All favorites cleared", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true; // Required for onFling to work
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) {
                return false;
            }

            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY) &&
                    Math.abs(diffX) > SWIPE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                    return true;
                }
            }
            return false;
        }
    }

    private void onSwipeRight() {
        Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        Toast.makeText(this, "Swiped Right to Main Screen!", Toast.LENGTH_SHORT).show();
    }
}