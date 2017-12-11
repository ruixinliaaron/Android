package com.example.bohan.flyingpig;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.content.Intent;

import java.util.Calendar;
/**
 * Created by bohan on 12/4/17.
 */

public class TimeDisplayActivity extends AppCompatActivity {

    TextView hourDisplay;
    private Handler mHandler = new Handler();
    private static int SLEEP_TIME = 10000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        hourDisplay = (TextView)findViewById(R.id.hourDisplay);
        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        //hourDisplay.setText("We are not in Service now This app will close in 5 seconds");

        //finish();

        Runnable r = new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        };
        mHandler.postDelayed(r, SLEEP_TIME);

    }
}
