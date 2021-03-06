package com.uocp8.jigsawv2.service.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.uocp8.jigsawv2.dao.ImageDao;
import com.uocp8.jigsawv2.dao.impl.ImageDaoImpl;
import com.uocp8.jigsawv2.model.Difficulty;
import com.uocp8.jigsawv2.model.ImageEntity;
import com.uocp8.jigsawv2.service.JigsawService;

import java.util.UUID;

public class JigsawServiceImpl  implements JigsawService {

    /** Class name for logging */
    private static final String TAG = "JigsawServiceImpl";

    /** Image entity dao */
    private ImageDao dao;

    /**
     * Create new jigsaw service given context
     *
     * @param context the application context
     */
    public JigsawServiceImpl(Context context) {
        dao = new ImageDaoImpl(context);
    }

    @Override
    public Long create(Bitmap original, Difficulty level) {
        dao.cleanJigsawTable();
        return createImageTiles(original, level.getValue());
    }

    /**
     * Slice original image into tiles then save in the db
     *
     * @param original the original image to slice up
     * @param n how many slices to cut the image into
     */
    private Long createImageTiles(Bitmap original, int n) {
        Long originalId = saveOriginal(original);

        Long idealPosition = 0L;

        int w = original.getWidth();
        int h = original.getHeight();

        int tileWidth = w / n;
        int tileHeight = h / n;

        for (int y = 0; y + tileHeight <= h; y += tileHeight) {
            for (int x = 0; x + tileWidth <= w; x += tileWidth) {
                Bitmap tile = Bitmap.createBitmap(original, x, y, tileWidth, tileHeight);
                saveTile(tile, x, y, originalId, idealPosition);
                idealPosition++;
            }
        }

        return originalId;
    }

    /**
     * Save the original image with a random UUID
     *
     * @param original image to save
     */
    private Long saveOriginal(Bitmap original) {
        String name = UUID.randomUUID() + ".png";
        String desc = "original image " + name;
        Log.d(TAG, "image name: " + name);

        return saveEntity(original, name, desc, null, null);
    }

    /**
     * Save created tile in the database
     *
     * @param tile the tile to save
     * @param x the tile's x coordinate
     * @param y the tile's y coordinate
     */
    private void saveTile(Bitmap tile, int x, int y, Long originalId, Long idealPosition) {
        String name = "tile-" + x + "-" + y + ".png";
        String desc = "sub image " + name;
        Log.d(TAG, "image name: " + name);

        saveEntity(tile, name, desc, originalId, idealPosition);
    }

    /**
     * Create an image entity with the give parameters and save
     *
     * @param image the bitmap image
     * @param name the name
     * @param desc the description
     */
    private Long saveEntity(Bitmap image, String name, String desc, Long originalId, Long idealPosition) {
        return dao.create(new ImageEntity(image, name, desc, originalId, idealPosition));
    }
}
