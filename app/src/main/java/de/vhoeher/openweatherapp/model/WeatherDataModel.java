package de.vhoeher.openweatherapp.model;

import java.io.Serializable;

/**
 * Model class, which holds the information of a single weather data.
 *
 * @author Victor Höher
 * @version 1.0
 */
public class WeatherDataModel implements Serializable {

    private String mLocationName, mHumidity, mWindSpeed, mWindDirection, mPressure, mTemperature, mPrecipitation, mWeatherDescription;
    private long mDataTimestamp, mRequestTimestamp = -1;
    private int mIconID = -1;

    /**
     * Constructor, which receives all needed values
     *
     * @param locationName       The name of the location, from where the data is from
     * @param humidity           The humidity value
     * @param windSpeed          The wind speed value
     * @param windDirection      The wind direction
     * @param pressure           The pressure value
     * @param temperature        The temperature value
     * @param precipitation      The precipitation value
     * @param weatherDescription The description of the weather condition
     * @param dataTimestamp      The timestamp, when the weather data was measured
     * @param requestTimestamp   The timestamp, when the request for the weather data was performed
     * @param iconID             The id of the icon
     */
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

    /**
     * Getter for the location of the weather data
     *
     * @return The location of the weather data
     */
    public String getLocationName() {
        return mLocationName;
    }

    /**
     * Setter for the location
     *
     * @param locationName The new location
     */
    public void setLocationName(String locationName) {
        this.mLocationName = locationName;
    }

    /**
     * Getter for the humidity of the weather data
     *
     * @return The humidity of the weather data
     */
    public String getHumidity() {
        return mHumidity;
    }

    /**
     * Setter for the humidity
     *
     * @param humidity The new humidity
     */
    public void setHumidity(String humidity) {
        this.mHumidity = humidity;
    }

    /**
     * Getter for the wind speed of the weather data
     *
     * @return The wind speed of the weather data
     */
    public String getWindSpeed() {
        return mWindSpeed;
    }

    /**
     * Setter for the wind speed
     *
     * @param windSpeed The new wind speed
     */
    public void setWindSpeed(String windSpeed) {
        this.mWindSpeed = windSpeed;
    }

    /**
     * Getter for the wind direction of the weather data
     *
     * @return The wind direction of the weather data
     */
    public String getWindDirection() {
        return mWindDirection;
    }

    /**
     * Setter for the wind direction
     *
     * @param windDirection The new wind direction
     */
    public void setWindDirection(String windDirection) {
        this.mWindDirection = windDirection;
    }

    /**
     * Getter for the pressure of the weather data
     *
     * @return The pressure of the weather data
     */
    public String getPressure() {
        return mPressure;
    }

    /**
     * Setter for the pressure
     *
     * @param pressure The new pressure
     */
    public void setPressure(String pressure) {
        this.mPressure = pressure;
    }

    /**
     * Getter for the temperature of the weather data
     *
     * @return The temperature of the weather data
     */
    public String getTemperature() {
        return mTemperature;
    }

    /**
     * Setter for the temperature
     *
     * @param temperature The new temperature
     */
    public void setTemperature(String temperature) {
        this.mTemperature = temperature;
    }

    /**
     * Getter for the precipitation of the weather data
     *
     * @return The precipitation of the weather data
     */
    public String getmPrecipitation() {
        return mPrecipitation;
    }

    /**
     * Setter for the precipitation
     *
     * @param precipitation The new precipitation
     */
    public void setPrecipitation(String precipitation) {
        this.mPrecipitation = precipitation;
    }

    /**
     * Getter for the weather condition description of the weather data
     *
     * @return The weather condition description of the weather data
     */
    public String getWeatherDescription() {
        return mWeatherDescription;
    }

    /**
     * Setter for the weather description
     *
     * @param weatherDescription The new weather description
     */
    public void setWeatherDescription(String weatherDescription) {
        this.mWeatherDescription = weatherDescription;
    }

    /**
     * Getter for the timestamp, when the weather data was measured
     *
     * @return The timestamp, when the weather data was measured
     */
    public long getDataTimestamp() {
        return mDataTimestamp;
    }

    /**
     * Setter for the measurement timestamp
     *
     * @param dataTimestamp The new measurement timestamp
     */
    public void setDataTimestamp(long dataTimestamp) {
        this.mDataTimestamp = dataTimestamp;
    }

    /**
     * Getter for the timestamp, when the weather data was requested
     *
     * @return The timestamp, when the weather data was requested
     */
    public long getRequestTimestamp() {
        return mRequestTimestamp;
    }

    /**
     * Setter for the request timestamp
     *
     * @param requestTimestamp The new request timestamp
     */
    public void setmRequestTimestamp(long requestTimestamp) {
        this.mRequestTimestamp = requestTimestamp;
    }

    /**
     * Getter for the icon id of the weather data
     *
     * @return The precipitation of the weather data
     */
    public int getIconID() {
        return mIconID;
    }

    /**
     * Setter for the icon id
     *
     * @param iconID The new icon id
     */
    public void setmIconID(int iconID) {
        this.mIconID = iconID;
    }
}
