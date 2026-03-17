package com.example;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import androidx.core.app.NotificationCompat;
import android.media.RingtoneManager;
import android.net.Uri;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Mensaje recibido de: " + remoteMessage.getFrom());
        
        try {
            JSONObject data = new JSONObject();
            
            // Datos de la notificación
            if (remoteMessage.getNotification() != null) {
                data.put("title", remoteMessage.getNotification().getTitle());
                data.put("body", remoteMessage.getNotification().getBody());
                data.put("sound", remoteMessage.getNotification().getSound());
                data.put("clickAction", remoteMessage.getNotification().getClickAction());
                data.put("wasTapped", false);
            }
            
            // Datos adicionales
            if (remoteMessage.getData().size() > 0) {
                JSONObject customData = new JSONObject();
                for (String key : remoteMessage.getData().keySet()) {
                    customData.put(key, remoteMessage.getData().get(key));
                }
                data.put("data", customData);
            }
            
            // Mostrar notificación en la barra de estado
            sendNotification(data);
            
            // Enviar a la capa JavaScript
            MyPushPlugin.sendNotification(data);
            
        } catch (JSONException e) {
            Log.e(TAG, "Error creando JSON", e);
        }
    }
    
    private void sendNotification(JSONObject data) {
        try {
            String title = data.getString("title");
            String body = data.getString("body");
            
            Intent intent = new Intent(this, com.example.MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            
            NotificationCompat.Builder notificationBuilder = 
                new NotificationCompat.Builder(this, "default_channel")
                    .setSmallIcon(getApplicationInfo().icon)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            
            NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            
            notificationManager.notify(0, notificationBuilder.build());
            
        } catch (Exception e) {
            Log.e(TAG, "Error mostrando notificación", e);
        }
    }
    
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Nuevo token generado: " + token);
        MyPushPlugin.sendToken(token);
    }
}