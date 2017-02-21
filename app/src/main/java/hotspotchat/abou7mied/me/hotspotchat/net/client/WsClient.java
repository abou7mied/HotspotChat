package hotspotchat.abou7mied.me.hotspotchat.net.client;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.WebSocket;
import org.java_websocket.client.*;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import hotspotchat.abou7mied.me.hotspotchat.activities.MainActivity;
import hotspotchat.abou7mied.me.hotspotchat.core.App;
import hotspotchat.abou7mied.me.hotspotchat.core.Preferences;
import hotspotchat.abou7mied.me.hotspotchat.core.Profile;
import hotspotchat.abou7mied.me.hotspotchat.fragments.ChatsFragment;
import hotspotchat.abou7mied.me.hotspotchat.messaging.Message;
import hotspotchat.abou7mied.me.hotspotchat.net.WsMessage;
import hotspotchat.abou7mied.me.hotspotchat.net.server.WsServer;

public class WsClient extends org.java_websocket.client.WebSocketClient {

    //    public static ArrayList<Profile> profiles = new ArrayList<Profile>();
//    public static ArrayList<Profile> profiles = new ArrayList<Profile>();
    public static ObservableArrayList<String> online = new ObservableArrayList<>();
//    public static HashMap<String, Profile> profiles = new HashMap();

    public static final int PING_INTERVAL = 3000;
    public static final String LOG_TAG = "WsClient";

    private long lastPongTimestamp = System.currentTimeMillis();
    private Timer pingTimer = new Timer(true);
    private ConnectionEvents connectionEventsListener;


    public WsClient(URI serverURI, ConnectionEvents connectionEvents) {
        super(serverURI);
        this.connectionEventsListener = connectionEvents;
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
        if (connectionEventsListener != null) {
            connectionEventsListener.onConnect();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
        WsMessage wsMessage = new Gson().fromJson(message, WsMessage.class);
        Type listType;

        switch (wsMessage.getAction()) {
            case WsMessage.ACTION_PROFILES_DETAILS:
                System.out.println("ACTION_PROFILES_DETAILS");

                try {
                    listType = new TypeToken<ArrayList<Profile>>() {
                    }.getType();
                    ArrayList<Profile> col = new Gson().fromJson(wsMessage.getData().getString(WsMessage.DATA_PROFILES_KEY), listType);

                    for (Profile profile : col) {
                        Profile.cacheProfile(profile);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case WsMessage.ACTION_ONLINE_USERS:
                listType = new TypeToken<ObservableArrayList<String>>() {
                }.getType();
                try {
                    ObservableArrayList<String> col = new Gson().fromJson(wsMessage.getData().getString(WsMessage.DATA_ONLINE_IDS_KEY), listType);

                    online.clear();
                    online.addAll(col);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (MainActivity.getInstance() != null) {
                    Log.e("getOnlineFragment", (MainActivity.getInstance().getOnlineFragment() != null) + "-");
                    MainActivity.getInstance().getOnlineFragment().notifyList();
                }

                break;

            case WsMessage.EVENT_RECEIVE_MESSAGES:
                try {
                    JSONArray col = new JSONArray(wsMessage.getData().getString(WsMessage.DATA_MESSAGES));
                    Preferences preferences = App.getInstance().getPreferences();

                    for (int i = 0; i < col.length(); i++) {
                        Message msg = new Gson().fromJson(col.getString(i), Message.class);
                        ChatsFragment.cacheMessage(msg, true);
                        boolean notificationEnabled = preferences.isNotificationEnabled();
                        Log.i("notificationEnabled", notificationEnabled + "_");

                        if (!ChatsFragment.isActive() && notificationEnabled && i == col.length() - 1 && msg.getId() > preferences.getLastMessageId() && !msg.getAuthor().equals(App.getInstance().getMyProfile().getId())) {
                            pushNotification(msg);
                        }

                        preferences.setLastMessageId(msg.getId());

                    }

                    if (ChatsFragment.getInstance(false) != null)
                        ChatsFragment.getInstance().notifyList();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;


        }


    }

    private void pushNotification(Message msg) {


        try {
            String notificationTitle = msg.getProfile().getName();
            String notificationText = msg.getText();

            Context applicationContext = App.getInstance();
            Intent intent = new Intent(applicationContext, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext,
                    0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(applicationContext)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(notificationTitle + ": " + notificationText)
                    .setSmallIcon(msg.getProfile().getAvatarRes())
                    .setWhen(System.currentTimeMillis())
                    .setWhen(msg.getId());


            NotificationManager notificationManager = (NotificationManager) applicationContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            notificationBuilder.setVibrate(new long[]{0, 150});
            notificationBuilder.setLights(Color.GREEN, 1000, 1000);

            Notification notification = notificationBuilder.build();
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;

            notificationManager.notify(0, notification);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onWebsocketPong(WebSocket conn, Framedata f) {
        super.onWebsocketPong(conn, f);
        lastPongTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }


    public long getLastPongTimestamp() {
        return lastPongTimestamp;
    }

    public void reconnect() {
        if (this.getConnectionEventsListener() != null)
            if (this.getConnection().isClosed() || this.getLastPongTimestamp() + WsServer.PING_TIMEOUT < System.currentTimeMillis()) {
                System.out.println("client should reconnect");
                pingTimer.cancel();
                pingTimer.purge();
                this.getConnectionEventsListener().onTimeout();
            }

    }

    public ConnectionEvents getConnectionEventsListener() {
        return connectionEventsListener;
    }


    public void sendMyDetails() {
        WsMessage wsMessage = new WsMessage(WsMessage.ACTION_PREPARE_CONNECTION);
        wsMessage.putData(WsMessage.DATA_PROFILE_KEY, new Gson().toJson(App.getInstance().getMyProfile().toProfile()));
        wsMessage.putData(Preferences.LAST_MESSAGE_ID, App.getInstance().getPreferences().getLastMessageId());
        this.send(wsMessage);
    }


    public void send(WsMessage message) throws NotYetConnectedException {
        super.send(new Gson().toJson(message));
    }

    public boolean isOpen() {
        return getConnection().isOpen();
    }

    public interface ConnectionEvents {
        void onConnect();

        void onTimeout();
    }

    public void sendPing() throws NotYetConnectedException {
        FramedataImpl1 frame = new FramedataImpl1(Framedata.Opcode.PING);
        frame.setFin(true);
        getConnection().sendFrame(frame);
    }

}
