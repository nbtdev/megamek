package com.netbattletech.nbt;

import javax.swing.*;
import java.net.URISyntaxException;

public class LobbySession {
    LobbySessionController controller;
    LobbySessionView view;

    public LobbySession(String lobbyServiceUrl, String callsign, JFrame parentFrame) throws URISyntaxException {
        controller = new LobbySessionController(lobbyServiceUrl);
        view = new LobbySessionView(parentFrame);
        controller.setView(view);
        controller.connect(callsign);
    }

    public void close() {
        controller.shutdown();
    }
}
