package com.dupat.dupatchat.instance;

import android.app.Application;
import android.content.Intent;

import com.dupat.dupatchat.service.onlineStatus;
import com.google.firebase.database.FirebaseDatabase;

public class DupatChat extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
