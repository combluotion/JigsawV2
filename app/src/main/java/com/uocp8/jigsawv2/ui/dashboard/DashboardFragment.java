package com.uocp8.jigsawv2.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.service.controls.actions.FloatAction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uocp8.jigsawv2.model.PictureModel;
import com.uocp8.jigsawv2.R;
import com.uocp8.jigsawv2.RecyclerViewAdaptador;
import com.uocp8.jigsawv2.databinding.FragmentDashboardBinding;
import com.uocp8.jigsawv2.model.Difficulty;
import com.uocp8.jigsawv2.tasks.JigsawGenerator;
import com.uocp8.jigsawv2.util.PermissionsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;



    private RecyclerView recyclerViewPicture;
    private RecyclerViewAdaptador adaptadorPicture;

    private FloatingActionButton openCamera;
    ActivityResultLauncher<Intent> mGetPhoto;

    private FloatingActionButton galleryRun;
    Handler handler = new Handler();
    private Bitmap currentBitmap = null;


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

        openCamera = getView().findViewById(R.id.openCamera);
        mGetPhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>(){
            @Override
            public void onActivityResult(ActivityResult data) {
                if (data.getResultCode() == RESULT_OK) {
                    Bundle extras = data.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    createJigsawFromBitmap(imageBitmap);
                }
            }

        });

        openCamera.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                        mGetPhoto.launch(takePictureIntent);
                    }
                }
            });

        galleryRun = getView().findViewById(R.id.galleryRun);
        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        obtainGalleryImageAndCreateJigsaw();
                    }
                });
        galleryRun.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //PermissionsUtil.checkPermission(42,getContext(), Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);


                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.READ_EXTERNAL_STORAGE ) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    obtainGalleryImageAndCreateJigsaw();
                }  else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                            Manifest.permission.READ_EXTERNAL_STORAGE );
                }
                }
        });
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
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), picture);
        //bitmap = GridUtil.getResizedBitmap(bitmap, 1480, 1300, false);
        JigsawGenerator task = new JigsawGenerator(getContext(), Difficulty.fromValue(which));


        task.execute(bitmap.copy(bitmap.getConfig(), true));
    }

    private void createJigsawFromBitmap(Bitmap bitmap) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.level_difficulty)
                .items("Easy", "Medium", "Hard")
                .itemsCallbackSingleChoice(0, (dialog, view, which, text) -> {
                    JigsawGenerator task = new JigsawGenerator(getContext(), Difficulty.fromValue(which));
                    task.execute(bitmap.copy(bitmap.getConfig(), true));
                    return true;
                })
                .positiveText(R.string.action_ok)
                .negativeText(R.string.action_cancel)
                .show();
        //Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), picture);
        //bitmap = GridUtil.getResizedBitmap(bitmap, 1480, 1300, false);

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

private void obtainGalleryImageAndCreateJigsaw()
{
    String[] projection = new String[]{
            MediaStore.Images.Media.DATA,
    };

    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    Cursor cur = getActivity().getContentResolver().query(images,
            projection,
            null,
            null,
            null
    );

    final ArrayList<String> imagesPath = new ArrayList<String>();
    if (cur.moveToFirst()) {

        int dataColumn = cur.getColumnIndex(
                MediaStore.Images.Media.DATA);
        do {
            imagesPath.add(cur.getString(dataColumn));
        } while (cur.moveToNext());
    }
    cur.close();
    final Random random = new Random();
    final int count = imagesPath.size();

    int number = random.nextInt(count);
    String path = imagesPath.get(number);
    if (currentBitmap != null)
        currentBitmap.recycle();
    currentBitmap = BitmapFactory.decodeFile(path);
    createJigsawFromBitmap(currentBitmap);


}
}