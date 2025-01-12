package com.example.studylah;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class EventNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Call create notification channel to ensure it's created before showing the notification
        createNotificationChannel(context);

        // Get event details from intent
        String eventName = intent.getStringExtra("event_name");
        String eventTime = intent.getStringExtra("event_time");

        // Log notification sending
        Log.d("NotificationReceiver", "Sending notification for event: " + eventName);

        // Send the notification
        sendNotification(context, eventName, eventTime);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Event Notifications";
            String description = "Channel for event reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("event_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendNotification(Context context, String eventName, String eventTime) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "event_channel")
                .setSmallIcon(R.drawable.studylah_logo)// Replace with your icon
                .setContentTitle("Let's Join StudyLah Event!")
                .setContentText(eventName + " is happening tomorrow at " + eventTime + " tomorrow. Check it out!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);  // Dismiss the notification when tapped

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
        Log.d("NotificationReceiver", "Notification sent");
    }
}

