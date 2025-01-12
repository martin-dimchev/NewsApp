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
            getSupportActionBar().setDisplayShowTitleEnabled(false);  // This hides the title
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
                String publishedAt = entity.getPublishedAt(); // Get the publishedAt value
                Log.d("PublishedAt", publishedAt);
                // Add the NewsArticle to the list
                favorites.add(new NewsArticle(
                        entity.getTitle(),
                        entity.getDescription(),
                        entity.getContent(),
                        entity.getImageUrl(),
                        "a",
                        entity.getPublishedAt()// Use the non-null publishedAt

                ));
            }

            // Sort the favorites based on the publishedAt date
            Collections.sort(favorites, (news1, news2) -> {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                    Date date1 = format.parse(news1.getPublishedAt());
                    Date date2 = format.parse(news2.getPublishedAt());

                    if (isDesc) {
                        return date2.compareTo(date1);  // Sort in descending order
                    } else {
                        return date1.compareTo(date2);  // Sort in ascending order
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;  // Return 0 if parsing fails
                }
            });

            // Update the UI with the sorted news
            runOnUiThread(() -> {
                if (newsAdapter != null) {
                    newsAdapter.setNewsList(favorites);
                    newsAdapter.notifyDataSetChanged(); // Notify that the data set has changed
                } else {
                    newsAdapter = new NewsAdapter(FavoritesActivity.this, favorites);
                    recyclerView.setAdapter(newsAdapter);
                }
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
            Toast.makeText(this, "Sorted Ascending", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.sort_descending) {
            isDesc = true; // Sort Descending
            loadFavorites();  // Reload favorites with new sort order
            Toast.makeText(this, "Sorted Descending", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}