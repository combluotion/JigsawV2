package com.uocp8.jigsawv2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.uocp8.jigsawv2.R;

public class ServicioMusica extends Service {

    private static MediaPlayer miReproductor;
    private static boolean isMuted = false;

    public static MediaPlayer getUniqueIstance(){
        if (miReproductor == null) {
            miReproductor = new MediaPlayer();
        }
        return miReproductor;
    }

    public static void setMuted()
    {
        if(!isMuted) {
            isMuted = true;
            PauseMusic();
        }
        else {
            isMuted = false;
            RestartMusic();
        }
        }

    public static void changeSong(Context context, Uri uri){
        if(miReproductor != null && miReproductor.isPlaying())
        {
            miReproductor.stop();
            miReproductor.release();
        }
        miReproductor = MediaPlayer.create(context, uri);
        miReproductor.setLooping(true);
        miReproductor.setVolume(100, 100);
        miReproductor.start();
        isMuted = false;
    }

    public static void PauseMusic(){
        if(miReproductor != null && miReproductor.isPlaying())
        {
            miReproductor.pause();

        }

    }

    public static void RestartMusic(){
        if(miReproductor != null && !miReproductor.isPlaying() && !isMuted)
        {
            miReproductor.start();
        }

    }

    public void onCreate () {
        super.onCreate();
        if(!miReproductor.isPlaying()) {
            miReproductor = MediaPlayer.create(this, R.raw.sound_long);
            miReproductor.setLooping(true);
            miReproductor.setVolume(100, 100);
        }
        }



    public int onStartCommand (Intent intent, int flags, int startId) {
        if(miReproductor != null && !miReproductor.isPlaying())
        miReproductor.start();

      /*  if(miReproductor != null) {
            if(miReproductor.isPlaying())  miReproductor.stop();
            miReproductor.release();
        }
        if(intent.getExtras() != null)
        {
            Uri myUri = Uri.parse(intent.getExtras().getString("uri"));
            miReproductor = MediaPlayer.create(this, myUri);
        }
        else
        {
            miReproductor = MediaPlayer.create(this, R.raw.sound_long);
        }*/
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
