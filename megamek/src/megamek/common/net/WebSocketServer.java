package megamek.common.net;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class WebSocketServer  {

    org.java_websocket.server.WebSocketServer webSocketServer;
    int port = -1;

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

            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {

            }

            @Override
            public void onMessage(WebSocket conn, String message) {
            }

            @Override
            public void onMessage(WebSocket conn, ByteBuffer message) {
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
            }

            @Override
            public void onStart() {

            }
        };

        webSocketServer.start();
        this.port = port;
        onListen();
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