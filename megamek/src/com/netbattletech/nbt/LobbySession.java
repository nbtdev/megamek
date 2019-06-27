package com.netbattletech.nbt;

import javax.swing.*;
import java.net.URISyntaxException;

public class LobbySession implements LobbySessionController.Listener {
    LobbySessionController controller;
    LobbySessionView view;

    public interface Listener {
        void onLaunched(String serverUrl);
    }

    Listener listener;

    public LobbySession(String lobbyServiceUrl, String callsign, JFrame parentFrame, Listener listener) throws URISyntaxException {
        this.listener = listener;
        controller = new LobbySessionController(lobbyServiceUrl, this);
        view = new LobbySessionView(parentFrame);
        controller.setView(view);
        controller.connect(callsign);
    }

    public void close() {
        controller.shutdown();
    }

    @Override
    public void onLaunched(String serverUrl) {
        listener.onLaunched(serverUrl);
    }
}
