package com.nilson.firebaseapp.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nilson.firebaseapp.R;
import com.nilson.firebaseapp.model.User;

import static com.nilson.firebaseapp.util.App.CHANNEL_1;

public class NotificationService extends Service {
    private ValueEventListener listener;
    private DatabaseReference receiveRef;

    @Override
    public void onCreate() {
        super.onCreate();
    }
    public void showNotify(User user){
        //criar notificação
        Notification notification = new NotificationCompat
                .Builder(getApplicationContext(),CHANNEL_1)
                .setSmallIcon(R.drawable.ic_account_circle_black_24dp)
                .setContentTitle("Alteração!")
                .setContentText(user.getNome())
                .setPriority(Notification.PRIORITY_HIGH)
                .build();

        //enviando channel
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1,notification);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //executado quando o serviço é chamado
        receiveRef = FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        listener = receiveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                showNotify(user);

                onRebind( new Intent());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        receiveRef.removeEventListener(listener);
        onStartCommand(null,0,1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
