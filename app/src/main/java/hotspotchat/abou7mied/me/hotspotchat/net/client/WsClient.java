package hotspotchat.abou7mied.me.hotspotchat.net.client;

import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import hotspotchat.abou7mied.me.hotspotchat.net.server.WsServer;

public class WsClient extends WebSocketClient {

    public static final int PING_INTERVAL = 3000;

    private long lastPongTimestamp = System.currentTimeMillis();
    private Timer pingTimer = new Timer(true);


    private ConnectionTimeout connectionTimeoutListener;

    public WsClient(URI serverURI) {
        super(serverURI);
    }

    public WsClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
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
        System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    @Override
    public void onPong() {
        System.out.println("client: onPong");
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


    public interface ConnectionTimeout {
        void onTimeout();
    }

}
