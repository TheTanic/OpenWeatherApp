package de.vhoeher.openweatherapp.model;

import java.io.Serializable;

public class WeatherDataModel implements Serializable {

    private String mLocationName, mHumidity, mWindSpeed, mWindDirection, mPressure, mTemperature, mPrecipitation, mWeatherDescription;
    private long mDataTimestamp, mRequestTimestamp = -1;
    private int mIconID = -1;

    public WeatherDataModel(String locationName, String humidity, String windSpeed, String windDirection, String pressure,
                            String temperature, String precipitation, String weatherDescription, long dataTimestamp, long requestTimestamp, int iconID) {
        this.mLocationName = locationName;
        this.mHumidity = humidity;
        this.mWindSpeed = windSpeed;
        this.mWindDirection = windDirection;
        this.mPressure = pressure;
        this.mTemperature = temperature;
        this.mPrecipitation = precipitation;
        this.mWeatherDescription = weatherDescription;
        this.mDataTimestamp = dataTimestamp;
        this.mRequestTimestamp = requestTimestamp;
        this.mIconID = iconID;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        this.mLocationName = locationName;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public void setHumidity(String humidity) {
        this.mHumidity = humidity;
    }

    public String getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.mWindSpeed = windSpeed;
    }

    public String getWindDirection() {
        return mWindDirection;
    }

    public void setWindDirection(String windDirection) {
        this.mWindDirection = windDirection;
    }

    public String getPressure() {
        return mPressure;
    }

    public void setPressure(String pressure) {
        this.mPressure = pressure;
    }

    public String getTemperature() {
        return mTemperature;
    }

    public void setTemperature(String temperature) {
        this.mTemperature = temperature;
    }

    public String getmPrecipitation() {
        return mPrecipitation;
    }

    public void setPrecipitation(String precipitation) {
        this.mPrecipitation = precipitation;
    }

    public String getWeatherDescription() {
        return mWeatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.mWeatherDescription = weatherDescription;
    }

    public long getDataTimestamp() {
        return mDataTimestamp;
    }

    public void setDataTimestamp(long dataTimestamp) {
        this.mDataTimestamp = dataTimestamp;
    }

    public long getRequestTimestamp() {
        return mRequestTimestamp;
    }

    public void setmRequestTimestamp(long requestTimestamp) {
        this.mRequestTimestamp = requestTimestamp;
    }

    public int getIconID() {
        return mIconID;
    }

    public void setmIconID(int iconID) {
        this.mIconID = iconID;
    }
}
