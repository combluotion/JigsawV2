package com.uocp8.jigsawv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.uocp8.jigsawv2.model.Difficulty;
import com.uocp8.jigsawv2.model.JigsawState;
import com.uocp8.jigsawv2.model.LongParcelable;
import com.uocp8.jigsawv2.service.ServicioMusica;
import com.uocp8.jigsawv2.tasks.JigsawLoader;
import com.uocp8.jigsawv2.viewmodel.GameViewModel;

public class Game extends AppCompatActivity {

    ImageView imageView;
    /** The original image id to look up for jigsaw */
    public static final String ORIGINAL_IMG_ID = "originalId";

    /** Class name for logging */
    private static final String TAG = "JigsawActivity";

    /** Chronometer to display elapsed time */
    private Chronometer chronometer;

    /** Used to get current state of the chronometer play or pause */
    private JigsawState state = JigsawState.RUNNING;

    /** Need to keep track of time to reset the chronometer */
    private long elapsedTime = 0L;

    /**Current level being played */
    private Difficulty level;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_game);

        imageView = findViewById(R.id.imageView2);

        Intent intent = getIntent();
        int image = intent.getIntExtra("image",-1);

        imageView.setImageDrawable(getResources().getDrawable(image));*/
        init();
    }


    /**
     * Initialize all the views
     */
    private void init() {
        setContentView(R.layout.activity_game);
       // enableMenuBarUpButton();
        initViews();
        initTimer();
    }

    /**
     * Initialize the jigsaw grid view
     */
    private void initViews() {
        Log.d(TAG, "initializing jigsaw grid view");
        final GameViewModel gridView = findViewById(R.id.jigsaw_grid);

        JigsawLoader task = new JigsawLoader(getApplicationContext(), gridView);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            LongParcelable parcelable = bundle.getParcelable(ORIGINAL_IMG_ID);
            if(parcelable != null) {
                task.execute(parcelable.getData());
            }
            level =(Difficulty) bundle.get("level");
        }

        gridView.setDifficultyLevel(level);

        gridView.setOnItemLongClickListener(onItemLongClickListener(gridView));
        gridView.setOnDropListener(onDropListener(gridView));
        gridView.setOnDragListener(onDragListener());
    }

    /**
     * Initialize the chronometer
     */
    private void initTimer() {
        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    /**
     * Listener to get hold of the click event to start the grid edit mode
     *
     * @param gridView the grid view
     * @return the item long click listener
     */
    private GameViewModel.OnItemLongClickListener onItemLongClickListener(final GameViewModel gridView) {
        return (parent, view, position, id) -> {
            gridView.startEditMode(position);
            return true;
        };
    }

    /**
     * Listener to get hold of the drop event to stop the grid edit mode
     *
     * @param gridView the grid view
     * @return the drop listener
     */
    private GameViewModel.OnDropListener onDropListener(
            final GameViewModel gridView) {
        return () -> {
            Log.d(TAG, "dropped element");
            gridView.stopEditMode(chronometer);
        };
    }

    /**
     * Listener to get hold of the drag and position changed events
     *
     * @return the drag listener
     */
    private GameViewModel.OnDragListener onDragListener() {
        return new GameViewModel.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
                Log.d(TAG, "dragging starts...position: " + position);
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                Log.d(TAG, String.format("drag changed from %d to %d", oldPosition, newPosition));
            }
        };
    }

    @Override
    protected void onDestroy() {
        //stopService(new Intent(this, ServicioMusica.class));
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        final GameViewModel gridView = findViewById(R.id.jigsaw_grid);

        if(!gridView.IsChangingActivity) {
            stopService(new Intent(this, ServicioMusica.class));
        }
        gridView.IsChangingActivity = false;
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        startService(new Intent(this, ServicioMusica.class));
        super.onPostResume();
    }



}