package com.netbattletech.nbt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class LobbyServiceClient extends WebSocketClient {
    public interface Listener {
        void onLobbyListUpdated(List<ILobby> lobbies);
        void onCurrentLobbyChanged(ILobby ILobby);
    }

    Listener listener;
    List<ILobby> lobbies = new ArrayList<>();
    ILobby currentILobby;

    public LobbyServiceClient(String url, Listener listener) throws URISyntaxException {
        super(new URI(url));
        this.listener = listener;
    }

    public LobbyServiceClient(String url) throws URISyntaxException {
        super(new URI(url));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {
        System.out.println(message);
        ObjectMapper mapper = new ObjectMapper();
        CommandResponse response;

        try {
            response = mapper.readValue(message, mapper.getTypeFactory().constructParametricType(CommandResponse.class, ILobby.class));
        } catch (java.lang.Exception e) {
            try {
                response = mapper.readValue(message, mapper.getTypeFactory().constructParametricType(CommandResponse.class, String.class));
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }

        // process command results
        if (response.reason == ResponseReason.COMMAND) {
            if (response.result == CommandResult.SUCCESS) {
                if (response.command.equals("create")) {
                    currentILobby = (ILobby) response.content;
                    listener.onCurrentLobbyChanged(currentILobby);
                }
                if (response.command.equals("leave")) {
                    listener.onCurrentLobbyChanged(null);
                }
                if (response.command.equals("join")) {
                    currentILobby = (ILobby) response.content;
                    listener.onCurrentLobbyChanged(currentILobby);
                }
                if (response.command.equals("list")) {
                    synchronized (lobbies) {
                        lobbies = response.listContent;
                        listener.onLobbyListUpdated(lobbies);
                    }
                }
            } else {
                System.out.println(response.content);
            }
        }

        // process push notifications
        if (response.reason == ResponseReason.PUSH) {
            synchronized (lobbies) {
                if (response.listContent != null) {
                    lobbies = response.listContent;
                    listener.onLobbyListUpdated(lobbies);
                } else {
                    // could just be a lobby join?
                    if (response.content instanceof ILobby) {

                    }
                }
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println(String.format("Server closed connection. Reason: %s", reason));
    }

    @Override
    public void onError(java.lang.Exception ex) {

    }

    public void connect(String callsign) {
        try {
            if (connectBlocking()) {
                send(String.format("register %s", callsign));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        close();
    }

    public void requestLobbyListing() {
        send("list");
    }

    public void requestNewLobby(int lowerLimit, int upperLimit) {
        send(String.format("create %d %d", lowerLimit, upperLimit));
    }

    public void joinLobby(Integer lobbyId) {
        send(String.format("join %d", lobbyId));
    }

    public void leaveLobby(Integer lobbyId) {
        send(String.format("leave %d", lobbyId));
    }
}
