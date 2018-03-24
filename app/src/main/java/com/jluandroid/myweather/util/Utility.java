package com.jluandroid.myweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.jluandroid.myweather.db.City;
import com.jluandroid.myweather.db.County;
import com.jluandroid.myweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fengl on 2018/3/20.
 */

public class Utility {

    private static final String TAG = "Utility";

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                Log.d(TAG, "handleProvinceResponse: JSON解析省级数据完成啦");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "handleProvinceResponse: JSON解析省级数据时出错啦", e);
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                Log.d(TAG, "handleCityResponse: JSON解析市级数据成功啦");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "handleCityResponse: JSON处理市级数据时出错啦", e);
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                Log.d(TAG, "handleCountyResponse: JSON解析县级数据成功啦");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "handleCountyResponse: JSON处理县级数据时出错啦", e);
            }
        }
        return false;
    }
}
