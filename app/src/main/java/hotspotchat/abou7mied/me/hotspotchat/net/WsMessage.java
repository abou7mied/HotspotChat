package hotspotchat.abou7mied.me.hotspotchat.net;

import org.json.JSONObject;

/**
 * Created by abou7mied on 12/2/16.
 */

public class WsMessage {


    public static final String ACTION_SET_MY_DETAILS = "seMytDetails";
    public static final String ACTION_BROADCAST_PROFILES = "broadCastProfiles";

    public static final String DATA_PROFILE_KEY = "profile";
    public static final String DATA_PROFILES_KEY = "profiles";



    private String action;
    private JSONObject data = new JSONObject();


    public WsMessage() {

    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public JSONObject getData() {
        return data;
    }
}
