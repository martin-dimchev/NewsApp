package com.example.news_app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsDetailsActivity extends AppCompatActivity {

    private Button buttonFavorite;
    private AppDatabase database;
    private TextView newsTitle, newsDescription, newsContent;
    private ImageView newsImage;
    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        // Create the notification channel (only for devices API 26 and above)
        NotificationHelper.createNotificationChannel(this);

        // Initialize views
        newsTitle = findViewById(R.id.newsTitle);
        newsDescription = findViewById(R.id.newsDescription);
        newsContent = findViewById(R.id.newsContent);
        newsImage = findViewById(R.id.newsImage);
        buttonFavorite = findViewById(R.id.buttonFavorite);

        // Initialize gesture detector
        gestureDetector = new GestureDetectorCompat(this, new GestureListener());

        database = AppDatabase.getInstance(this);

        // Check permission for Android 13 and higher (POST_NOTIFICATIONS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Get the data passed from NewsListActivity
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String content = intent.getStringExtra("content");
        String imageUrl = intent.getStringExtra("imageUrl");
        String publishedAt = intent.getStringExtra("publishedAt");

        // Set the data to the views
        newsTitle.setText(title);
        newsDescription.setText(description);
        newsContent.setText(content);

        // Load the image using Glide
        Glide.with(this)
                .load(imageUrl)
                .into(newsImage);

        // Check if the article is already in the favorites
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
                article.setPublishedAt(publishedAt);

                String rawDate = toRawDateFormat(publishedAt);
                article.setPublishedAt(rawDate);

                NewsArticleEntity existing = database.newsArticleDao().getFavoriteByTitle(title);
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (existing != null) {
                    database.newsArticleDao().deleteFavorite(existing);
                    runOnUiThread(() -> {
                        buttonFavorite.setText("Add to Favorites");
                        vibrator.vibrate(100);
                        NotificationHelper.showNotification(NewsDetailsActivity.this, title, false);
                    });
                } else {
                    database.newsArticleDao().insertFavorite(article);
                    runOnUiThread(() -> {
                        buttonFavorite.setText("Remove from Favorites");
                        vibrator.vibrate(100);
                        NotificationHelper.showNotification(NewsDetailsActivity.this, title, true);
                    });
                }
            }).start();
        });
    }

    private String toRawDateFormat(String formattedDate) {
        try {
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            // Parse the formatted date string
            Date date = outputFormat.parse(formattedDate);

            // Convert it back to raw format
            return inputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null or an appropriate fallback
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return false;  // Return false to allow other touch events
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY) &&
                        Math.abs(diffX) > SWIPE_THRESHOLD &&
                        Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) { // Right swipe
                        onBackPressed();
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Check if the touch event is on the favorite button
        if (buttonFavorite != null) {
            int[] location = new int[2];
            buttonFavorite.getLocationOnScreen(location);
            int buttonX = location[0];
            int buttonY = location[1];

            if (ev.getX() >= buttonX &&
                    ev.getX() <= buttonX + buttonFavorite.getWidth() &&
                    ev.getY() >= buttonY &&
                    ev.getY() <= buttonY + buttonFavorite.getHeight()) {
                // If touch is on button, let the normal event handling take place
                return super.dispatchTouchEvent(ev);
            }
        }

        // If not on button, check for swipe gesture
        if (gestureDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}
