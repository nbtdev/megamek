package megamek.common.net;

import org.java_websocket.WebSocket;
import java.net.URL;

public class WebSocketClient {
    private WebSocket webSocket = null;
    int id;

    // client connections
    public WebSocketClient(URL url, int id) {
        // use url to open connection (create WebSocket) to URL
        this.id = id;
    }

    // server connections
    public WebSocketClient(WebSocket webSocket, int id) {
        this.webSocket = webSocket;
        this.id = id;
    }

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
}