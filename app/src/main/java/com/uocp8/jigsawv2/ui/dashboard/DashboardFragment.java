package com.uocp8.jigsawv2.ui.dashboard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.uocp8.jigsawv2.R;
import com.uocp8.jigsawv2.databinding.FragmentDashboardBinding;
import com.uocp8.jigsawv2.model.Difficulty;
import com.uocp8.jigsawv2.tasks.JigsawGenerator;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private Button button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        button =  getView().findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getContext(), Game.class);
                openCreateJigsawDialog();
                //intent.putExtra("image",getResources().getIdentifier("image1","drawable", getContext().getPackageName()));
                //startActivity(intent);
            }
        });
    }

    private void openCreateJigsawDialog() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.level_difficulty)
                .items("Easy", "Medium", "Hard")
                .itemsCallbackSingleChoice(0, (dialog, view, which, text) -> {
                    createJigsaw(which);
                    return true;
                })
                .positiveText(R.string.action_ok)
                .negativeText(R.string.action_cancel)
                .show();
    }

    private void createJigsaw(int which)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.image1);
        JigsawGenerator task = new JigsawGenerator(getContext(), Difficulty.fromValue(which));


        task.execute(bitmap.copy(bitmap.getConfig(), true));
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}