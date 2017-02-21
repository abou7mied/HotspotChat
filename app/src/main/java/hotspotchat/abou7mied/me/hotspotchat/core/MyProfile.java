package hotspotchat.abou7mied.me.hotspotchat.core;

/**
 * Created by abou7mied on 12/2/16.
 */

public class MyProfile extends Profile {

    private static MyProfile instance;
    private App app;

    MyProfile(App app) {
        this.app = app;
        instance = this;
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
        profile.setId(id);
        profile.setName(name);
        profile.setAvatar(avatar);
        return profile;
    }

    public static MyProfile getInstance() {
        return instance;
    }
}
