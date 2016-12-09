package hotspotchat.abou7mied.me.hotspotchat.core;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;

import com.facebook.stetho.Stetho;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import hotspotchat.abou7mied.me.hotspotchat.R;
import hotspotchat.abou7mied.me.hotspotchat.net.Websocket;

/**
 * Created by abou7mied on 12/2/16.
 */

public class App extends Application {
    private NetService netService;
    public MyProfile myProfile;
    private static App instance;
    private Preferences preferences;

    public static App getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("App", "onCreate");
        Stetho.initializeWithDefaults(this);

        preferences = new Preferences(this);

        instance = this;
        Intent intent = new Intent(this, NetService.class);
        myProfile = new MyProfile(this);
        prepareMyProfile();

        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    public NetService getNetService() {
        return netService;
    }


    public void prepareMyProfile() {
        String name = getPreferences().getName();
        int avatar = getPreferences().getAvatar();

        if (name != null) {
            ((Profile) myProfile).setName(name);
            ((Profile) myProfile).setAvatar(avatar);
        }

    }

    public boolean isProfilePrepared() {
        return getPreferences().getName() != null;
    }


    public Preferences getPreferences() {
        return preferences;
    }

    public MyProfile getMyProfile() {
        return myProfile;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            NetService.ServiceBinder serviceBinder = (NetService.ServiceBinder) iBinder;
            netService = serviceBinder.getService();
            Log.e("Service", "connected!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            netService = null;
        }
    };
}
