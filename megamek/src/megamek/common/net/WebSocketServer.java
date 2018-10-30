package megamek.common.net;

import megamek.common.net.marshall.PacketMarshaller;
import megamek.common.net.marshall.PacketMarshallerFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class WebSocketServer {

    private org.java_websocket.server.WebSocketServer webSocketServer;
    private List<WebSocketConnection> connections = new ArrayList<>();
    private PacketSerializer packetSerializer = null;
    private int port = -1;

    /**
     * Called after the server is in a state ready to accept new incoming connections.
     * @Note: This method is a stub and is intended to be overridden by subclasses that
     *        wish to be notified of this event
     */
    protected void onListen() {
    }

    /**
     * Called after the server is no longer in a condition to accept new incoming connections (typically
     * when the server is being shut down).
     * @Note: This method is a stub and is intended to be overridden by subclasses that
     *        wish to be notified of this event
     */
    protected void onListenEnded() {
    }

    /**
     * Called when a new incoming connection has been accepted. At the time this method is invoked, the
     * connection is not fully initialized (for example, any unique connection ID should be set by the
     * subclass override of this method).
     * @param connection The new incoming connection.
     * @Note: This method is a stub and is intended to be overridden by subclasses that
     *        wish to be notified of this event
     * @Note: This method will be invoked from a different thread and it is the responsibility
     *        of the subclass to manage any cross-thread synchronization needed.
     */
    protected void onConnectionOpen(IConnection connection) {
    }

    /**
     * Called when an existing connection has closed (when the remote client initiates the disconnection). At
     * the time that this method is invoked, the connection has been closed and any messages sent on the connection
     * will fail to reach the intended destination.
     * @param connection The connection that has been closed.
     * @Note: This method is a stub and is intended to be overridden by subclasses that
     *        wish to be notified of this event
     * @Note: This method will be invoked from a different thread and it is the responsibility
     *        of the subclass to manage any cross-thread synchronization needed.
     */
    protected void onConnectionClose(IConnection connection) {
    }

    /**
     * Called when a packet arrives on an existing connection.
     * @param connection The connection receiving the packet.
     * @param packet The packet that arrived on the connection.
     * @Note: This method is a stub and is intended to be overridden by subclasses that
     *        wish to be notified of this event
     * @Note: This method will be invoked from a different thread and it is the responsibility
     *        of the subclass to manage any cross-thread synchronization needed.
     */
    protected void onPacketReceived(IConnection connection, Packet packet) {
    }

    protected void listen(int port) throws IOException {
        webSocketServer = new org.java_websocket.server.WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                WebSocketConnection wsConn = new WebSocketConnection(conn);
                conn.setAttachment(wsConn);
                connections.add(wsConn);
                onConnectionOpen(wsConn);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                for (WebSocketConnection wsConn : connections) {
                    if (wsConn.wraps(conn)) {
                        connections.remove(wsConn);
                        wsConn.close();
                        onConnectionClose(wsConn);
                        return;
                    }
                }
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                // we don't have textual message support
            }

            @Override
            public void onMessage(WebSocket conn, ByteBuffer message) {
                WebSocketConnection wsConn = null;
                for (WebSocketConnection c : connections) {
                    if (c.wraps(conn)) {
                        wsConn = c;
                        break;
                    }
                }

                if (wsConn == null) {
                    return;
                }

                if (packetSerializer == null) {
                    packetSerializer = new PacketSerializer(PacketMarshallerFactory.getInstance().getMarshaller(PacketMarshaller.NATIVE_SERIALIZATION_MARSHALING));
                }
                try {
                    onPacketReceived(wsConn, packetSerializer.read(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onStart() {
                setConnectionLostTimeout(0);
                setConnectionLostTimeout(1000);
                onListen();
            }
        };

        webSocketServer.start();
        this.port = port;
    }

    protected int getLocalPort() {
        return port;
    }

    protected void stopNetwork() throws Exception {
        if (webSocketServer == null) {
            return;
        }

        webSocketServer.stop();
        onListenEnded();
    }
}