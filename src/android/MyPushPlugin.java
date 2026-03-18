package com.example;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import android.util.Log;
import android.content.Context;

public class MyPushPlugin extends CordovaPlugin {
    private static final String TAG = "MyPushPlugin";
    private static CallbackContext notificationCallbackContext;
    private static CallbackContext tokenCallbackContext;
    
    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        
        // 🔥 Inicializar Firebase automáticamente
        Context context = this.cordova.getActivity().getApplicationContext();
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context);
            Log.d(TAG, "Firebase inicializado automáticamente");
        }
    }
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "Ejecutando acción: " + action);
        
        if (action.equals("getToken")) {
            this.getToken(callbackContext);
            return true;
        } else if (action.equals("onNotification")) {
            this.onNotification(callbackContext);
            return true;
        } else if (action.equals("onTokenRefresh")) {
            this.onTokenRefresh(callbackContext);
            return true;
        }
        return false;
    }
    
    private void getToken(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(() -> {
            try {
                FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String token = task.getResult();
                            Log.d(TAG, "Token obtenido: " + token);
                            callbackContext.success(token);
                        } else {
                            Exception e = task.getException();
                            Log.e(TAG, "Error obteniendo token", e);
                            callbackContext.error("Error obteniendo token: " + (e != null ? e.getMessage() : "desconocido"));
                        }
                    });
            } catch (Exception e) {
                Log.e(TAG, "Excepción en getToken", e);
                callbackContext.error(e.getMessage());
            }
        });
    }
    
    private void onNotification(CallbackContext callbackContext) {
        notificationCallbackContext = callbackContext;
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
        Log.d(TAG, "Listener de notificaciones registrado");
    }
    
    private void onTokenRefresh(CallbackContext callbackContext) {
        tokenCallbackContext = callbackContext;
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
        Log.d(TAG, "Listener de token refresh registrado");
    }
    
    public static void sendNotification(JSONObject data) {
        if (notificationCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, data);
            result.setKeepCallback(true);
            notificationCallbackContext.sendPluginResult(result);
            Log.d(TAG, "Notificación enviada a JS: " + data.toString());
        }
    }
    
    public static void sendToken(String token) {
        if (tokenCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, token);
            result.setKeepCallback(true);
            tokenCallbackContext.sendPluginResult(result);
            Log.d(TAG, "Token enviado a JS: " + token);
        }
    }
}