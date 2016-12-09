package hotspotchat.abou7mied.me.hotspotchat.net.client;

import android.databinding.ObservableArrayList;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import hotspotchat.abou7mied.me.hotspotchat.core.App;
import hotspotchat.abou7mied.me.hotspotchat.core.Profile;
import hotspotchat.abou7mied.me.hotspotchat.net.WsMessage;
import hotspotchat.abou7mied.me.hotspotchat.net.server.WsServer;

public class WsClient extends WebSocketClient {

    //    public static ArrayList<Profile> profiles = new ArrayList<Profile>();
//    public static ArrayList<Profile> profiles = new ArrayList<Profile>();
    public static ObservableArrayList<Profile> profiles = new ObservableArrayList<>();
    public static final int PING_INTERVAL = 3000;
    public static final String LOG_TAG = "WsClient";

    private long lastPongTimestamp = System.currentTimeMillis();
    private Timer pingTimer = new Timer(true);
    private ConnectionTimeout connectionTimeoutListener;


    public WsClient(URI serverURI) {
        super(serverURI);
    }


    @Override
    public void connect() {
        super.connect();
        pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                WsClient.this.sendPing();
                WsClient.this.reconnect();
            }
        }, PING_INTERVAL, PING_INTERVAL);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i(LOG_TAG, "connected");
        sendMyDetails();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
        WsMessage wsMessage = new Gson().fromJson(message, WsMessage.class);

        switch (wsMessage.getAction()) {
            case WsMessage.ACTION_BROADCAST_PROFILES:
                Log.i(LOG_TAG, "Woohooo!");
                try {
                    profiles.clear();
                    Type listType = new TypeToken<ArrayList<Profile>>() {
                    }.getType();
                    ArrayList<Profile> col = new Gson().fromJson(wsMessage.getData().getString(WsMessage.DATA_PROFILES_KEY), listType);
                    profiles.addAll(col);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }


    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    @Override
    public void onPong() {
        lastPongTimestamp = System.currentTimeMillis();
    }

    public long getLastPongTimestamp() {
        return lastPongTimestamp;
    }

    public void reconnect() {
        if (this.getConnectionTimeoutListener() != null)
            if (this.isClosed() || this.getLastPongTimestamp() + WsServer.PING_TIMEOUT < System.currentTimeMillis()) {
                System.out.println("client should reconnect");
                pingTimer.cancel();
                pingTimer.purge();
                this.getConnectionTimeoutListener().onTimeout();
            }

    }

    public ConnectionTimeout getConnectionTimeoutListener() {
        return connectionTimeoutListener;
    }

    public void setConnectionTimeoutListener(ConnectionTimeout connectionTimeoutListener) {
        this.connectionTimeoutListener = connectionTimeoutListener;
    }

    public void sendMyDetails() {
        WsMessage wsMessage = new WsMessage();
        wsMessage.setAction(WsMessage.ACTION_SET_MY_DETAILS);
        JSONObject data = new JSONObject();
        try {
            data.put("profile", new Gson().toJson(App.getInstance().getMyProfile().toProfile()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        wsMessage.setData(data);
        Gson gson = new Gson();
        this.send(gson.toJson(wsMessage));
    }


    public interface ConnectionTimeout {
        void onTimeout();
    }

}
