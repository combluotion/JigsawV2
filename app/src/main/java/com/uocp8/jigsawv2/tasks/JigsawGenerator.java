package com.uocp8.jigsawv2.tasks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.uocp8.jigsawv2.Game;
import com.uocp8.jigsawv2.model.Difficulty;
import com.uocp8.jigsawv2.model.LongParcelable;
import com.uocp8.jigsawv2.service.JigsawService;
import com.uocp8.jigsawv2.service.impl.JigsawServiceImpl;

public class JigsawGenerator extends AsyncTask<Bitmap, Integer, Long> {

    /** Jigsaw service */
    private JigsawService service;

    /** Difficulty level */
    private Difficulty level;

    /** Application context */
    private Context context;

    public JigsawGenerator(Context context, Difficulty level) {
        this.context = context;
        this.level = level;
        this.service = new JigsawServiceImpl(context);
    }

    @Override
    protected Long doInBackground(Bitmap... params) {
        return service.create(params[0], level);
    }

    @Override
    protected void onPostExecute(Long id) {
        startJigsaw(id);
    }

    private void startJigsaw(long id) {
        Intent intent = new Intent(context, Game.class).putExtra(
                Game.ORIGINAL_IMG_ID, new LongParcelable(id));
        intent.putExtra("level",this.level);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
