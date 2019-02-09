package de.vhoeher.openweatherapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.vhoeher.openweatherapp.model.WeatherDataModel;

public class HistoryUtil extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WeatherHistory.db";
    private static final String WEATHER_TABLE_NAME = "history";
    private static final String WEATHER_COLUMN_ID = "id";
    private static final String WEATHER_COLUMNS_DATA = "data";

    private static HistoryUtil mInstance;

    private ArrayList<WeatherDataModel> mHistory;

    private HistoryUtil(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static HistoryUtil getInstance(Context context) {
        if (mInstance == null)
            mInstance = new HistoryUtil(context);
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + WEATHER_TABLE_NAME + " (" + WEATHER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + WEATHER_COLUMNS_DATA + " TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + WEATHER_TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<WeatherDataModel> getHistory() {

        if (mHistory == null) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT *  FROM " + WEATHER_TABLE_NAME + " ORDER BY " + WEATHER_COLUMN_ID + " DESC", null);
            mHistory = new ArrayList<>();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    mHistory.add((WeatherDataModel) ObjectSerializerUtil.stringToObject(cursor.getString(cursor.getColumnIndex(WEATHER_COLUMNS_DATA))));
                    cursor.moveToNext();
                }
            }
        }

        return mHistory;
    }

    public void addDataToHistory(WeatherDataModel model) {
        if (mHistory == null)
            getHistory();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WEATHER_COLUMNS_DATA, ObjectSerializerUtil.objectToString(model));
        db.insert(WEATHER_TABLE_NAME, null, values);
        mHistory.add(0, model);
        if (mHistory.size() > 100) {
            db.execSQL("DELETE FROM " + WEATHER_TABLE_NAME + " WHERE " + WEATHER_COLUMN_ID + " =(SELECT MIN (" + WEATHER_COLUMN_ID + ") FROM " + WEATHER_TABLE_NAME + ")");
        }
    }
}
