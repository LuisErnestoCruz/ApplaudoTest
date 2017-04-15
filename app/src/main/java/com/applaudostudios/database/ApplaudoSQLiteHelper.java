package com.applaudostudios.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Created by Tarles on 08/04/2017.
 */

public class ApplaudoSQLiteHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "ApplaudoDB.db";
    public String STADIUM_TABLE_NAME = "Stadium";
    public String SCHEDULE_GAME_TABLE_NAME = "Schedule_games";

    String sqlCreateStadiumTable = "CREATE TABLE Stadium (id INTEGER PRIMARY KEY, id_remote_stadium INTEGER, team_name TEXT, since TEXT, coach TEXT, team_nickname TEXT, stadium TEXT, img_logo TEXT, img_stadium TEXT, latitude TEXT, longitude TEXT, website TEXT, tickets_url TEXT, address TEXT, phone_number TEXT, description TEXT, video_url TEXT)";
    String sqlCreateScheduleGameTable = "CREATE TABLE Schedule_games(id INTEGER PRIMARY KEY, id_stadium INTEGER, date TEXT, stadium_name TEXT)";
    String sqlDropStadiumTable = "DROP TABLE IF EXISTS Stadium";
    String sqlDropScheduleGameTable = "DROP TABLE IF EXISTS Schedule_games";

    public ApplaudoSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateStadiumTable);
        db.execSQL(sqlCreateScheduleGameTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(sqlDropStadiumTable);
        db.execSQL(sqlDropScheduleGameTable);

        db.execSQL(sqlCreateStadiumTable);
        db.execSQL(sqlCreateScheduleGameTable);
    }
}
