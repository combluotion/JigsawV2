package com.uocp8.jigsawv2.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.GridView;

import com.uocp8.jigsawv2.adapters.JigsawGridAdapter;
import com.uocp8.jigsawv2.dao.ImageDao;
import com.uocp8.jigsawv2.dao.impl.ImageDaoImpl;
import com.uocp8.jigsawv2.model.ImageEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JigsawLoader extends AsyncTask<Long, Integer, List<ImageEntity>> {
    /** Image dao */
    private ImageDao dao;

    /** The grid view */
    private GridView gridView;

    /** The application context */
    private Context context;

    public JigsawLoader(Context context, GridView gridView) {
        this.context = context;
        this.gridView = gridView;
        this.dao = new ImageDaoImpl(context);
    }

    @Override
    protected /*List<Bitmap>*/ List<ImageEntity> doInBackground(Long... params) {
        List<ImageEntity> entities = dao.findTiles(params[0]);
        Collections.shuffle(entities);

        //return entities.stream().map(ImageEntity::getImage).collect(Collectors.toList());
        return entities;
    }

    @Override
    protected void onPostExecute(/*List<Bitmap>*/ List<ImageEntity> tiles) {
        int pieces = (int) Math.sqrt(tiles.size());
        JigsawGridAdapter adapter = new JigsawGridAdapter(context, tiles, pieces);

        gridView.setAdapter(adapter);
        gridView.setNumColumns(pieces);
        //gridView.setColumnWidth(tiles.get(0).getWidth());

    }
}
