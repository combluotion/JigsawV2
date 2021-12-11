package com.uocp8.jigsawv2.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.uocp8.jigsawv2.Game;
import com.uocp8.jigsawv2.MainActivity;
import com.uocp8.jigsawv2.R;
import com.uocp8.jigsawv2.ServicioMusica;
import com.uocp8.jigsawv2.databinding.FragmentHomeBinding;
import com.uocp8.jigsawv2.model.Difficulty;
import com.uocp8.jigsawv2.tasks.JigsawGenerator;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

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



        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}