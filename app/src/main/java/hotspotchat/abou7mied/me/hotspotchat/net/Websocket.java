package hotspotchat.abou7mied.me.hotspotchat.net;

import android.util.Log;

import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import hotspotchat.abou7mied.me.hotspotchat.net.client.WsClient;
import hotspotchat.abou7mied.me.hotspotchat.net.server.WsServer;

/**
 * Created by abou7mied on 12/1/16.
 */

public class Websocket {

    public static final String LOG_TAG = "wsServer";
    public static final String IP_ADDRESS = "192.168.43.1";
    public static final int PORT = 8887;
    public static WebSocketServer server;
    public static WsClient client;

    public static void startServer() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                server = new WsServer(new InetSocketAddress(PORT));
                server.run();
            }
        }).start();

    }


    public static void startClient() {
        startClient(false);
    }

    public static synchronized void startClient(boolean createNew) {

        if (!createNew && client != null)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {

                URI serverURI = null;
                try {
                    serverURI = new URI("ws://" + IP_ADDRESS + ":" + PORT);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                Log.i("Start client", serverURI.toString());
                client = new WsClient(serverURI);
                client.setConnectionTimeoutListener(new WsClient.ConnectionTimeout() {
                    @Override
                    public void onTimeout() {
                        startClient(true);
                    }
                });
                client.connect();
            }
        }).start();

    }


    public static void stopServer() {
        Log.i("websocket", "stop server");

        if (server != null)
            try {
                server.stop();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }


}
