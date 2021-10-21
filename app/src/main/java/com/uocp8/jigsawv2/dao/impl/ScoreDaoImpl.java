package com.uocp8.jigsawv2.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.uocp8.jigsawv2.dao.ScoreDao;
import com.uocp8.jigsawv2.db.JigsawDB;
import com.uocp8.jigsawv2.model.Score;

import java.util.List;

public class ScoreDaoImpl implements ScoreDao {

    private static final String TAG = "ScoreDaoImpl";

    private SQLiteDatabase db;

    public ScoreDaoImpl(Context context) {
        JigsawDB mdb = new JigsawDB(context);
        db = mdb.getWritableDatabase();
    }

    @Override
    public Long create(Score entity) {
        return null;
    }

    @Override
    public List<Score> findTiles(Long id) {
        return null;
    }
}
