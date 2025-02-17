package com.contest.parking.domain;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.contest.parking.R;
import com.contest.parking.presentation.MainActivity;

public class PrenotazioneReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Verifica se il permesso per le notifiche è concesso (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permesso non concesso: non mostrare la notifica oppure gestisci diversamente
                return;
            }
        }

        String message = intent.getStringExtra("message");
        if (message == null) {
            message = "Domani inizia la tua prenotazione!";
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "prenotazione_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Promemoria Prenotazione")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        builder.setContentIntent(contentIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1001, builder.build());
    }
}
