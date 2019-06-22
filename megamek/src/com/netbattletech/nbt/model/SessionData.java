package com.netbattletech.nbt.model;

import com.netbattletech.nbt.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SessionData implements ISessionDataModel {
    ArrayList<ILobby> lobbies = new ArrayList<>();
    IChat chat;
    IPlayer player;

    public SessionData() {
        this.chat = new Chat();
    }

    @Override
    public List<ILobby> lobbies() {
        return Collections.unmodifiableList(this.lobbies);
    }

    @Override
    public IChat chat() {
        return this.chat;
    }

    @Override
    public void addLobby(ILobby lobby) {
        this.lobbies.add(lobby);
    }

    @Override
    public void updatePlayer(IPlayer player) { this.player = player; }

    @Override
    public void removeLobby(ILobby lobby) {
        Lobby _lobby = (Lobby)lobby;
        this.lobbies.removeIf(l -> ((Lobby)l).getId().equals(_lobby.getId()));
    }

    @Override
    public void postChat(IChatEntry chatEntry) {
        Chat _chat = (Chat)this.chat;
        _chat.append(chatEntry);
    }

    @Override
    public void clearLobbies() {
        this.lobbies.clear();
    }

    @Override
    public IPlayer player() { return player; }
}
