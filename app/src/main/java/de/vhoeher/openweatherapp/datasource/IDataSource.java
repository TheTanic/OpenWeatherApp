package de.vhoeher.openweatherapp.datasource;

import de.vhoeher.openweatherapp.model.WeatherDataModel;
import okhttp3.Callback;

public interface IDataSource {

    DataSourceType getDataSourceType();

    void setAPIKey(String key);

    void fetchCurrentWeatherByCity(String city, Callback callback);

    void fetchCurrentWeatherByCoordinates(double lat, double lon, Callback callback);

    void fetchForecastByCity(String city, Callback callback);

    void fetchForecastByCoordinates(double lat, double lon, Callback callback);

    WeatherDataModel convertJSONToSingleData(String json);

    boolean isResponseCodeValid(int code);
}
