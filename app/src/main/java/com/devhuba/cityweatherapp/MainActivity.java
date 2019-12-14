package com.devhuba.cityweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActivityChooserView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=a2c829889063b6f16d25c0d3693afe2f&units=metric";

    private MediaPlayer mediaPlayer;
    private CheckBox vCheckBoxMusic;
    private EditText vEditTextCity;
    private TextView vTextViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vEditTextCity = findViewById(R.id.editTextCity);
        vTextViewWeather = findViewById(R.id.textViewWeather);

        // CheckBox and MediaPlayer ...
        vCheckBoxMusic = findViewById(R.id.checkBoxMusic);
        mediaPlayer = MediaPlayer.create(this,R.raw.hp);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    public void isChecked(View view) {

    }

    public void onClickShowWeather(View view) {
        String city = vEditTextCity.getText().toString().trim();
        if (!city.isEmpty()) {
            DownloadWeatherTask task = new DownloadWeatherTask();
            String url = String.format(WEATHER_URL, city);
            task.execute(url);
        }
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                // Taking just string from object
                String jsonCity = jsonObject.getString("name");
                // Going to object and then taking String
                String jsonTemp = jsonObject.getJSONObject("main").getString("temp");
                // Going to Array after that to Object and then to String
                String jsonDescription = jsonObject.getJSONArray("weather")
                        .getJSONObject(0).getString("description");

                String jsonMainDescription = jsonObject.getJSONArray("weather")
                        .getJSONObject(0).getString("main");

                String jsonWeather = String.format("%s\nTemperature is: %s \u2103" +
                        "\nWeather description: %s" +
                        "\n", jsonCity,jsonTemp,jsonDescription ); // Saved all info in one string

                vTextViewWeather.setText(jsonWeather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
