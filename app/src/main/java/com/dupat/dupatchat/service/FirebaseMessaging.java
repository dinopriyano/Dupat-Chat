package com.dupat.dupatchat.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "Data: "+remoteMessage.getData());
            Log.d(TAG, "sender uid: "+remoteMessage.getData().get("sender_uid"));
            Log.d(TAG, "type: "+remoteMessage.getData().get("type"));

        }
    }
}
