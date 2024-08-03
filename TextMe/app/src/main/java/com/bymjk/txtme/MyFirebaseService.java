package com.bymjk.txtme;

import static android.os.Build.VERSION.SDK_INT;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bymjk.txtme.Activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            sendNotification(notification.getTitle(), notification.getBody());
        }
    }

    PendingIntent pendingIntent;

    void sendNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        String channelId = "1"; /*getString(R.string.default_notification_channel_id)*/
        Uri customSoundUri = getRandomSoundUri();
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.sendbtn)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(customSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Chats",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Chats Related Notifications");
            channel.setSound(customSoundUri, null);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private Uri getRandomSoundUri() {
        Random random = new Random();
        int soundIndex = random.nextInt(2); // Generates 0 or 1
        if (soundIndex == 0) {
            return Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_1);
        } else {
            return Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_2);
        }
    }
}
