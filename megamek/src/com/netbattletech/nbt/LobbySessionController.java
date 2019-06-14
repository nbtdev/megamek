package com.netbattletech.nbt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netbattletech.nbt.model.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class LobbySessionController extends WebSocketClient implements ISessionViewControl, ILobbyViewControl{
    enum Mode {
        SESSION_MODE,
        LOBBY_MODE,
    }

    Mode mode = Mode.SESSION_MODE;

    ILobbySessionView view;
    ISessionDataModel sessionDataModel;
    LobbyData lobbyDataModel;
    IPlayer player;

    public LobbySessionController(String lobbyServiceUrl) throws URISyntaxException {
        super(new URI(lobbyServiceUrl));
        sessionDataModel = new SessionData();
    }

    public void shutdown() {
        if (view != null) {
            view.close();
        }

        close();
    }

    public void setView(ILobbySessionView view) {
        this.view = view;
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

    /// WebSocketClient implementation
    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {
        System.out.println(message);
        ObjectMapper mapper = new ObjectMapper();
        CommandResponse response;

        try {
            response = mapper.readValue(message, mapper.getTypeFactory().constructParametricType(CommandResponse.class, Lobby.class));
        } catch (java.lang.Exception e) {
            try {
                response = mapper.readValue(message, mapper.getTypeFactory().constructParametricType(CommandResponse.class, String.class));
            } catch (IOException ex) {
                try {
                    response = mapper.readValue(message, mapper.getTypeFactory().constructParametricType(CommandResponse.class, ChatEntry.class));
                } catch (IOException ex2) {
                    try {
                        response = mapper.readValue(message, mapper.getTypeFactory().constructParametricType(CommandResponse.class, Player.class));
                    } catch (IOException ex3) {
                        ex3.printStackTrace();
                        return;
                    }
                }
            }
        }

        handleMessage(response);
    }

    void updateSessionLobbyList(List<Lobby> lobbies) {
        sessionDataModel.clearLobbies();
        for (Lobby l : lobbies) {
            sessionDataModel.addLobby(l);
        }
    }

    void handleMessage(CommandResponse response) {
        // process command results
        if (response.reason == ResponseReason.COMMAND) {
            if (response.result == CommandResult.SUCCESS) {
                if (mode == Mode.SESSION_MODE) {
                    if (response.command.equals("register")) {
                        view.activateSessionView(sessionDataModel, this);
                        lobbyDataModel = null;
                        player = (IPlayer)response.content;
                        setAttachment(player);
                        requestLobbyListing();
                    }

                    if (response.command.equals("create")) {
                        Lobby l = (Lobby) response.content;
                        sessionDataModel.addLobby(l);
                        lobbyDataModel = new LobbyData(l, getAttachment());
                        mode = Mode.LOBBY_MODE;
                        view.activateLobbyView(lobbyDataModel, this);
                    }

                    if (response.command.equals("join")) {
                        Lobby l = (Lobby) response.content;
                        lobbyDataModel = new LobbyData(l, getAttachment());
                        mode = Mode.LOBBY_MODE;
                        view.activateLobbyView(lobbyDataModel, this);
                    }

                    if (response.command.equals("list")) {
                        List<Lobby> lobbies = response.listContent;
                        updateSessionLobbyList(lobbies);
                        view.updateActiveView();
                    }
                } else {
                    if (response.command.equals("leave")) {
                        mode = Mode.SESSION_MODE;
                        view.activateSessionView(sessionDataModel, this);
                        lobbyDataModel = null;

                        // request an updated lobby listing
                        requestLobbyListing();
                    } else if (response.command.equals("ready")) {
                        if (lobbyDataModel != null) {
                            lobbyDataModel.setReady(true);
                            view.updateActiveView();
                        }
                    } else if (response.command.equals("not-ready")) {
                        if (lobbyDataModel != null) {
                            lobbyDataModel.setReady(false);
                            view.updateActiveView();
                        }
                    }
                }
            } else {
                view.postError((String)response.content);
            }
        }

        // process push notifications
        if (response.reason == ResponseReason.PUSH) {
            if (response.listContent != null) {
                // updated list of lobbies, this goes to the session data
                List<Lobby> lobbies = response.listContent;
                updateSessionLobbyList(lobbies);
                view.updateActiveView();
            } else {
                // could just be a lobby join?
                if (response.content instanceof ILobby) {
                    ILobby lobby = (ILobby)response.content;
                    lobbyDataModel.updateLobby(lobby);
                    view.updateActiveView();
                }
            }
        }

        // process announcements -- they go to chat with null author
        if (response.reason == ResponseReason.ANNOUNCEMENT) {
            String message = (String)response.content;
            view.postAnnouncement(message);
        }

        // CHAT should actually be a PUSH, but...it's basically an announcement with an author, so treat is as such
        if (response.reason == ResponseReason.CHAT) {
            ChatEntry chatMsg = (ChatEntry)response.content;
            view.postChat(chatMsg.author(), chatMsg.message());
        }
    }

    public void requestLobbyListing() {
        send("list");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println(String.format("Server closed connection. Reason: %s", reason));
    }

    @Override
    public void onError(java.lang.Exception ex) {

    }

    /// Things that can be done while in Session view
    @Override
    public void requestLobbyCreate(IPlayer owner, int lowerLimit, int upperLimit) {
        send(String.format("create %d %d", lowerLimit, upperLimit));
    }

    @Override
    public void joinLobby(ILobby lobby) {
        if (lobby == null) {
            return;
        }

        send(String.format("join %d", ((Lobby)lobby).getId()));
    }

    /// Things that can be done while in Lobby view
    @Override
    public void leaveLobby(ILobby lobby) {
        if (lobby == null) {
            return;
        }

        send(String.format("leave %d", ((Lobby)lobby).getId()));
    }

    @Override
    public void setReady(Boolean ready) {
        if (ready) {
            send("ready");
        } else {
            send("not-ready");
        }
    }

    @Override
    public void launch(ILobby lobby) {

    }

    @Override
    public void kick(ILobby lobby, IPlayer player) {

    }

    /// Things that can be done while in either view
    @Override
    public void postChat(String text) {
        send(String.format("chat %s", text));
    }

    @Override
    public void onViewClose() {
        if (mode == Mode.LOBBY_MODE) {
            // then go back to session mode
            mode = Mode.SESSION_MODE;
            view.activateSessionView(sessionDataModel, this);

            // request an updated lobby listing
            requestLobbyListing();
        } else {
            // actually closing up the session
            shutdown();
        }
    }
}
