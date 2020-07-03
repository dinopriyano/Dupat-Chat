package com.dupat.dupatchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class onlineStatus extends Service {
    private static final String TAG = "onlineStatus";
    FirebaseUser myAuth;
    DatabaseReference myRef;

    @Override
    public void onCreate() {
        super.onCreate();
        myAuth = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("user");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand");
        myRef.child(myAuth.getUid()).child("online_status").setValue("Online");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateNow = dateFormat.format(new Date());
        Log.d(TAG, "Service Destroy");
        myRef.child(myAuth.getUid()).child("online_status").setValue("Offline");
        myRef.child(myAuth.getUid()).child("last_seen").setValue(dateNow);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
