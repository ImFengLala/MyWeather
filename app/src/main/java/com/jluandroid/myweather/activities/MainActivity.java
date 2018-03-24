package com.jluandroid.myweather.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jluandroid.myweather.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.test_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoosePositionActivity.activityStartForResult(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ChoosePositionActivity.GET_POSITION_STRING:
                if (resultCode == RESULT_OK) {
                    String returnedData = data.getStringExtra(String.valueOf(ChoosePositionActivity.GET_POSITION_STRING));
                    TextView textView = findViewById(R.id.test_textView);
                    textView.setText(returnedData);
                    break;
                }
            default:
                break;
        }
    }
}
