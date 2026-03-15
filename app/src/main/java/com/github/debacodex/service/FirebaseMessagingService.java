package com.github.debacodex.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.RemoteMessage;
import com.github.debacodex.helper.NotificationHelper;
import com.github.debacodex.R;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
	
	private final String TAG = getClass().getSimpleName();
	
	public FirebaseMessagingService() {
	}
	
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);
		sendNotif(remoteMessage);
	}
	
	@Override
	public void onNewToken(@NonNull String s) {
		super.onNewToken(s);
	}
	
	private void sendNotif(RemoteMessage remoteMessage) {
		Log.e(TAG, "sendNotif: " + remoteMessage.getData().get("id"));
		Log.e(TAG, "sendNotif: " + remoteMessage.getNotification().getTitle());
		Log.e(TAG, "sendNotif: " + remoteMessage.getNotification().getBody());
		
		final NotificationHelper notificationHelper = new NotificationHelper(this);
		notificationHelper.createNotification("Reminder", "Apakah anda sudah mengisi DSR hari ini?");
	}
}