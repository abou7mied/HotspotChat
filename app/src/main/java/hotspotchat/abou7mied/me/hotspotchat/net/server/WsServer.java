package hotspotchat.abou7mied.me.hotspotchat.net.server;

import com.google.gson.Gson;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import hotspotchat.abou7mied.me.hotspotchat.core.Profile;
import hotspotchat.abou7mied.me.hotspotchat.net.WsMessage;

public class WsServer extends WebSocketServer {

    static {
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final String IP_ADDRESS = "192.168.43.1";
    public static final int PORT = 8887;

    public static int PING_TIMEOUT = 10000;
    private Timer heartBeatsTimer = new Timer(true);
    private HashMap<WebSocket, WsConnection> connections = new HashMap<>();
    private HashMap<WebSocket, Profile> profiles = new HashMap<>();


    public WsServer() {
        super(new InetSocketAddress(PORT));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
        WsConnection wsConnection = new WsConnection(this, conn);
        connections.put(conn, wsConnection);
    }

    @Override
    public void run() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                WsServer.super.run();
            }
        }).start();

        heartBeatsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Iterator it = getConnections().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    WsConnection wsConnection = (WsConnection) pair.getValue();
                    if (wsConnection.isPingTimedout()) {
                        wsConnection.getConnection().close();
                    }
                }
            }
        }, PING_TIMEOUT, PING_TIMEOUT);
    }

    @Override
    public synchronized void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        profiles.remove(conn);
        pushProfilesToConnections();
    }


    @Override
    public void onMessage(WebSocket conn, String message) {
        WsConnection wsConnection = connections.get(conn);
        if (wsConnection != null) {
            wsConnection.onMessage(message);
        }
    }


    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onWebsocketPing(WebSocket conn, Framedata f) {
        super.onWebsocketPing(conn, f);
        WsConnection wsConnection = connections.get(conn);
        if (wsConnection != null) {
            wsConnection.updateLastPingTimestamp();
        }
    }

    public HashMap<WebSocket, WsConnection> getConnections() {
        return connections;
    }

    public void addProfile(WsConnection webSocket, Profile profile) {
        profiles.put(webSocket.getConnection(), profile);
        pushProfilesToConnections();
    }

    public HashMap<WebSocket, Profile> getProfiles() {
        return profiles;
    }

    private void pushProfilesToConnections() {

        WsMessage wsMessage = new WsMessage();
        wsMessage.setAction(WsMessage.ACTION_BROADCAST_PROFILES);
        JSONObject data = new JSONObject();
        try {
            ArrayList<Profile> p = new ArrayList<>();
            for (Profile profile : getProfiles().values()) {
                p.add(profile);
            }
            data.put(WsMessage.DATA_PROFILES_KEY, new Gson().toJson(p));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        wsMessage.setData(data);
        broadcastMessage(wsMessage);

    }

    private void broadcastMessage(WsMessage wsMessage) {
        Iterator it = getConnections().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            WsConnection wsConnection = (WsConnection) pair.getValue();
            wsConnection.getConnection().send(new Gson().toJson(wsMessage));
        }

    }

}
