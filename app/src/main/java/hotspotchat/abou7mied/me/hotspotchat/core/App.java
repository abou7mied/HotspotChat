package hotspotchat.abou7mied.me.hotspotchat.core;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import hotspotchat.abou7mied.me.hotspotchat.ItemListActivity;

/**
 * Created by abou7mied on 12/2/16.
 */

public class App extends Application {
    private NetService netService;


    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, NetService.class);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            NetService.ServiceBinder serviceBinder = (NetService.ServiceBinder) iBinder;
            App.this.netService = serviceBinder.getService();
            Log.e("Service", "connected!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            App.this.netService = null;
        }
    };
}
