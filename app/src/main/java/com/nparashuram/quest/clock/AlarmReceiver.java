package com.nparashuram.quest.clock;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "ALARM_CHANNEL";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            AlarmActivity.markAlarmTriggered(context);
            showNotification(context);
            String currentTime = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
            Log.w(CHANNEL_ID, "Alarm was triggered at " + currentTime);
        } catch (Exception e) {
            Log.e(CHANNEL_ID, e.toString());
        }
    }

    private void showNotification(Context context) {
        try {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Create notification channel for Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Alarm Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Channel for alarm notifications");
                notificationManager.createNotificationChannel(channel);
            }

            // Create intent for when notification is tapped
            Intent intent = new Intent(context, AlarmActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Create PendingIntent with proper flags
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            flags |= PendingIntent.FLAG_IMMUTABLE;

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, flags
            );

            // Build notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setContentTitle("Alarm!")
                    .setContentText("Alarm from the clock app at " +
                        java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new java.util.Date()))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setTimeoutAfter(60000)
                    .setContentIntent(pendingIntent);

            // Show notification
            notificationManager.notify(NOTIFICATION_ID, builder.build());

        } catch (Exception e) {
            Log.e(CHANNEL_ID, "Failed to show notification: " + e.getMessage(), e);
        }
    }
} 