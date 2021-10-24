package com.uocp8.jigsawv2.dao.impl;

import static com.uocp8.jigsawv2.util.Base64Util.base64ToBitmap;
import static com.uocp8.jigsawv2.util.DBUtil.ALL_COLUMNS;
import static com.uocp8.jigsawv2.util.DBUtil.ALL_SCORE_COLUMNS;
import static com.uocp8.jigsawv2.util.DBUtil.DATE_SCORE;
import static com.uocp8.jigsawv2.util.DBUtil.DESC_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.IDEAL_POSITION;
import static com.uocp8.jigsawv2.util.DBUtil.ID_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.ID_USER_SCORE;
import static com.uocp8.jigsawv2.util.DBUtil.IMAGE_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.JIGSAW_TABLE;
import static com.uocp8.jigsawv2.util.DBUtil.NAME_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.ORIGINAL_COLUMN;
import static com.uocp8.jigsawv2.util.DBUtil.ORIGINAL_SELECTION;
import static com.uocp8.jigsawv2.util.DBUtil.SCORE_TABLE;
import static com.uocp8.jigsawv2.util.DBUtil.TIME_SCORE;
import static com.uocp8.jigsawv2.util.DBUtil.USERNAME_SCORE;
import static com.uocp8.jigsawv2.util.DBUtil.getIdArguments;
import static com.uocp8.jigsawv2.util.EntityUtil.entityToContentValues;
import static com.uocp8.jigsawv2.util.EntityUtil.scoreToContentValues;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uocp8.jigsawv2.dao.ScoreDao;
import com.uocp8.jigsawv2.db.JigsawDB;
import com.uocp8.jigsawv2.model.ImageEntity;
import com.uocp8.jigsawv2.model.Score;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScoreDaoImpl implements ScoreDao {

    private static final String TAG = "ScoreDaoImpl";

    private SQLiteDatabase db;

    public ScoreDaoImpl(Context context) {
        JigsawDB mdb = new JigsawDB(context);
        db = mdb.getWritableDatabase();
    }

    @Override
    public Long create(Score score) {
        Long id = db.insert(SCORE_TABLE, null, scoreToContentValues(score));
        Log.d(TAG, "successfully saved image...id: " + id);

        return id;
    }

    @Override
    public List<Score> retrieveScores() {
        List<Score> scores = new ArrayList<>();
        Cursor cursor = db.query(SCORE_TABLE, ALL_SCORE_COLUMNS, null, null, null, null,TIME_SCORE + " DESC","10");
        scores.addAll(getAllFromCursor(cursor));
        cleanUp(cursor);

        Log.d(TAG, "Found " + scores.size() + " scores ");
        return scores;
    }

    private List<Score> getAllFromCursor(Cursor cursor) {
        List<Score> entities = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Score entity = getScore(cursor);
                entities.add(entity);
            }
        }
        return entities;
    }

    private Score getScore(Cursor cursor) {
        String name = cursor.getString(getIndex(cursor, USERNAME_SCORE));
        String date = cursor.getString(getIndex(cursor, DATE_SCORE));
        double timeScore = cursor.getDouble(getIndex(cursor, TIME_SCORE));
        int id = cursor.getInt(getIndex(cursor, ID_USER_SCORE));

        Log.d(TAG, "Score found for : " + name + " of: " +timeScore);

        Score score = new Score(name,date,timeScore);
        score.setIduser(id);

        return score;
    }

    private int getIndex(final Cursor cursor, final String col) {
        return cursor.getColumnIndex(col);
    }

    private void cleanUp(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

}
