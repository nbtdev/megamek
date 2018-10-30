package megamek.common.net;

import megamek.common.net.marshall.PacketMarshaller;
import megamek.common.net.marshall.PacketMarshallerFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class WebSocketConnection implements IConnection {
    private WebSocket webSocket;
    private PacketSerializer packetSerializer = null;
    private int id;
    private List<ConnectionListener> connectionListeners = new ArrayList<>();
    private int bytesSent = 0;
    private int bytesRecv = 0;

    public WebSocketConnection(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    /**
     * Establish a client connection to a listening endpoint
     * @param uri
     * @param id
     */
    public WebSocketConnection(URI uri, int id) {
        this.id = id;
        this.webSocket = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                for (ConnectionListener l : WebSocketConnection.this.connectionListeners) {
                    l.connected(new ConnectedEvent(WebSocketConnection.this));
                }
            }

            @Override
            public void onMessage(String message) {
                // not implemented
            }

            @Override
            public void onMessage(ByteBuffer message) {
                PacketSerializer serializer = getPacketSerializer();

                try {
                    Packet packet = serializer.read(message);
                    for (ConnectionListener l : WebSocketConnection.this.connectionListeners) {
                        l.packetReceived(new PacketReceivedEvent(WebSocketConnection.this, packet));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (!remote) {
                    return;
                }

                for (ConnectionListener l : WebSocketConnection.this.connectionListeners) {
                    l.disconnected(new DisconnectedEvent(WebSocketConnection.this));
                }
            }

            @Override
            public void onError(Exception ex) {
                // not implemented
            }
        };
    }

    PacketSerializer getPacketSerializer() {
        if (packetSerializer == null) {
            packetSerializer = new PacketSerializer(PacketMarshallerFactory.getInstance().getMarshaller(PacketMarshaller.NATIVE_SERIALIZATION_MARSHALING));
        }

        return packetSerializer;
    }

    /**
     * IConnection overrides
     */

    @Override
    public boolean open() {
        return true;
    }

    @Override
    public void close() {
        webSocket.close();

        for (ConnectionListener l : connectionListeners) {
            l.disconnected(new DisconnectedEvent(this));
        }

        webSocket = null;
    }

    @Override
    public boolean isClosed() {
        return webSocket == null;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getInetAddress() {
        return webSocket.getLocalSocketAddress().toString();
    }

    @Override
    public void update() {
        // not implemented
    }

    @Override
    public void flush() {
        // not implemented
    }

    @Override
    public void send(Packet packet) {
        PacketSerializer serializer = getPacketSerializer();

        try {
            byte[] frameData = serializer.write(packet);
            webSocket.send(frameData);
            bytesSent += frameData.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasPending() {
        return false;
    }

    @Override
    public long bytesSent() {
        return bytesSent;
    }

    @Override
    public long bytesReceived() {
        return bytesRecv;
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    /**
     * Check to see if this IConnection wraps the given WebSocket instance
     * @param ws
     * @return
     */
    boolean wraps(WebSocket ws) {
        return webSocket == ws;
    }
}
