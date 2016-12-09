package hotspotchat.abou7mied.me.hotspotchat.net.server;

import android.util.Log;

import com.google.gson.Gson;

import org.java_websocket.WebSocket;
import org.json.JSONException;

import hotspotchat.abou7mied.me.hotspotchat.core.Profile;
import hotspotchat.abou7mied.me.hotspotchat.net.WsMessage;
import hotspotchat.abou7mied.me.hotspotchat.net.client.WsClient;

/**
 * Created by abou7mied on 12/1/16.
 */

public class WsConnection {

    private WsServer wsServer;
    private WebSocket connection;

    private long lastPingTimestamp = System.currentTimeMillis();


    public WsConnection(WsServer wsServer, WebSocket connection) {
        this.wsServer = wsServer;
        this.connection = connection;
    }

    public void onMessage(String message) {
        WsMessage wsMessage = new Gson().fromJson(message, WsMessage.class);
        Log.e("message.getAction", wsMessage.getAction().toString());
        switch (wsMessage.getAction()) {
            case WsMessage.ACTION_SET_MY_DETAILS:
                try {
                    Profile profile = new Gson().fromJson(wsMessage.getData().getString(WsMessage.DATA_PROFILE_KEY), Profile.class);
                    getWsServer().addProfile(this, profile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

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
}
