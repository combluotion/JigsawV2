package com.uocp8.jigsawv2.util;

import static com.uocp8.jigsawv2.util.Base64Util.bitMapToBase64;
import static com.uocp8.jigsawv2.util.DBUtil.DATE_SCORE;
import static com.uocp8.jigsawv2.util.DBUtil.DESC_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.IDEAL_POSITION;
import static com.uocp8.jigsawv2.util.DBUtil.IMAGE_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.IMGPATH;
import static com.uocp8.jigsawv2.util.DBUtil.NAME_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.ORIGINAL_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.TIME_SCORE;
import static com.uocp8.jigsawv2.util.DBUtil.USERNAME_SCORE;

import android.content.ContentValues;

import com.uocp8.jigsawv2.model.ImageEntity;
import com.uocp8.jigsawv2.model.ImgPath;
import com.uocp8.jigsawv2.model.Score;

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
        cv.put(IDEAL_POSITION, entity.getIdealPosition());

        return cv;
    }

    public static ContentValues scoreToContentValues(Score score) {
        ContentValues cv = new ContentValues();
        cv.put(USERNAME_SCORE, score.getName());
        cv.put(DATE_SCORE, score.getDate().toString());
        cv.put(TIME_SCORE, score.getTime());

        return cv;
    }

    public static ContentValues imgPathToContentValues(ImgPath imgPath) {
        ContentValues cv = new ContentValues();
        cv.put(IMGPATH, imgPath.getImgPath());
        return cv;
    }

}
