package com.example.news_app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class NewsArticleEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private String publishedAt;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getPublishedAt () { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
}
