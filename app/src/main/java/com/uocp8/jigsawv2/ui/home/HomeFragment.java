package com.uocp8.jigsawv2.ui.home;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.uocp8.jigsawv2.R;
import com.uocp8.jigsawv2.databinding.FragmentHomeBinding;
import com.uocp8.jigsawv2.service.ServicioMusica;

import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ImageButton chooseMusic;
    ActivityResultLauncher<Intent> mChooseMusic;
    private ImageButton muteMusic;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        /**
         *
         * titulo score
         *
        final TextView scoreTitle = binding.ScoreTitle;
        homeViewModel.getScoreTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {  eliminar
                scoreTitle.setText(s);
            }
        });
            */

        final ListView listView = binding.scoreList;
        homeViewModel.loadScore().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> s) {
                ArrayAdapter<String> itemsAdapter =
                        new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, s);

                ListView listView = (ListView) getView().findViewById(R.id.score_list);
                listView.setAdapter(itemsAdapter);
            }
        });

        chooseMusic = binding.chooseMusic;
        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        OpenFileManager();
                    }
                });
        chooseMusic.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.READ_EXTERNAL_STORAGE ) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                        OpenFileManager();
                }  else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                            Manifest.permission.READ_EXTERNAL_STORAGE );
                }
            }
        });
        mChooseMusic = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult data) {
                if (data.getResultCode() == RESULT_OK) {
                    Uri filePath = data.getData().getData();
                    Intent i = new Intent(getContext(), ServicioMusica.class);
                    i.putExtra("uri", filePath.toString());

                    ServicioMusica.changeSong(getContext(), filePath);
                }
            }

            ;
        });
        muteMusic = binding.muteMusic;
        muteMusic.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                ServicioMusica.setMuted();
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void OpenFileManager()
    {
        Intent audio_picker_intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        if (audio_picker_intent.resolveActivity(getContext().getPackageManager()) != null) {
            mChooseMusic.launch(audio_picker_intent);
        };

    }
}