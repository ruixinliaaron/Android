package com.example.bohan.flyingpig;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {

    private static int SLEEP_TIME = 5000;
    private Handler mHandler = new Handler();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        Calendar rightNow = Calendar.getInstance();
        //rightNow.set(Calendar.HOUR_OF_DAY, 24);
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (2 ==3) {
            Intent outService = new Intent(WelcomeActivity.this, TimeDisplayActivity.class);
            startActivity(outService);
            finish();
        } else {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    if (user != null) {
                        Intent turn = new Intent(WelcomeActivity.this, MenuActivity.class);

                        String userEmail = user.getEmail();
                        turn.putExtra("userEmail", userEmail);
                        //mDatabase.child("customers").setValue(userEmail);
                        startActivity(turn);
                        finish();
                    } else {
                        Intent turn = new Intent(WelcomeActivity.this, CheckUserActivity.class);
                        startActivity(turn);
                        finish();
                    }

                }
            };
            mHandler.postDelayed(r, SLEEP_TIME);
        }
    }
}
