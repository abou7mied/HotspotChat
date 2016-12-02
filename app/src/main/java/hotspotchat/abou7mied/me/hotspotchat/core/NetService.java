package hotspotchat.abou7mied.me.hotspotchat.core;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.java_websocket.WebSocket;

import hotspotchat.abou7mied.me.hotspotchat.net.Websocket;

/**
 * Created by abou7mied on 12/2/16.
 */

public class NetService extends Service {

    public static final String SERVICE_LOG_TAG = "netService";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(SERVICE_LOG_TAG, "Service Started");

        Websocket.startServer();
        Websocket.startClient();
    }

    public void printSomething() {
        Log.e(SERVICE_LOG_TAG, "Something!");
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


}
