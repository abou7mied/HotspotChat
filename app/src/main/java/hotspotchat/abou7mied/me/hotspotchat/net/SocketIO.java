package hotspotchat.abou7mied.me.hotspotchat.net;

import android.util.Log;

import com.corundumstudio.socketio.AckMode;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.netty.util.internal.ThreadLocalRandom;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by abou7mied on 12/1/16.
 */

public class SocketIO {


    public static final String LOG_TAG = "wsServer";
    public static final int PORT = ThreadLocalRandom.current().nextInt(1025, 9999);

    public static void startServer() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "start server on port:" + PORT);
                Configuration config = new Configuration();
                config.setPort(PORT);
//                config.setHostname("127.0.0.1");
                config.setOrigin("*");
                config.setTransports(Transport.WEBSOCKET);
                config.setAckMode(AckMode.AUTO_SUCCESS_ONLY);

                config.setAuthorizationListener(new AuthorizationListener() {
                    @Override
                    public boolean isAuthorized(HandshakeData data) {
                        return true;
                    }
                });

                SocketIOServer server = new SocketIOServer(config);

                server.addConnectListener(new ConnectListener() {
                    @Override
                    public void onConnect(SocketIOClient client) {
                        Log.i("client", "connected");
                    }
                });

                server.addDisconnectListener(new DisconnectListener() {
                    @Override
                    public void onDisconnect(SocketIOClient client) {
                        Log.i("client", "disconnected");
                    }
                });


                server.start();

            }
        }).start();
    }

    public static void startClient() {

        IO.Options options = new IO.Options();
        options.port = PORT;
        options.hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                Log.i(LOG_TAG, "socket: hostname: " + s);
                return true;
            }
        };

        Socket socket;
        try {
            socket = IO.socket("http://127.0.0.1:" + PORT, options);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.i(LOG_TAG, "socket: connected");
            }

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                for (Object ob : args) {
                    Log.i("event", ob.toString());
                }

            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.i(LOG_TAG, "socket: disconnected!!!");

            }

        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                for (Object ob : args) {
                    Log.i(LOG_TAG, "socket: Connect error " + ob.toString());
                }
            }

        });

        socket.connect();

    }


}

