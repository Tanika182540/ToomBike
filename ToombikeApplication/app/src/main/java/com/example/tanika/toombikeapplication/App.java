package com.example.tanika.toombikeapplication;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CH_1 = "ch_1";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CH_1,"CH_1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("เราได้รับข้อมูลของคุณแล้ว เรากำลังไปหาคุณ!");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
