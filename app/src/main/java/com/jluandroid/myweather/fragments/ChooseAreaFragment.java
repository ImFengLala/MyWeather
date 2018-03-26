package com.jluandroid.myweather.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jluandroid.myweather.R;
import com.jluandroid.myweather.activities.ChoosePositionActivity;
import com.jluandroid.myweather.db.City;
import com.jluandroid.myweather.db.County;
import com.jluandroid.myweather.db.Province;
import com.jluandroid.myweather.handleFragmentBack.FragmentBackHandler;
import com.jluandroid.myweather.util.HttpUtil;
import com.jluandroid.myweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Fengl on 2018/3/20.
 */

public class ChooseAreaFragment extends Fragment implements FragmentBackHandler, View.OnClickListener{
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressBar progressBar;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private static final String TAG = "ChooseAreaFragment";

    // 省列表
    private List<Province> provinceList;
    // 市列表
    private List<City> cityList;
    // 县列表
    private List<County> countyList;
    // 选中的省份
    private Province selectedProvince;
    // 选中的城市
    private City selectedCity;
    // 当前选中的级别
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 开始创建界面啦");
        View mView = inflater.inflate(R.layout.choose_area, container, false);
        // 初始化UI控件的值
        titleText = mView.findViewById(R.id.title_text_inChooseArea_TextView);
        backButton = mView.findViewById(R.id.back_button_inChooseArea_Button);
        listView = mView.findViewById(R.id.list_view_inChooseArea_ListView);
        progressBar = mView.findViewById(R.id.circle_ProgressBar);
        // 设置ListView的适配器
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        Log.d(TAG, "onCreateView: 创建好了界面啦");
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: 开始创建活动啦");
        super.onActivityCreated(savedInstanceState);
        // 设置ListView内容的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, "onItemClick: 点击了ListView中的内容啦");
                // 清屏并显示进度条
                dataList.clear();
                adapter.notifyDataSetChanged();
                showProgressBar();
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    // 点击了最终结果啦
                    String result = selectedProvince.getProvinceName() + " "
                                    + selectedCity.getCityName() + " "
                                    + countyList.get(position).getCountyName();
                    Log.d(TAG, "onItemClick: result的值是" + result + "呀");
                    Intent intent = new Intent();
                    intent.putExtra(String.valueOf((ChoosePositionActivity.GET_POSITION_STRING)), result);
                    getActivity().setResult(RESULT_OK, intent);
                    getActivity().finish();
                }
            }
        });
        // 设置返回按钮的点击事件
        backButton.setOnClickListener(ChooseAreaFragment.this);
        Log.d(TAG, "onActivityCreated: 创建活动的时候查询省级页面数据呀");
        queryProvinces();
        Log.d(TAG, "onActivityCreated: 创建活动完成啦");
    }

    // 设置返回按钮的点击事件
    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: 点击了左上的返回按钮啦");
        switch (view.getId()) {
            case R.id.back_button_inChooseArea_Button: {
                handleBack();
            }
        }
    }

    // 设置系统返回事件
    @Override
    public boolean onBackPressed() {
        Log.d(TAG, "onBackPressed: 然后这里才开始真正处理截获到的返回事件啦");
        return !(currentLevel == LEVEL_PROVINCE && HttpUtil.requestCount == 0) && handleBack();
    }

    /**
     * 被上面两个方法调用
     * @see #onBackPressed()
     * @see #onClick(View)
     * 处理fragment的返回事件的具体细节，避免代码重复
     * @return true 表示处理过了，false 表示不处理交由上层处理
     */
    // 返回事件的响应 调用这个方法时currentLevel不会是LEVEL_PROVINCE
    private boolean handleBack() {
        Log.d(TAG, "handleBack: 开始执行返回前的收尾工作啦");
        Log.d(TAG, "handleBack: 取消所有网络请求呀");
        if (HttpUtil.requestCount > 0) {
            HttpUtil.client.dispatcher().cancelAll();
        }
        // 清屏并显示进度条
        dataList.clear();
        adapter.notifyDataSetChanged();
        showProgressBar();
        Log.d(TAG, "handleBack: 清屏和显示进度条完成啦");
        if (HttpUtil.requestCount > 0) {
            HttpUtil.client.dispatcher().cancelAll();
            HttpUtil.requestCount = 0;
            if (currentLevel == LEVEL_PROVINCE) {
                // 现在在省级页面正在查询市级数据\
                queryProvinces();
                return true;
            } else if (currentLevel == LEVEL_CITY) {
                // 现在在市级页面正在查询县级数据
                queryCities();
                return true;
            }
        }
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        }
        return true;
    }

    // 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryProvinces() {
        Log.d(TAG, "queryProvinces: 查询省级数据啦");
        titleText.setText(getString(R.string.china));
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            Log.d(TAG, "queryProvinces: 从本地数据库查询省级数据啦");
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            // 为什么要设置已选中？
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
            progressBar.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "queryProvinces: 从网络查询省级数据啦");
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    // 查询选中省中所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryCities() {
        Log.d(TAG, "queryCities: 查询市级数据啦");
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            Log.d(TAG, "queryCities: 从本地数据库查询市级数据啦");
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
            progressBar.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "queryCities: 从网络查询市级数据啦");
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    // 查询选中市中所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            Log.d(TAG, "queryCounties: 从本地数据库查询县级数据啦");
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
            progressBar.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "queryCounties: 从网络查询县级数据啦");
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    // 根据传入的地址和类型从服务器上查询省市县数据
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call,@NonNull IOException e) {
                if (call.isCanceled()) {
                    Log.d(TAG, "onFailure: 取消网络请求啦");
                    return;
                }
                Log.e(TAG, "onFailure: 从服务器请求并解析异常啦", e);
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "从服务器请求并解析异常啦", Toast.LENGTH_SHORT).show();
                    }
                });
                if (currentLevel == LEVEL_PROVINCE) {
                    getActivity().finish();
                } else if (currentLevel == LEVEL_CITY) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryProvinces();
                        }
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call,@NonNull Response response) throws IOException {
                Log.d(TAG, "onResponse: 发送的网络请求得到相应啦" + address);
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                } else {
                    throw new IOException("JSON解析数据异常啦");
                }
            }
        });
    }

    // 显示进度条
    private void showProgressBar() {
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
    }
}