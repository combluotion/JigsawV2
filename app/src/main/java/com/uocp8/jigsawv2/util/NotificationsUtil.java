package com.uocp8.jigsawv2.util;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.uocp8.jigsawv2.R;

public final class NotificationsUtil{

    NotificationManagerCompat notificationManagerCompat;
    Notification notification;


    public NotificationsUtil(String title, String message, NotificationManager notiManager, Context context) {

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    {
        NotificationChannel channel = new NotificationChannel("JigsawNotifChannel","Jigsaw Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = notiManager;
        manager.createNotificationChannel(channel);
    }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "JigsawNotifChannel")
                .setSmallIcon(R.drawable.puzzle)
                .setContentTitle(title)
                .setContentText(message);

        notification = builder.build();

        notificationManagerCompat = NotificationManagerCompat.from(context);


        notificationManagerCompat.notify(1, notification);
    }
}
