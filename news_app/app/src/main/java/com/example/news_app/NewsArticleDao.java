package com.example.news_app;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NewsArticleDao {

    @Insert
    void insertFavorite(NewsArticleEntity article);

    @Delete
    void deleteFavorite(NewsArticleEntity article);

    @Query("SELECT * FROM favorites")
    List<NewsArticleEntity> getAllFavorites();

    @Query("SELECT * FROM favorites WHERE title = :title LIMIT 1")
    NewsArticleEntity getFavoriteByTitle(String title);
}

