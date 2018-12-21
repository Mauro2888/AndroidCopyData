package com.example.maurocaredda.copydata;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class ServiceBg extends JobService {

    private static final String TAG = ServiceBg.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(TAG,"Start job here");
        notifyNow(getApplicationContext());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    public static void notifyNow(Context context){
        Notification notification = new Notification(context);
        Intent intentNotification = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intentNotification,0);
        NotificationCompat.Builder builder = notification.getAndroidChannelNotification("Notifica","notify").setContentIntent(pendingIntent);
        notification.getManager().notify(Integer.parseInt(StaticValues.ID_NOTIFICATION),builder.build());
    }
}
