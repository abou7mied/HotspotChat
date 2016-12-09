package hotspotchat.abou7mied.me.hotspotchat.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.Bindable;

import hotspotchat.abou7mied.me.hotspotchat.R;

/**
 * Created by abou7mied on 12/2/16.
 */

public class MyProfile extends Profile {

    App app;

    public MyProfile(App app) {
        this.app = app;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        app.getPreferences().setName(name);
    }

    @Override
    public void setAvatar(int avatar) {
        super.setAvatar(avatar);
        app.getPreferences().setAvatar(avatar);
    }

    public Profile toProfile() {
        Profile profile = new Profile();
        profile.setName(name);
        profile.setAvatar(avatar);
        return profile;
    }
}
