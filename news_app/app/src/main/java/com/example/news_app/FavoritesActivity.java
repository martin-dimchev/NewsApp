package com.example.news_app;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private AppDatabase database;
    private GestureDetector gestureDetector;
    private boolean isDesc = true; // Default sort order: descending

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Your Favorites");
            getSupportActionBar().setDisplayShowTitleEnabled(true);  // Make sure title is displayed
        }
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
                String publishedAt = entity.getPublishedAt();
                Log.d("PublishedAt", "Date before processing: " + publishedAt);

                favorites.add(new NewsArticle(
                        entity.getTitle(),
                        entity.getDescription(),
                        entity.getContent(),
                        entity.getImageUrl(),
                        "a",
                        publishedAt
                ));
            }

            // Sort the favorites list
            Collections.sort(favorites, (news1, news2) -> {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

                    // Add logging to debug date parsing
                    Log.d("Sorting", "Comparing dates: " + news1.getPublishedAt() + " and " + news2.getPublishedAt());

                    // Remove any potential milliseconds from the date string
                    String date1Str = news1.getPublishedAt().split("\\.")[0];
                    String date2Str = news2.getPublishedAt().split("\\.")[0];

                    Date date1 = format.parse(date1Str);
                    Date date2 = format.parse(date2Str);

                    if (date1 == null || date2 == null) {
                        Log.e("Sorting", "Date parsing returned null");
                        return 0;
                    }

                    Log.d("Sorting", "Parsed dates: " + date1 + " and " + date2);

                    // Sort based on isDesc flag
                    return isDesc ? date2.compareTo(date1) : date1.compareTo(date2);
                } catch (ParseException e) {
                    Log.e("Sorting", "Error parsing date: " + e.getMessage());
                    e.printStackTrace();
                    return 0;
                }
            });

            runOnUiThread(() -> {
                if (newsAdapter == null) {
                    newsAdapter = new NewsAdapter(FavoritesActivity.this, favorites);
                    recyclerView.setAdapter(newsAdapter);
                } else {
                    newsAdapter.setNewsList(favorites);
                    newsAdapter.notifyDataSetChanged();
                }
                // Add a toast to show the current sort order
                String sortOrder = isDesc ? "Descending" : "Ascending";
                Toast.makeText(FavoritesActivity.this, "Sorted by date: " + sortOrder, Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu); // Inflate the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_ascending) {
            isDesc = false; // Sort Ascending
            loadFavorites();  // Reload favorites with new sort order
            return true;
        } else if (item.getItemId() == R.id.sort_descending) {
            isDesc = true; // Sort Descending
            loadFavorites();  // Reload favorites with new sort order
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}