package de.vhoeher.openweatherapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.vhoeher.openweatherapp.R;

/**
 * This Activity is responsible to help the user to get his OpenWeatherMap API-Key.
 * It uses a WebView for that task.
 *
 * @author Victor Hoeher
 * @version 1.0
 */
public class GetAPIKeyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set content
        setContentView(R.layout.activity_get_apikey);

        //Get WebView
        WebView webView = findViewById(R.id.openweathermap_webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });

        //Load URL
        webView.loadUrl("https://home.openweathermap.org/api_keys");
    }
}
