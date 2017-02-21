package hotspotchat.abou7mied.me.hotspotchat.net.server;

import android.util.Log;

import com.google.gson.Gson;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;

import hotspotchat.abou7mied.me.hotspotchat.core.App;
import hotspotchat.abou7mied.me.hotspotchat.core.Preferences;
import hotspotchat.abou7mied.me.hotspotchat.core.Profile;
import hotspotchat.abou7mied.me.hotspotchat.database.DBController;
import hotspotchat.abou7mied.me.hotspotchat.messaging.Message;
import hotspotchat.abou7mied.me.hotspotchat.net.WsMessage;

/**
 * Created by abou7mied on 12/1/16.
 */

public class WsConnection {

    private WsServer wsServer;
    private WebSocket connection;
    private Profile profile;
    private long lastMessageId;

    private long lastPingTimestamp = System.currentTimeMillis();


    public WsConnection(WsServer wsServer, WebSocket connection) {
        this.wsServer = wsServer;
        this.connection = connection;
    }

    public void onMessage(String message) {
        WsMessage wsMessage = new Gson().fromJson(message, WsMessage.class);
        switch (wsMessage.getAction()) {
            case WsMessage.ACTION_PREPARE_CONNECTION:
                try {
                    lastMessageId = wsMessage.getData().getLong(Preferences.LAST_MESSAGE_ID);
                    Profile profile = new Gson().fromJson(wsMessage.getData().getString(WsMessage.DATA_PROFILE_KEY), Profile.class);
                    setProfile(profile);
                    fetchMessages();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case WsMessage.ACTION_SEND_MESSAGE:
                try {
                    String messageJson = wsMessage.getData().getString(WsMessage.DATA_MESSAGE);
                    Message msg = new Gson().fromJson(messageJson, Message.class);
                    Message.cacheMessageIntoDb(msg);

                    JSONArray messages = new JSONArray();
                    messages.put(messageJson);

                    WsMessage reply = new WsMessage(WsMessage.EVENT_RECEIVE_MESSAGES);
                    reply.putData(WsMessage.DATA_MESSAGES, messages.toString());
                    getWsServer().broadcastMessage(reply);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;


        }

    }


    public void setProfile(Profile profile) {
        this.profile = profile;
        getWsServer().addOnlineUser(profile);
    }


    public void fetchMessages() {
        JSONArray messages = App.getInstance().getDbController().get(DBController.DB_TABLE_MESSAGES, null, Message.ID_KEY + ">?", new String[]{lastMessageId + ""}, null, null, null);
        WsMessage reply = new WsMessage(WsMessage.EVENT_RECEIVE_MESSAGES);
        reply.putData(WsMessage.DATA_MESSAGES, messages.toString());
        connection.send(new Gson().toJson(reply, WsMessage.class));
    }


    public long getLastPingTimestamp() {
        return lastPingTimestamp;
    }

    public void updateLastPingTimestamp() {
        this.lastPingTimestamp = System.currentTimeMillis();
    }

    public boolean isPingTimedout() {
        return getLastPingTimestamp() + WsServer.PING_TIMEOUT < System.currentTimeMillis();
    }

    public WebSocket getConnection() {
        return connection;
    }

    public WsServer getWsServer() {
        return wsServer;
    }

    public Profile getProfile() {
        return profile;
    }
}
