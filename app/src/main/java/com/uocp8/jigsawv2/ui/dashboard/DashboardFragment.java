package com.uocp8.jigsawv2.ui.dashboard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.uocp8.jigsawv2.MainActivity;
import com.uocp8.jigsawv2.model.PictureModel;
import com.uocp8.jigsawv2.R;
import com.uocp8.jigsawv2.RecyclerViewAdaptador;
import com.uocp8.jigsawv2.databinding.FragmentDashboardBinding;
import com.uocp8.jigsawv2.model.Difficulty;
import com.uocp8.jigsawv2.tasks.JigsawGenerator;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;



    private RecyclerView recyclerViewPicture;
    private RecyclerViewAdaptador adaptadorPicture;

    

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

        recyclerViewPicture = (RecyclerView) getView().findViewById(R.id.recyclerPicture);
        recyclerViewPicture.setLayoutManager(new LinearLayoutManager(getContext()));

        adaptadorPicture = new RecyclerViewAdaptador(obtenerPictures(), new RecyclerViewAdaptador.ItemClickListener() {
            @Override
            public void onItemClick(PictureModel picture) {
                showToast(picture.getPicture() + "Clicked!");
                openCreateJigsawDialog(picture.getImgPicture());
            }
        });
        recyclerViewPicture.setAdapter(adaptadorPicture);
    }

   private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void openCreateJigsawDialog(int picture) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.level_difficulty)
                .items("Easy", "Medium", "Hard")
                .itemsCallbackSingleChoice(0, (dialog, view, which, text) -> {
                    createJigsaw(which, picture);
                    return true;
                })
                .positiveText(R.string.action_ok)
                .negativeText(R.string.action_cancel)
                .show();
    }

    private void createJigsaw(int which, int picture) {
        ((MainActivity)getActivity()).ChangingActivity(true);
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), picture);
        //bitmap = GridUtil.getResizedBitmap(bitmap, 1480, 1300, false);
        JigsawGenerator task = new JigsawGenerator(getContext(), Difficulty.fromValue(which));


        task.execute(bitmap.copy(bitmap.getConfig(), true));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public List<PictureModel> obtenerPictures() {
        List<PictureModel> picture = new ArrayList<>();
        picture.add(new PictureModel("Michael Cera", R.drawable.image1));
        picture.add(new PictureModel("Steve Buscemi", R.drawable.image2));
        //picture.add(new PictureModel("Imagen 3", R.drawable.image3));
        picture.add(new PictureModel("Marion Cotillard", R.drawable.image4));
        picture.add(new PictureModel("Niña 1", R.drawable.image5));
        picture.add(new PictureModel("Hombre 1", R.drawable.image6));
        return picture;

    }


}