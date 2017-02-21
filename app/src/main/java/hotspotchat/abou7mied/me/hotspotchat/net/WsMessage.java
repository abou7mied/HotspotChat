package hotspotchat.abou7mied.me.hotspotchat.net;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abou7mied on 12/2/16.
 */

public class WsMessage {


    public static final String ACTION_PREPARE_CONNECTION = "prepareConnection";
    public static final String ACTION_PROFILES_DETAILS = "profilesDetails";
    public static final String ACTION_ONLINE_USERS = "onlineUsers";
    public static final String ACTION_FETCH_MESSAGES = "fetchMessages";

    public static final String ACTION_SEND_MESSAGE = "sendMessage";

    public static final String EVENT_RECEIVE_MESSAGES = "receiveMessages";


    public static final String DATA_PROFILE_KEY = "profile";
    public static final String DATA_PROFILES_KEY = "profiles";
    public static final String DATA_ONLINE_IDS_KEY = "onlineIds";

    public static final String DATA_MESSAGE = "message";
    public static final String DATA_MESSAGES = "messages";


    private String action;
    private JSONObject data = new JSONObject();


    public WsMessage(String action) {
        setAction(action);
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

    public void putData(String key, Object obj) {
        try {
            data.put(key, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getData() {
        return data;
    }


}
