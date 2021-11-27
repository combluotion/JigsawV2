package com.uocp8.jigsawv2.dao.impl;

import static com.uocp8.jigsawv2.util.DBUtil.ALL_IMGPATH_COLUMNS;
import static com.uocp8.jigsawv2.util.DBUtil.IMGPATH_TABLE;
import static com.uocp8.jigsawv2.util.DBUtil.JIGSAW_TABLE;
import static com.uocp8.jigsawv2.util.EntityUtil.entityToContentValues;
import static com.uocp8.jigsawv2.util.EntityUtil.imgPathToContentValues;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uocp8.jigsawv2.dao.ImgPathDao;
import com.uocp8.jigsawv2.db.JigsawDB;
import com.uocp8.jigsawv2.model.ImgPath;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ImgPathDaoImpl implements ImgPathDao {

    /** Class name for logging */
    private static final String TAG = "ImageDaoImpl";

    /** SQLite database */
    private SQLiteDatabase db;

    /**
     * Create new dao object with given context
     *
     * @param context the application context
     */
    public ImgPathDaoImpl(Context context) {
        JigsawDB mdb = new JigsawDB(context);
        db = mdb.getWritableDatabase();
    }

    @Override
    public Long create(ImgPath entity) {
        Long id = db.insert(IMGPATH_TABLE, null, imgPathToContentValues(entity));
        Log.d(TAG, "successfully saved image...id: " + id);

        return id;
    }

    @Override
    public ArrayList<String> retrievePaths() {
        Cursor c = db.query(IMGPATH_TABLE, ALL_IMGPATH_COLUMNS, null, null, null,null,null);
        if(c != null) {
            ArrayList<String> paths = new ArrayList<String>();
            while(c.moveToNext())
            {
                paths.add(c.getString(0));
            }
            c.close();
            return paths;
        }
        //Si no lo encuentra, retorna null
        return null;

    }
}
