package hotspotchat.abou7mied.me.hotspotchat.core;

import android.content.Context;
import android.content.SharedPreferences;

import hotspotchat.abou7mied.me.hotspotchat.R;

/**
 * Created by abou7mied on 12/2/16.
 */

public class Preferences {

    private static String PREFERENCE_FILE_KEY = "preferences";
    private static String MY_PROFILE_NAME = "my_profile_name";
    private static String MY_PROFILE_AVATAR = "my_profile_avatar";

    private App app;
    private String name;
    private int avatar;
    private SharedPreferences sharedPref;

    public Preferences(App app) {
        this.app = app;
        sharedPref = app.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        name = sharedPref.getString(MY_PROFILE_NAME, null);
        avatar = sharedPref.getInt(MY_PROFILE_AVATAR, 0);

    }

    public String getName() {
        return name;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setName(String name) {
        this.name = name;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MY_PROFILE_NAME, name);
        editor.commit();
        prepare();
    }


    public void setAvatar(int avatar) {
        this.avatar = avatar;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(MY_PROFILE_AVATAR, avatar);
        editor.commit();
        prepare();
    }

    public void prepare() {
        if (app.getNetService() != null)
            app.getNetService().startServerAndClient();
    }

}
