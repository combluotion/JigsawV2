package com.uocp8.jigsawv2;



import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.uocp8.jigsawv2.databinding.ActivityMainBinding;
import com.uocp8.jigsawv2.service.ServicioMusica;
import com.uocp8.jigsawv2.util.NotificationsUtil;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ImageView musicButton;

    private boolean isChangingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        isChangingActivity = false;
        if(!ServicioMusica.getUniqueIstance().isLooping())
        startService(new Intent(this, ServicioMusica.class));

        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_ayuda, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Ayuda) {
            //Return to Home menu
            Intent intent = new Intent(this.getApplicationContext(), Ayuda.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.getApplicationContext().startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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

    public void ChangingActivity(boolean value)
    {
        isChangingActivity = value;
    }

}