package hotspotchat.abou7mied.me.hotspotchat.core;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;


/**
 * Created by abou7mied on 12/2/16.
 */

public class Preferences {

    private static String PREFERENCE_FILE_KEY = "preferences";
    private static String MY_PROFILE_ID = "my_profile_id";
    private static String MY_PROFILE_NAME = "my_profile_name";
    private static String MY_PROFILE_AVATAR = "my_profile_avatar";
    private static String NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static String AP_NETWORK_NAME = "network_name";
    private static String AP_NETWORK_PASSWORD = "network_password";

    private static String DEFAULT_NETWORK_NAME = "HotSpotChat-" + ((int) Math.floor(Math.random() * 1000));
    private static String DEFAULT_NETWORK_PASSWORD = "";

    public static String LAST_MESSAGE_ID = "lastMessageId";

    private App app;

    private boolean notificationEnabled = true;
    private SharedPreferences sharedPref;
    private String networkName;
    private String networkPassword;
    private long lastMessageId;


    Preferences(App app) {
        this.app = app;
        sharedPref = app.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }

    void prepare() {
        getMyProfile().setId(sharedPref.getString(MY_PROFILE_ID, ""));

        if (getMyProfile().getId().equals(""))
            setProfileId(getMACAddress());


        getMyProfile().setName(sharedPref.getString(MY_PROFILE_NAME, null));
        getMyProfile().setAvatar(sharedPref.getInt(MY_PROFILE_AVATAR, Profile.DEFAULT_AVATAR));


        notificationEnabled = sharedPref.getBoolean(NOTIFICATIONS_ENABLED, true);
        networkName = sharedPref.getString(AP_NETWORK_NAME, null);

        lastMessageId = sharedPref.getLong(LAST_MESSAGE_ID, 0);


        if (networkName == null)
            setNetworkName(DEFAULT_NETWORK_NAME);

        networkPassword = sharedPref.getString(AP_NETWORK_PASSWORD, null);

        if (networkPassword == null)
            setNetworkPassword(DEFAULT_NETWORK_PASSWORD);

    }

    private MyProfile getMyProfile() {
        return this.app.getMyProfile();
    }


    private static String getMACAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF)).append(":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }


    private void setProfileId(String profileId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MY_PROFILE_ID, profileId);
        editor.apply();
        prepared();
    }

    public void setName(String name) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MY_PROFILE_NAME, name);
        editor.apply();
        prepared();
    }

    public void setAvatar(int avatar) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(MY_PROFILE_AVATAR, avatar);
        editor.apply();
        prepared();
    }

    private void prepared() {
        if (app.getNetService() != null)
            app.getNetService().startServerAndClient();
    }

    public void setNotificationsEnabled(boolean enabled) {
        notificationEnabled = enabled;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(NOTIFICATIONS_ENABLED, notificationEnabled);
        editor.apply();
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AP_NETWORK_NAME, networkName);
        editor.apply();
    }

    public String getNetworkPassword() {
        return networkPassword;
    }

    public void setNetworkPassword(String networkPassword) {
        this.networkPassword = networkPassword;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AP_NETWORK_PASSWORD, networkPassword);
        editor.apply();
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(LAST_MESSAGE_ID, lastMessageId);
        editor.apply();
    }
}
