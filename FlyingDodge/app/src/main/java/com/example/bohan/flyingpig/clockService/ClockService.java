package com.example.bohan.flyingpig.clockService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.bohan.flyingpig.InventoryActivity;

/**
 * Created by beiwen on 2017/12/4.
 */

public class ClockService extends Service {
    private boolean controllService = true;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        countTime();
        return Service.START_STICKY;
    }
    //implement CountDown function
    //decrease total time every one second interval and
    //send broadcast to InventoryActivity
    private void countTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent  intent= new Intent(InventoryActivity.CLOCK_ACTION);
                while(controllService){
                    try {
                        Thread.sleep(1000);
                        if(InventoryActivity.TIME == 0){
                            sendBroadcast(intent);
                            InventoryActivity.TIME = 60*60*1000;
                        }
                        InventoryActivity.TIME -= 1000;
                        sendBroadcast(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
