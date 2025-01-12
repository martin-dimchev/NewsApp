package com.example.news_app;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {NewsArticleEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NewsArticleDao newsArticleDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "news_app_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
