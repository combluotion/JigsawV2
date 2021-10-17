package com.uocp8.jigsawv2.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;

public final class GridUtil {
    private GridUtil() {}

    public static float getViewX(View view) {
        return Math.abs((view.getRight() - view.getLeft()) / 2);
    }

    public static float getViewY(View view) {
        return Math.abs((view.getBottom() - view.getTop()) / 2);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean recycleOriginal) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        // Determine scale to change size
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // Create Matrix for maniuplating size
        Matrix matrix = new Matrix();
        // Set the Resize Scale for the Matrix
        matrix.postScale(scaleWidth, scaleHeight);

        //Create a new Bitmap from original using matrix and new width/height
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        //Remove memory leaks if told to recycle, warning, if using original else where do not recycle it here
        if(recycleOriginal) {
            bm.recycle();

        }

        //Return the scaled new bitmap
        return resizedBitmap;

    }
}
