package de.vhoeher.openweatherapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import de.vhoeher.openweatherapp.model.WeatherDataModel;

/**
 * Util class, which handles and holds the history of weather data requests.
 * Stores WeatherDataModels in an SQLite-Database and reads the values out of the database.
 * The maximum of elements inside of the history is 100.
 * This class is a singleton.
 *
 * @author Victor Höher
 * @version 1.0
 */
public class HistoryUtil extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WeatherHistory.db";
    private static final String WEATHER_TABLE_NAME = "history";
    private static final String WEATHER_COLUMN_ID = "id";
    private static final String WEATHER_COLUMNS_DATA = "data";

    private static HistoryUtil mInstance;

    private ArrayList<WeatherDataModel> mHistory;

    /**
     * Private constructor to fulfill the singleton pattern.
     *
     * @param context The context, on which the util should be created
     */
    private HistoryUtil(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     * Getter for the current instance of the HistoryUtil
     *
     * @param context The context, on which the util should be created
     * @return The current HistoryUtil instance
     */
    public static HistoryUtil getInstance(Context context) {
        if (mInstance == null)
            mInstance = new HistoryUtil(context);
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Database if it is not created before
        db.execSQL("CREATE TABLE IF NOT EXISTS " + WEATHER_TABLE_NAME + " (" + WEATHER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + WEATHER_COLUMNS_DATA + " TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Perform upgrade (Hard upgrade)
        db.execSQL("DROP TABLE IF EXISTS " + WEATHER_TABLE_NAME);
        onCreate(db);
    }

    /**
     * Getter for the history
     *
     * @return The history of requested weather data
     */
    public ArrayList<WeatherDataModel> getHistory() {

        //If the history was not requested before, read it out of the database
        if (mHistory == null) {
            SQLiteDatabase db = this.getReadableDatabase();
            //Get cursor for the values
            Cursor cursor = db.rawQuery("SELECT *  FROM " + WEATHER_TABLE_NAME + " ORDER BY " + WEATHER_COLUMN_ID + " DESC", null);
            mHistory = new ArrayList<>();
            //Collect data
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    //Deserialize the data to the WeatherDataModel
                    mHistory.add((WeatherDataModel) ObjectSerializerUtil.stringToObject(cursor.getString(cursor.getColumnIndex(WEATHER_COLUMNS_DATA))));
                    cursor.moveToNext();
                }
            }
        }

        return mHistory;
    }

    /**
     * Add a weather data model to the history.
     * If the size of the history is greater then 100, then the oldest data will be deleted.
     *
     * @param model The model, which should be added.
     */
    public void addDataToHistory(WeatherDataModel model) {
        //Get history
        if (mHistory == null)
            getHistory();
        //Get database
        SQLiteDatabase db = this.getWritableDatabase();
        //Add value
        ContentValues values = new ContentValues();
        values.put(WEATHER_COLUMNS_DATA, ObjectSerializerUtil.objectToString(model));
        db.insert(WEATHER_TABLE_NAME, null, values);
        mHistory.add(0, model);
        //Remove the oldest element, if the history is greater then 100 (has 101 elements)
        if (mHistory.size() == 101) {
            mHistory.remove(100);
            db.execSQL("DELETE FROM " + WEATHER_TABLE_NAME + " WHERE " + WEATHER_COLUMN_ID + " =(SELECT MIN (" + WEATHER_COLUMN_ID + ") FROM " + WEATHER_TABLE_NAME + ")");
        }
    }
}
