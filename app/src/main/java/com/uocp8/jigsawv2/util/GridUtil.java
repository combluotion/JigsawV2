package com.uocp8.jigsawv2.util;

import android.view.View;

public final class GridUtil {
    private GridUtil() {}

    public static float getViewX(View view) {
        return Math.abs((view.getRight() - view.getLeft()) / 2);
    }

    public static float getViewY(View view) {
        return Math.abs((view.getBottom() - view.getTop()) / 2);
    }
}
