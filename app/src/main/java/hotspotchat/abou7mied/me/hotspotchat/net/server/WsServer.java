package hotspotchat.abou7mied.me.hotspotchat.net.server;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class WsServer extends WebSocketServer {

    static {
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static int PING_TIMEOUT = 10000;
    private Timer heartBeatsTimer = new Timer(true);
    private HashMap<WebSocket, WsConnection> connections = new HashMap<>();


    public WsServer(InetSocketAddress address) {
        super(address);

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
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
        WsConnection wsConnection = new WsConnection(conn);
        connections.put(conn, wsConnection);
    }

    @Override
    public synchronized void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
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
}
