package com.uocp8.jigsawv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.uocp8.jigsawv2.service.ServicioMusica;

public class Ayuda extends AppCompatActivity {

    private WebView webView;
    public boolean isChangingActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        webView = findViewById(R.id.wb);
        webView.loadUrl("file:///android_asset/help.html");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

    }
    @Override
    protected void onDestroy() {
        //stopService(new Intent(this, ServicioMusica.class));
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(!isChangingActivity) {
            ServicioMusica.PauseMusic();
        }
        isChangingActivity = false;
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        if(!ServicioMusica.getUniqueIstance().isPlaying())
           ServicioMusica.RestartMusic();

        super.onPostResume();
    }

    @Override
    public void onBackPressed() {
        ChangingActivity(true);
        super.onBackPressed();
    }

    public void ChangingActivity(boolean value)
    {
        isChangingActivity = value;
    }


}