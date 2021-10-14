package com.uocp8.jigsawv2.service;

import android.graphics.Bitmap;

import com.uocp8.jigsawv2.model.Difficulty;

public interface JigsawService {
    /**
     * Create jigsaw puzzle from given original image and difficulty level
     *
     * @param original the original image to create jigsaw puzzle
     * @param level difficulty level
     * @return the id of the original image once saved
     */
    Long create(Bitmap original, Difficulty level);
}
