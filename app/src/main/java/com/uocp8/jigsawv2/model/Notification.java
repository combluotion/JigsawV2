package com.uocp8.jigsawv2.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.uocp8.jigsawv2.R;

public class Notification extends AppCompatActivity {

    NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.puzzle)
            .setContentTitle("¡New record!")
            .setContentIntent(ContentIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);


    Intent NotificationIntent = new Intent(this,Score.class);
    PendingIntent ContentIntent = PendingIntent.getActivity(this,0,NotificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);

    }