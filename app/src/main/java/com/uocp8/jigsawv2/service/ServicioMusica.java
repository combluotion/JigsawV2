package com.uocp8.jigsawv2.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.uocp8.jigsawv2.R;

public class ServicioMusica extends Service {

    MediaPlayer miReproductor;

    public void onCreate () {

        super.onCreate();

        miReproductor = MediaPlayer.create(this, R.raw.sound_long);


        miReproductor.setLooping(true);

        miReproductor.setVolume(100, 100);



    }


    public int onStartCommand (Intent intent, int flags, int startId) {

        miReproductor.start();

        return START_STICKY;


    }

    public void onDestroy() {

        super.onDestroy();

        if(miReproductor.isPlaying()) miReproductor.stop();

        miReproductor.release();

        miReproductor=null;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
