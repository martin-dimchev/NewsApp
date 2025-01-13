package com.example.news_app;
import android.content.Intent;
import android.widget.ImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<NewsArticle> newsList;

    public NewsAdapter(Context context, List<NewsArticle> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        NewsArticle article = newsList.get(position);

        holder.titleTextView.setText(article.getTitle());
        holder.descriptionTextView.setText(article.getDescription());

        // Format the date string
        String rawDate = article.getPublishedAt();
        String formattedDate = formatDate(rawDate);
        holder.publishedAtTextView.setText(formattedDate);

        String imageUrl = article.getUrlToImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Use Glide to load the image
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.imageView);
        }


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewsDetailsActivity.class);
            intent.putExtra("title", article.getTitle());
            intent.putExtra("description", article.getDescription());
            intent.putExtra("content", article.getContent());
            intent.putExtra("imageUrl", article.getUrlToImage());
            intent.putExtra("publishedAt", formattedDate); // Pass the formatted date
            context.startActivity(intent);
        });
    }

    private String formatDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) {
            return "Unknown Date";
        }

        try {
            // Define the input and output date formats
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            // Parse the raw date string into a Date object
            Date date = inputFormat.parse(rawDate);

            // Format the Date object into the desired string format
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void setNewsList(List<NewsArticle> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView publishedAtTextView;

        public NewsViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.newsTitle);
            descriptionTextView = itemView.findViewById(R.id.newsDescription);
            imageView = itemView.findViewById(R.id.imageView);
            publishedAtTextView = itemView.findViewById(R.id.newsPublishedAt);
        }
    }
}

