package hotspotchat.abou7mied.me.hotspotchat.net.server;

import org.java_websocket.WebSocket;

import hotspotchat.abou7mied.me.hotspotchat.net.client.WsClient;

/**
 * Created by abou7mied on 12/1/16.
 */

public class WsConnection {

    private WebSocket connection;

    private long lastPingTimestamp = System.currentTimeMillis();


    public WsConnection(WebSocket connection) {
        this.connection = connection;
    }

    public void onMessage(String message) {
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
}
