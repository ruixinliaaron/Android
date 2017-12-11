package com.example.bohan.flyingpig;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.bohan.flyingpig.clockService.ClockService;


/**
 * Created by bohan on 12/4/17.
 */

public class CheckUserActivity extends AppCompatActivity {
    Button chef;
    Button user;

    View thisView;
    static boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_usercheck);

        addListenerOnButton();

        thisView = (View) findViewById(R.id.drawer_layout_checkUser);
        thisView.getBackground().setAlpha(50);

        //start CountDown service:
        if(!flag) {
            startService(new Intent(this, ClockService.class));
            flag = true;
        }
    }


    public void addListenerOnButton() {

        user = (Button) findViewById(R.id.bUser);
        chef = (Button) findViewById(R.id.bChef);

        user.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intentClient = new Intent(CheckUserActivity.this, CustomerLogActivity.class);
                startActivity(intentClient);

            }

        });

        chef.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intentChef = new Intent(CheckUserActivity.this, ChefLogActivity.class);
                startActivity(intentChef);

            }

        });

    }


}


