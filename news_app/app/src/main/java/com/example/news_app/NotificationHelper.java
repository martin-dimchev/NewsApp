package com.example.news_app;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "favorites_channel";
    private static int notificationId = 0;

    public static void createNotificationChannel(Context context) {
        // Create the notification channel only for Android Oreo (API 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Favorites",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for favorite news notifications");

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showNotification(Context context, String title, boolean isAdding) {
        // For Android 13 (API 33) and above, you would check the permission (if needed)
        // But on Android 12 and below, we don't need the permission

        String message = isAdding ? "Added to favorites" : "Removed from favorites";

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // For devices below Android Oreo, set the priority manually
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // Send the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        try {
            notificationManager.notify(notificationId++, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
            // Handle the exception - log it or show a Toast
        }
    }
}
