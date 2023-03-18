package com.theelitedevelopers.teamup.push_notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.theelitedevelopers.teamup.R;
import com.theelitedevelopers.teamup.modules.main.home.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if(message.getData().size() > 0){
            JSONObject messageObject = new JSONObject(message.getData());
            try {
                String token = messageObject.get("to").toString();
                String title = messageObject.getJSONObject("data").get("title").toString();
                String body = messageObject.getJSONObject("data").get("body").toString();

                showNotification(title, body);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (message.getNotification() != null) {
            // Since the notification is received directly
            // from FCM, the title and the body can be
            // fetched directly as below.
            showNotification(
                    message.getNotification().getTitle(),
                    message.getNotification().getBody());
        }
    }

    // Method to display the notifications
    public void showNotification(String title,
                                 String message)
    {
        // Pass the intent to switch to the MainActivity
        Intent intent
                = new Intent(this, MainActivity.class);
        // Assign channel ID
        String channel_id = "notification_channel";
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.drawable.team_up_app_icon)
                .setAutoCancel(true)
                .setVibrate(new long[] { 1000, 1000, 1000,
                        1000, 1000 })
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);


        builder = builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.team_up_app_icon);

        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        NotificationManager notificationManager
                = (NotificationManager)getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "android_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }
}
