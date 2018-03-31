package com.jluandroid.myweather.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jluandroid.myweather.R;
import com.jluandroid.myweather.gson.DailyForecast;
import com.jluandroid.myweather.gson.LifeStyle;
import com.jluandroid.myweather.gson.Weather;
import com.jluandroid.myweather.util.HttpUtil;
import com.jluandroid.myweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    // UI部分
    private ScrollView weatherScrollView;
    private TextView titleCityTextView;
    private TextView titleUpdateTimeTextView;
    private TextView degreeTextView;
    private TextView weatherInfoTextView;
    private LinearLayout forecastLinearLayout;
    private TextView aqiTextView;
    private TextView pm25TextView;
    private TextView comfortTextView;
    private TextView carWashTextView;
    private TextView sportTextView;
    private ImageView backgroundImageView;

    public final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        // 初始化各控件
        weatherScrollView = findViewById(R.id.weather_ScrollView);
        titleCityTextView = findViewById(R.id.title_city_TextView_activityWeather);
        titleUpdateTimeTextView = findViewById(R.id.title_update_time_TextView_activityWeather);
        degreeTextView = findViewById(R.id.degree_TextView_now);
        weatherInfoTextView = findViewById(R.id.weather_info_TextView_nows);
        forecastLinearLayout = findViewById(R.id.forecast_LinearLayout);
        aqiTextView = findViewById(R.id.aqi_TextView);
        pm25TextView = findViewById(R.id.pm25_TextView);
        comfortTextView = findViewById(R.id.comfort_TextView);
        carWashTextView = findViewById(R.id.car_wash_TextView);
        sportTextView = findViewById(R.id.sport_TextView);
        backgroundImageView = findViewById(R.id.background_ImageView);
        // 读取或请求天气数据
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherScrollView.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        String savedImage = preferences.getString("back_image", null);
        if (savedImage != null) {
            GlideApp.with(WeatherActivity.this).load(savedImage).into(backgroundImageView);
        } else {
            loadBackImage();
        }
    }

    /**
     * 加载背景图片
     */
    private void loadBackImage() {
        final String imageUrl = "http://area.sinaapp.com/bingImg/";
        HttpUtil.sendOkHttpRequest(imageUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "onFailure: 加载图片出错啦", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String image = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("back_image", image);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GlideApp.with(WeatherActivity.this).load(R.mipmap.ic_launcher).into(backgroundImageView);
                    }
                });
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId) {
        Log.d(TAG, "requestWeather: weatherId的值为" + weatherId + "呀");
        String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" + weatherId
                + "&key=" + "c60771ee1f4343cc8d5e75d3acbf6727"
                + "&lang=" + getString(R.string.language);
        Log.d(TAG, "requestWeather: 整理的URL值为" + weatherUrl + "呀");
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, getString(R.string.toast_requestFailed), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, getString(R.string.toast_requestFailed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.whichCity;
        String updateTime = weather.update.locationTime;
        ///////////////////////////////////////温度单位
        String degree = weather.now.temperature + getString(R.string.temperatureUnit);
        String weatherInfo = weather.now.conditionText;
        titleCityTextView.setText(cityName);
        titleUpdateTimeTextView.setText(updateTime);
        degreeTextView.setText(degree);
        weatherInfoTextView.setText(weatherInfo);
        if (weather.forecastList != null) {
            for (DailyForecast dailyForecast : weather.forecastList) {
                View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item, forecastLinearLayout, false);
                TextView forecastDate = view.findViewById(R.id.date_TextView);
                TextView forecastInfo = view.findViewById(R.id.info_TextView);
                TextView forecastMax = view.findViewById(R.id.max_TextView);
                TextView forecastMin = view.findViewById(R.id.min_TextView);
                forecastDate.setText(dailyForecast.dateToday);
                String info = getString(R.string.day) + dailyForecast.conditionTextDay + " " + getString(R.string.night) + dailyForecast.conditionTextNight;
                forecastInfo.setText(info);
                String temp = dailyForecast.temperatureMax + getString(R.string.temperatureUnit);
                forecastMax.setText(temp);
                temp = dailyForecast.temperatureMin + getString(R.string.temperatureUnit);
                forecastMin.setText(temp);
                forecastLinearLayout.addView(view);
                Log.d(TAG, "showWeatherInfo: data的值为" + dailyForecast.dateToday + "呀");
                Log.d(TAG, "showWeatherInfo: info的值为" + dailyForecast.conditionTextDay +"呀");
                Log.d(TAG, "showWeatherInfo: max的值为" + dailyForecast.temperatureMax + "呀");
                Log.d(TAG, "showWeatherInfo: min的值为" + dailyForecast.temperatureMin + "呀");
            }
        } else {
            Log.e(TAG, "showWeatherInfo: weather.forecastList为空呀");
        }
        aqiTextView = findViewById(R.id.aqi_TextView);
        pm25TextView = findViewById(R.id.pm25_TextView);
        aqiTextView.setText("Hello");
        pm25TextView.setText("World");
        for (LifeStyle lifeStyle : weather.lifeStyleList) {
            if ("comf".equals(lifeStyle.descriptionType)) {
                comfortTextView = findViewById(R.id.comfort_TextView);
                comfortTextView.setText(lifeStyle.detailedDescription);
            } else if ("cw".equals(lifeStyle.descriptionType)) {
                carWashTextView = findViewById(R.id.car_wash_TextView);
                carWashTextView.setText(lifeStyle.detailedDescription);
            } else if ("sport".equals(lifeStyle.descriptionType)) {
                sportTextView = findViewById(R.id.sport_TextView);
                sportTextView.setText(lifeStyle.detailedDescription);
            }
        }
        weatherScrollView.setVisibility(View.VISIBLE);
    }
}
