package hotspotchat.abou7mied.me.hotspotchat.core;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import hotspotchat.abou7mied.me.hotspotchat.database.DBController;
import hotspotchat.abou7mied.me.hotspotchat.fragments.ChatsFragment;
import hotspotchat.abou7mied.me.hotspotchat.net.NetWorkManager;

/**
 * Created by abou7mied on 12/2/16.
 */

public class App extends Application {
    private NetService netService;
    public MyProfile myProfile;
    private static App instance;
    private Preferences preferences;
    private NetWorkManager netWorkManager;
    private DBController dbController;

    public static App getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("App", "onCreate");

        instance = this;

        Stetho.initializeWithDefaults(this);

        dbController = new DBController(this);

        myProfile = new MyProfile(this);

        preferences = new Preferences(this);
        preferences.prepare();

        Intent intent = new Intent(this, NetService.class);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);


        netWorkManager = new NetWorkManager();
        netWorkManager.prepare(this);

        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        retrieveDBData();


    }

    private void retrieveDBData() {
        Profile.loadProfiles();
        ChatsFragment.loadMessages();
    }

    public NetService getNetService() {
        return netService;
    }


    public boolean isProfilePrepared() {
        return !(myProfile.getName() == null || myProfile.getName().equals(""));
    }


    public Preferences getPreferences() {
        return preferences;
    }

    public MyProfile getMyProfile() {
        return myProfile;
    }

    public NetWorkManager getNetWorkManager() {
        return netWorkManager;
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

    public DBController getDbController() {
        return dbController;
    }
}
