package hotspotchat.abou7mied.me.hotspotchat.net;

import android.util.Log;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import hotspotchat.abou7mied.me.hotspotchat.net.client.WsClient;
import hotspotchat.abou7mied.me.hotspotchat.net.server.WsServer;

/**
 * Created by abou7mied on 12/1/16.
 */

public class Websocket {

    public static WsClient client;
    public static WsServer wsServer;
    public static WsClient.ConnectionEvents connectionEvents = new WsClient.ConnectionEvents() {
        @Override
        public void onConnect() {
            Log.i("websocket", "onConnect");

        }

        @Override
        public void onTimeout() {
            startClient(true);
        }
    };

    public static void startServer() {
        if (wsServer != null)
            return;

        wsServer = new WsServer();
        wsServer.run();
    }


    public static void startClient() {
        startClient(false);
    }

    public static WsClient startClient(boolean createNew) {

        if (client != null && !createNew) {
            if (client.isOpen())
                client.sendMyDetails();
            return client;
        }

        URI serverURI = null;
        try {
            serverURI = new URI("ws://" + WsServer.IP_ADDRESS + ":" + WsServer.PORT);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        client = new WsClient(serverURI, connectionEvents);
        client.connect();
        return client;

    }

    public static WsClient getClient() {
        return client;
    }

    public static void stopServer() {
        Log.i("websocket", "stop server");
        if (wsServer != null)
            try {
                wsServer.stop();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }


}
