package com.applaudostudios.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.applaudostudios.models.ScheduleGame;
import com.applaudostudios.models.Stadium;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Tarles on 08/04/2017.
 */

public class ApplaudoSQLiteController
{
    private ApplaudoSQLiteHelper applaudoDB;
    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    private ContentValues contentValues;
    private Cursor cursor;

    public  ApplaudoSQLiteController(Context context)
    {
        this.applaudoDB = new ApplaudoSQLiteHelper(context, "", null, 1);
        this.sqLiteDatabase = this.applaudoDB.getWritableDatabase();
    }

    public void closeDatabase()
    {
        try
        {
            if(this.sqLiteDatabase != null)
            {
                if(this.sqLiteDatabase.isOpen())
                {
                    this.sqLiteDatabase.close();
                }
            }
        }
        catch (Exception er)
        {
            Log.e("Error CloseDababase", er.getMessage());
        }
    }

    public void insertStadium(Stadium stadium)
    {
        try
        {
            long lastInsertId = 0;
            String image;
            if(stadium != null)
            {
                this.contentValues = new ContentValues();
                this.contentValues.put("id_remote_stadium", stadium.getId());
                this.contentValues.put("team_name", stadium.getTeamName());
                this.contentValues.put("since", stadium.getSince());
                this.contentValues.put("coach", stadium.getCoach());
                this.contentValues.put("team_nickname", stadium.getTeamNickname());
                this.contentValues.put("stadium", stadium.getStadium());
                this.contentValues.put("img_logo", stadium.getImgLogo());
                this.contentValues.put("img_stadium", stadium.getImgStadium());
                this.contentValues.put("latitude", stadium.getLatitude());
                this.contentValues.put("longitude", stadium.getLongitude());
                this.contentValues.put("website", stadium.getWebsite());
                this.contentValues.put("tickets_url", stadium.getTicketsUrl());
                this.contentValues.put("address", stadium.getAddress());
                this.contentValues.put("phone_number", stadium.getPhoneNumber());
                this.contentValues.put("description", stadium.getDescription());
                this.contentValues.put("video_url", stadium.getVideoUrl());

                lastInsertId = sqLiteDatabase.insert(applaudoDB.STADIUM_TABLE_NAME, null, this.contentValues);

                insertListScheduleGame(stadium.getScheduleGames(), lastInsertId);
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
            //Log.e("Error insertStadium", er.printStackTrace());
        }
    }

    public void insertListScheduleGame(List<ScheduleGame> scheduleGameList, long idStadium)
    {
        try
        {
            ScheduleGame scheduleGame = null;
            if(scheduleGameList != null)
            {
                if(scheduleGameList.size() > 0)
                {
                    for (int a = 0; a <scheduleGameList.size(); a++)
                    {
                        scheduleGame = scheduleGameList.get(a);
                        insertScheduleGame(scheduleGame, idStadium);
                    }
                }
            }
        }
        catch (Exception er)
        {
            Log.e("Error ListScheduleGame ", er.getMessage());
        }
    }

    public void insertScheduleGame(ScheduleGame scheduleGame, long idStadium)
    {
        try
        {
            if(scheduleGame != null)
            {
                this.contentValues = new ContentValues();
                this.contentValues.put("id_stadium", idStadium);
                this.contentValues.put("date", scheduleGame.getDate());
                this.contentValues.put("stadium_name", scheduleGame.getStadium());
                sqLiteDatabase.insert(applaudoDB.SCHEDULE_GAME_TABLE_NAME, null, this.contentValues);
            }
        }
        catch (Exception er)
        {
            Log.e("Error", er.getMessage());
        }
    }

    public int countTotalStadium()
    {
        int count = 0;
        try
        {
            String sqlQuery = "SELECT COUNT(*) FROM " + this.applaudoDB.STADIUM_TABLE_NAME;
            this.cursor = null;
            this.cursor = sqLiteDatabase.rawQuery(sqlQuery, null);
            if(cursor != null)
            {
                if(this.cursor.moveToFirst())
                {
                    count = this.cursor.getInt(0);
                }
            }

        }
        catch (Exception er)
        {
            er.printStackTrace();
            count = 0;
        }

        return count;
    }

    public int countTotalScheduleGameByIdStadium(int id)
    {
        int count = 0;
        try
        {
            if(id > 0)
            {
                String sqlQuery = "SELECT COUNT(*) FROM " + this.applaudoDB.SCHEDULE_GAME_TABLE_NAME + " WHERE id_stadium = " + id;
                this.cursor = null;
                this.cursor = sqLiteDatabase.rawQuery(sqlQuery, null);
                if (cursor != null) {
                    if (this.cursor.moveToFirst()) {
                        count = this.cursor.getInt(0);
                    }
                }
            }

        }
        catch (Exception er)
        {
            er.printStackTrace();
            count = 0;
        }

        return count;
    }

    public int getStadiumIdByRemoteId(int idRemoteStadium)
    {
        int id = 0;
        try
        {
            if(idRemoteStadium > 0)
            {
                String sqlQuery = "SELECT id FROM " + this.applaudoDB.STADIUM_TABLE_NAME + " WHERE id_remote_stadium = " + idRemoteStadium;
                this.cursor = null;
                this.cursor = sqLiteDatabase.rawQuery(sqlQuery, null);

                if(this.cursor != null)
                {
                    if(this.cursor.moveToFirst())
                    {
                        id = this.cursor.getInt(0);
                    }
                }
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
            id = 0;
        }
        return  id;
    }


    public void updateStadiumById(Stadium stadium, int id)
    {
        try
        {
            if(stadium != null && id > 0)
            {
                    String sqlCondition = "id = " + id;
                    this.contentValues = new ContentValues();
                    this.contentValues.put("id_remote_stadium", stadium.getId());
                    this.contentValues.put("team_name", stadium.getTeamName());
                    this.contentValues.put("since", stadium.getSince());
                    this.contentValues.put("coach", stadium.getCoach());
                    this.contentValues.put("team_nickname", stadium.getTeamNickname());
                    this.contentValues.put("stadium", stadium.getStadium());
                    this.contentValues.put("img_logo", stadium.getImgLogo());
                    this.contentValues.put("img_stadium", stadium.getImgStadium());
                    this.contentValues.put("latitude", stadium.getLatitude());
                    this.contentValues.put("longitude", stadium.getLongitude());
                    this.contentValues.put("website", stadium.getWebsite());
                    this.contentValues.put("tickets_url", stadium.getTicketsUrl());
                    this.contentValues.put("address", stadium.getAddress());
                    this.contentValues.put("phone_number", stadium.getPhoneNumber());
                    this.contentValues.put("description", stadium.getDescription());
                    this.contentValues.put("video_url", stadium.getVideoUrl());
                    sqLiteDatabase.update(applaudoDB.STADIUM_TABLE_NAME, this.contentValues, sqlCondition, null);
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }

    public void updateScheduleGameById(ScheduleGame scheduleGame, int id)
    {
        try
        {
            if(scheduleGame != null && id > 0)
            {
                String sqlCondition = "id_stadium = " + id;
                this.contentValues = new ContentValues();
                this.contentValues.put("id_stadium", id);
                this.contentValues.put("date", scheduleGame.getDate());
                this.contentValues.put("stadium_name", scheduleGame.getStadium());
                sqLiteDatabase.update(applaudoDB.SCHEDULE_GAME_TABLE_NAME, this.contentValues, sqlCondition, null);
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }

    public void deleteScheduleGameByIdStadium(int id)
    {
        try
        {
            if(id > 0)
            {
                String sqlCondition = "id_stadium = " + id;
                sqLiteDatabase.delete(applaudoDB.SCHEDULE_GAME_TABLE_NAME, sqlCondition, null);
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }

    public void deleteAllScheduleGame()
    {
        try
        {
            String sqlCondition = "id > 0";
            sqLiteDatabase.delete(applaudoDB.SCHEDULE_GAME_TABLE_NAME, sqlCondition, null);
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }

    public void deleteAllStadium()
    {
        try
        {
            String sqlCondition = "id > 0";
            sqLiteDatabase.delete(applaudoDB.STADIUM_TABLE_NAME, sqlCondition, null);
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }

    public Stadium getStadiumById(int id)
    {
        Stadium stadium = null;
        try
        {
            if(id > 0)
            {
                String sqlQuery = "SELECT * FROM " + this.applaudoDB.STADIUM_TABLE_NAME + " WHERE id = " + id;
                this.cursor = null;
                this.cursor = sqLiteDatabase.rawQuery(sqlQuery, null);

                if (this.cursor != null) {
                    if (this.cursor.moveToFirst()) {
                        stadium = new Stadium();
                        stadium.setId(this.cursor.getInt(1));
                        stadium.setTeamName(this.cursor.getString(2));
                        stadium.setSince(this.cursor.getString(3));
                        stadium.setCoach(this.cursor.getString(4));
                        stadium.setTeamNickname(this.cursor.getString(5));
                        stadium.setStadium(this.cursor.getString(6));
                        stadium.setImgLogo(this.cursor.getString(7));
                        stadium.setImgStadium(this.cursor.getString(8));
                        stadium.setLatitude(this.cursor.getDouble(9));
                        stadium.setLongitude(this.cursor.getDouble(10));
                        stadium.setWebsite(this.cursor.getString(11));
                        stadium.setTicketsUrl(this.cursor.getString(12));
                        stadium.setAddress(this.cursor.getString(13));
                        stadium.setPhoneNumber(this.cursor.getString(14));
                        stadium.setDescription(this.cursor.getString(15));
                        stadium.setVideoUrl(this.cursor.getString(16));
                        stadium.setScheduleGames(getAllScheduleGameById(this.cursor.getInt(0)));
                    }
                }
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
            stadium = null;
        }
        return stadium;
    }

    public List<Stadium> getAllStadium()
    {
        List<Stadium> listStadium = null;
        try
        {
           listStadium = new ArrayList<Stadium>();
            Stadium stadium = null;
            String sqlQuery = "SELECT * FROM " + this.applaudoDB.STADIUM_TABLE_NAME;
            this.cursor = null;
            this.cursor = sqLiteDatabase.rawQuery(sqlQuery, null);

            if (this.cursor != null) {
                if (this.cursor.moveToFirst())
                {
                    do
                    {
                        stadium = new Stadium();
                        stadium.setId(this.cursor.getInt(1));
                        stadium.setTeamName(this.cursor.getString(2));
                        stadium.setSince(this.cursor.getString(3));
                        stadium.setCoach(this.cursor.getString(4));
                        stadium.setTeamNickname(this.cursor.getString(5));
                        stadium.setStadium(this.cursor.getString(6));
                        stadium.setImgLogo(this.cursor.getString(7));
                        stadium.setImgStadium(this.cursor.getString(8));
                        stadium.setLatitude(this.cursor.getDouble(9));
                        stadium.setLongitude(this.cursor.getDouble(10));
                        stadium.setWebsite(this.cursor.getString(11));
                        stadium.setTicketsUrl(this.cursor.getString(12));
                        stadium.setAddress(this.cursor.getString(13));
                        stadium.setPhoneNumber(this.cursor.getString(14));
                        stadium.setDescription(this.cursor.getString(15));
                        stadium.setVideoUrl(this.cursor.getString(16));
                        listStadium.add(stadium);
                    } while(this.cursor.moveToNext());
                }
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
        return listStadium;
    }

    public List<ScheduleGame> getAllScheduleGameById(int id)
    {
        List<ScheduleGame> listScheduleGame = null;
        try
        {
            ScheduleGame scheduleGame = null;
            if(id > 0)
            {
                listScheduleGame = new ArrayList<ScheduleGame>();
                String sqlQuery = "SELECT * FROM " + this.applaudoDB.SCHEDULE_GAME_TABLE_NAME + " WHERE id_stadium = " + id;
                Cursor cursorQuery = null;
                cursorQuery = sqLiteDatabase.rawQuery(sqlQuery, null);

                if (cursorQuery!= null) {
                    scheduleGame = new ScheduleGame();
                    if (cursorQuery.moveToFirst())
                    {
                        do
                        {
                            scheduleGame = new ScheduleGame();
                            scheduleGame.setDate(cursorQuery.getString(2));
                            scheduleGame.setStadium(cursorQuery.getString(3));
                            listScheduleGame.add(scheduleGame);
                        } while(cursorQuery.moveToNext());
                    }
                }
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
        return listScheduleGame;
    }






    /*public InputStream getImageFromURL(String urlImage)
    {
        InputStream inputStream = null;
        try
        {
            if(urlImage != null)
            {
                if(urlImage.length() > 0)
                {
                    URL url = new URL(urlImage);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    inputStream = connection.getInputStream();
                }
            }
        }
        catch (IOException er)
        {
            Log.e("Error getImageFromURL", er.getMessage());
        }
        return inputStream;
    }

    public byte[] getBytes(InputStream inputStream) {
        byte[] arrayBytes = null;
        try
        {
            ByteArrayOutputStream byteBuffer = null;
            if(inputStream != null)
            {
                byteBuffer = new ByteArrayOutputStream();
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];

                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }

                if(byteBuffer != null)
                {
                    if (byteBuffer.size() > 0) {
                        arrayBytes = byteBuffer.toByteArray();
                    }
                }
            }
        }
        catch (IOException er)
        {
            Log.e("Error getBytes", er.getMessage());
        }
        return arrayBytes;
    }*/
}
