package de.vhoeher.openweatherapp.datasource;

import okhttp3.Callback;

public interface IDataSource {

    DataSourceType getDataSourceType();

    void setAPIKey(String key);

    void fetchCurrentWeatherByCity(String city, Callback callback);

    void fetchCurrentWeatherByCoordinates(double lat, double lon, Callback callback);

    void fetchForecastByCity(String city, Callback callback);

    void fetchForecastByCoordinates(double lat, double lon, Callback callback);
}
