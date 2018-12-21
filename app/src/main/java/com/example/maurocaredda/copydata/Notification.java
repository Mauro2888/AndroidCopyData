package com.example.maurocaredda.copydata;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

public class Notification extends ContextWrapper {

    private NotificationManager notificationManager;

    public Notification(Context base) {
        super(base);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    StaticValues.ID_CHANNEL,
                    StaticValues.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLockscreenVisibility(Notification.MODE_PRIVATE);
            channel.setLightColor(Color.GREEN);
            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationCompat.Builder getAndroidChannelNotification(String title, String body) {
        return new NotificationCompat.Builder(getApplicationContext(), StaticValues.ID_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true);
    }

    public NotificationManager getManager(){
        if (notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
}
