package hotspotchat.abou7mied.me.hotspotchat.core;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import hotspotchat.abou7mied.me.hotspotchat.BR;
import hotspotchat.abou7mied.me.hotspotchat.R;
import hotspotchat.abou7mied.me.hotspotchat.database.DBController;

/**
 * Created by abou7mied on 12/2/16.
 */

public class Profile extends BaseObservable {


    public static final String PROFILE_ID_KEY = "id";
    public static final String PROFILE_NAME_KEY = "name";
    public static final String PROFILE_AVATAR_KEY = "avatar";

    static final int DEFAULT_AVATAR = 1;

    protected String id;
    protected String name;
    protected int avatar = 1;

    private static HashMap<String, Profile> cachedProfiles = new HashMap<>();


    public Profile() {

    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
        notifyPropertyChanged(BR.avatar);
        notifyPropertyChanged(BR.avatarRes);
    }


    @Bindable
    public String getId() {
        return id;
    }

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public int getAvatar() {
        return avatar;
    }

    @Bindable
    public int getAvatarRes() {
        int avatar = R.drawable.avatar_1;
        switch (this.avatar) {
            case 1:
                avatar = R.drawable.avatar_1;
                break;
            case 2:
                avatar = R.drawable.avatar_2;
                break;
            case 3:
                avatar = R.drawable.avatar_3;
                break;
            case 4:
                avatar = R.drawable.avatar_4;
                break;
            case 5:
                avatar = R.drawable.avatar_5;
                break;
            case 6:
                avatar = R.drawable.avatar_6;
                break;
            case 7:
                avatar = R.drawable.avatar_7;
                break;
            case 8:
                avatar = R.drawable.avatar_8;
                break;
            case 9:
                avatar = R.drawable.avatar_9;
                break;
            case 10:
                avatar = R.drawable.avatar_10;
                break;
            case 11:
                avatar = R.drawable.avatar_11;
                break;
        }
        return avatar;
    }


    static void loadProfiles() {
        JSONArray profiles = App.getInstance().getDbController().get(DBController.DB_TABLE_PROFILES);
        for (int i = 0; i < profiles.length(); i++) {
            try {
                Profile profile = new Gson().fromJson(profiles.getJSONObject(i).toString(), Profile.class);
                cachedProfiles.put(profile.getId(), profile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private static void cacheProfileIntoDb(JSONObject profile) {
        try {
            String id = profile.getString(Profile.PROFILE_ID_KEY);
            App.getInstance().getDbController().insertOrUpdate(DBController.DB_TABLE_PROFILES, profile, Profile.PROFILE_ID_KEY + "=?", new String[]{id});
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void cacheProfile(Profile profile) {
        cachedProfiles.put(profile.getId(), profile);
        try {
            JSONObject profileJSON = new JSONObject(new Gson().toJson(profile));
            cacheProfileIntoDb(profileJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static Profile getProfile(String id) {
        return cachedProfiles.get(id);
    }


}
