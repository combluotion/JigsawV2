package com.uocp8.jigsawv2.util;

import java.time.LocalDate;
import java.time.LocalTime;

public final class DBUtil {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "jigsaw.db";
    public static final String JIGSAW_TABLE = "jigsaw_images";
    public static final String NAME_COLUMN = "name";
    public static final String ID_COLUMN = "id";
    public static final String IMAGE_COLUMN = "img";
    public static final String DESC_COLUMN = "desc";
    public static final String ORIGINAL_COLUMN = "original";
    public static final String IDEAL_POSITION = "idealposition";


    // Atributos para tabla Score

    public static final String SCORE_TABLE = "jigsaw_score";
    public static final String ID_USER_SCORE ="iduser";
    public static final String USERNAME_SCORE="username";
    public static final String DATE_SCORE = "datascore";
    public static final String TIME_SCORE ="timescore";


    private DBUtil() {}

    /** create jigsaw_images table */
    public static final String CREATE_JIGSAW_TABLE = "create table if not"
            + " exists " + JIGSAW_TABLE + " ("
            + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME_COLUMN + " TEXT,"
            + IMAGE_COLUMN + " TEXT," + DESC_COLUMN + " TEXT,"
            + ORIGINAL_COLUMN + " INTEGER,"
            + IDEAL_POSITION + " INTEGER);";

    /** drop jigsaw_images table */
    public static final String DROP_JIGSAW_TABLE = "drop table if exists " +
            JIGSAW_TABLE;

    /** for querying like in prepared statements */
    public static final String ID_SELECTION = "id = ?";

    /** original image selection */
    public static final String ORIGINAL_SELECTION = "original = ?";

    /** original image selection is null */
    public static final String ORIGINAL_SELECTION_NULL = "original is null";

    /** all columns selection */
    public final static String[] ALL_COLUMNS = new String[]{ID_COLUMN, NAME_COLUMN,
            IMAGE_COLUMN, DESC_COLUMN, ORIGINAL_COLUMN, IDEAL_POSITION};

    /** arguments to set for the prepared statements */
    public static String[] getIdArguments(final Long id) {
        return new String[]{String.valueOf(id)};
    }

    //Create Score Table
    public static final String CREATE_SCORE_TABLE = "create table if not"
            + " exists " + SCORE_TABLE + " ("
            + ID_USER_SCORE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + USERNAME_SCORE + " TEXT,"
            + DATE_SCORE + " DATE,"  //verificar
            + TIME_SCORE + " TIMESTAMP);"; //verificar
}
