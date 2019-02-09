package de.vhoeher.openweatherapp.datasource;

import de.vhoeher.openweatherapp.model.ForecastModel;
import de.vhoeher.openweatherapp.model.WeatherDataModel;
import okhttp3.Callback;

/**
 * Interface for the DataSource instances.
 * This interface needs to be implemented by all used DataSources.
 *
 * @author Victor Hoeher
 * @version 1.0
 */
public interface IDataSource {

    /**
     * Getter for the DataSourceType
     *
     * @return The DataSource type of the  DataSource
     */
    DataSourceType getDataSourceType();

    /**
     * Setter for the API-KEy
     *
     * @param key The new key
     */
    void setAPIKey(String key);

    /**
     * Fetches the current weather by a given city name.
     *
     * @param city     The name of the city, from where the current weather should be fetched.
     * @param callback The callback, which should be called, when the data was fetched.
     */
    void fetchCurrentWeatherByCity(String city, Callback callback);

    /**
     * Fetches the current weather by given coordinate
     *
     * @param lat      The latitude value of the coordinate
     * @param lon      The longitude value of the coordinate
     * @param callback The callback, which should be called, when the data was fetched.
     */
    void fetchCurrentWeatherByCoordinates(double lat, double lon, Callback callback);

    /**
     * Fetches the weather forecast for a given city name.
     *
     * @param city     The name of the city, from where the weather forecast should be fetched.
     * @param callback The callback, which should be called, when the data was fetched.
     */
    void fetchForecastByCity(String city, Callback callback);

    /**
     * Fetches the weather forecast by given coordinate
     *
     * @param lat      The latitude value of the coordinate
     * @param lon      The longitude value of the coordinate
     * @param callback The callback, which should be called, when the data was fetched.
     */
    void fetchForecastByCoordinates(double lat, double lon, Callback callback);

    /**
     * Converts a JSON of the DataSource to a WeatherDataModel
     *
     * @param json The json, which should be converted
     * @return The parsed WeatherDataModel
     */
    WeatherDataModel convertJSONToSingleData(String json);

    /**
     * Converts a JSON of the DataSource to a ForecastModel
     *
     * @param json The json, which should be converted
     * @return The parsed ForecastModel
     */
    ForecastModel convertJSONToForecast(String json);

    /**
     * Check if the response code is valid
     *
     * @param code The response code
     * @return True of the code is valid, False if not
     */
    boolean isResponseCodeValid(int code);
}
