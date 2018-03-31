package com.jluandroid.myweather.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jluandroid.myweather.R;
import com.jluandroid.myweather.handleFragmentBack.BackHandlerHelper;

/**
 * Created by Fengl on 2018/3/24.
 */

public class ChoosePositionActivity extends AppCompatActivity {

    private static final String TAG = "ChoosePositionActivity";
    public static final int GET_POSITION_STRING = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_position);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChoosePositionActivity.this);
        if (preferences.getString("weather", null) != null) {
            Intent intent = new Intent(ChoosePositionActivity.this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: 返回先传到了这里啦");
        // 如果子Fragment没有处理返回事件，则在此处处理
        if (!BackHandlerHelper.handleBackPress(this)) {
            Log.d(TAG, "onBackPressed: 最后一步啦，子Fragment都没有处理返回事件，交由系统处理啦");
            super.onBackPressed();
        }
    }

    public static void activityStartForResult(Activity activityFrom) {
        Intent intent = new Intent(activityFrom, ChoosePositionActivity.class);
        activityFrom.startActivityForResult(intent, GET_POSITION_STRING);
    }
}
