package hotspotchat.abou7mied.me.hotspotchat.core;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import hotspotchat.abou7mied.me.hotspotchat.R;
import hotspotchat.abou7mied.me.hotspotchat.net.Websocket;

/**
 * Created by abou7mied on 12/2/16.
 */

public class NetService extends Service {

    public static final String SERVICE_LOG_TAG = "netService";
    private App app;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(SERVICE_LOG_TAG, "Service Started");
        app = (App) getApplication();
        Websocket.startServer();

        startServerAndClient();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Websocket.stopServer();
    }

    public IBinder binder = new ServiceBinder();

    public class ServiceBinder extends Binder {
        public NetService getService() {
            return NetService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void startServerAndClient() {
        if (app.isProfilePrepared()) {
            Log.e(SERVICE_LOG_TAG, "Now user can connect");
            Websocket.startClient();
        } else {
            Log.e(SERVICE_LOG_TAG, "no name, user can't connect");

        }
    }


}
