package megamek.common.net;

import megamek.common.net.marshall.PacketMarshaller;
import megamek.common.net.marshall.PacketMarshallerFactory;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class WebSocketClient {
    private WebSocketConnection connection = null;
    private PacketSerializer packetSerializer = null;
    private boolean connected = false;
    private int id;

    /**
     * Stub implementation of onPacketReceived(); subclass should
     * override this method to handle incoming packets.
     *
     * @param packet
     * @Note This method will be invoked on a separate thread, so subclass
     * must handle any thread synchronization issues.
     */
    protected void onPacketReceived(Packet packet) {
    }

    /**
     * Stub implementation of onRemoteDisconnection(); subclass should
     * override this method to handle disconnection initiated by remote endpoint
     * such as server.
     *
     * @Note This method will be invoked on a separate thread, so subclass
     * must handle any thread synchronization issues.
     */
    protected void onRemoteDisconnection() {
    }

    protected void onLocalDisconnection() {
    }

    private PacketSerializer getPacketSerializer() {
        if (packetSerializer == null) {
            packetSerializer = new PacketSerializer(PacketMarshallerFactory.getInstance().getMarshaller(PacketMarshaller.NATIVE_SERIALIZATION_MARSHALING));
        }

        return packetSerializer;
    }

    boolean webSocketConnect(URI uri) {
        try {
            org.java_websocket.client.WebSocketClient webSocketClient = new org.java_websocket.client.WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {

                }

                @Override
                public void onMessage(String message) {
                    // not implemented, textual messages not supported
                }

                @Override
                public void onMessage(ByteBuffer message) {
                    PacketSerializer serializer = getPacketSerializer();
                    try {
                        Packet packet = serializer.read(message);
                        onPacketReceived(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    if (remote) {
                        onRemoteDisconnection();
                    } else {
                        onLocalDisconnection();
                    }
                }

                @Override
                public void onError(Exception ex) {

                }
            };

            connection = new WebSocketConnection(webSocketClient);
            return webSocketClient.connectBlocking(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean connect(String serverUrl) {
        try {
            URI uri = new URI(serverUrl);
            return webSocketConnect(uri);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean connect(String hostname, int port) {
        try {
            URI uri = new URI("http", null, hostname, port, null, null, null);
            return webSocketConnect(uri);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void disconnect() {
        if (connection == null) {
            return;
        }

        connection.close();
    }

    protected void send(Packet packet) {
        if (connection == null) {
            return;
        }

        connection.send(packet);
    }

    protected boolean isConnected() {
        return (connection != null) && connected;
    }

    protected void serverHandshakeCompleted() {
        connected = true;
    }

    protected String getName() {
        return "(Unknown)";
    }

    protected void flushConn() {
        // not implemented
    }
}