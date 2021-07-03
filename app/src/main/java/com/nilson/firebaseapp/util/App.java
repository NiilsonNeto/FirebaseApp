package com.nilson.firebaseapp.util;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.nio.channels.Channel;

public class App extends Application {
    public static final String CHANNEL_1 = "ch_1";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationsChannels();
    }
    private void createNotificationsChannels(){
        //verificar se o celular tem API 26+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //criar canais de notificação
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_1,"Canal 1", NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Este é o canal 1");
            //registar canal
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }
}
