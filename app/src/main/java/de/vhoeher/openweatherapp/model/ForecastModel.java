package de.vhoeher.openweatherapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ForecastModel implements Serializable {

    private ArrayList<Long> mTimestamp;
    private ArrayList<Double> mHumidity, mTemperature, mWindSpeed;
    private String mLocation;

    public ForecastModel(ArrayList<Long> timestamp, ArrayList<Double> humidity, ArrayList<Double> temperature, ArrayList<Double> windSpeed, String location) {
        this.mTimestamp = timestamp;
        this.mHumidity = humidity;
        this.mTemperature = temperature;
        this.mWindSpeed = windSpeed;
        this.mLocation = location;
    }

    public ArrayList<Long> getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(ArrayList<Long> timestamp) {
        this.mTimestamp = timestamp;
    }

    public ArrayList<Double> getHumidity() {
        return mHumidity;
    }

    public void setHumidity(ArrayList<Double> humidity) {
        this.mHumidity = mHumidity;
    }

    public ArrayList<Double> getTemperature() {
        return mTemperature;
    }

    public void setTemperature(ArrayList<Double> temperature) {
        this.mTemperature = temperature;
    }

    public ArrayList<Double> getWindSpeed() {
        return mWindSpeed;
    }

    public void setmWindSpeed(ArrayList<Double> windSpeed) {
        this.mWindSpeed = windSpeed;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }
}
