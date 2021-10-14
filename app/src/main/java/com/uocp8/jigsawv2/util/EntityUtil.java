package com.uocp8.jigsawv2.util;

import static com.uocp8.jigsawv2.util.Base64Util.bitMapToBase64;
import static com.uocp8.jigsawv2.util.DBUtil.DESC_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.IMAGE_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.NAME_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.ORIGINAL_COLUMN;

import android.content.ContentValues;

import com.uocp8.jigsawv2.model.ImageEntity;

public final class EntityUtil {
    private EntityUtil() {}

    /**
     * Convert the given image entity to content values
     *
     * @param entity the entity to convert
     * @return content values object
     */
    public static ContentValues entityToContentValues(ImageEntity entity) {
        ContentValues cv = new ContentValues();
        cv.put(NAME_COLUMN, entity.getName());
        cv.put(IMAGE_COLUMN, bitMapToBase64(entity.getImage()));
        cv.put(DESC_COLUMN, entity.getDesc());
        cv.put(ORIGINAL_COLUMN, entity.getOriginalId());

        return cv;
    }
}
