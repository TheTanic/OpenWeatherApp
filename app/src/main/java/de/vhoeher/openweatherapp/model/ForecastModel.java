package de.vhoeher.openweatherapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Model class, which holds the information of a weather forecast.
 *
 * @author Victor Höher
 * @version 1.0
 */
public class ForecastModel implements Serializable {

    private ArrayList<Long> mTimestamp;
    private ArrayList<Double> mHumidity, mTemperature, mWindSpeed;
    private String mLocation;

    /**
     * Constructor, which receives all needed values
     *
     * @param timestamp   The timestamps of the values
     * @param humidity    The humidity values
     * @param temperature The temperature values
     * @param windSpeed   The wind speed values
     * @param location    The location, from where the forecast is from
     */
    public ForecastModel(ArrayList<Long> timestamp, ArrayList<Double> humidity, ArrayList<Double> temperature, ArrayList<Double> windSpeed, String location) {
        this.mTimestamp = timestamp;
        this.mHumidity = humidity;
        this.mTemperature = temperature;
        this.mWindSpeed = windSpeed;
        this.mLocation = location;
    }

    /**
     * Getter for the timestamps of the forecast
     *
     * @return The timestamps of the forecast
     */
    public ArrayList<Long> getTimestamp() {
        return mTimestamp;
    }

    /**
     * Setter for the timestamps of the forecast
     *
     * @param timestamp The timestamps of the forecast
     */
    public void setTimestamp(ArrayList<Long> timestamp) {
        this.mTimestamp = timestamp;
    }

    /**
     * Getter for the humidity values of the forecast
     *
     * @return The humidity values of the forecast
     */
    public ArrayList<Double> getHumidity() {
        return mHumidity;
    }

    /**
     * Setter for the humidity values of the forecast
     *
     * @param humidity The humidity values of the forecast
     */
    public void setHumidity(ArrayList<Double> humidity) {
        this.mHumidity = mHumidity;
    }

    /**
     * Getter for the temperature values of the forecast
     *
     * @return The temperature values of the forecast
     */
    public ArrayList<Double> getTemperature() {
        return mTemperature;
    }

    /**
     * Setter for the temperature values of the forecast
     *
     * @param temperature The temperature values of the forecast
     */
    public void setTemperature(ArrayList<Double> temperature) {
        this.mTemperature = temperature;
    }

    /**
     * Getter for the wind speed values of the forecast
     *
     * @return The wind speed  values of the forecast
     */
    public ArrayList<Double> getWindSpeed() {
        return mWindSpeed;
    }

    /**
     * Setter for the wind speed values of the forecast
     *
     * @param windSpeed The wind speed values of the forecast
     */
    public void setmWindSpeed(ArrayList<Double> windSpeed) {
        this.mWindSpeed = windSpeed;
    }

    /**
     * Getter for the location of the forecast
     *
     * @return The location of the forecast
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * Setter for the location of the forecast
     *
     * @param location The location of the forecast
     */
    public void setLocation(String location) {
        this.mLocation = location;
    }
}
