package com.netbattletech.nbt.model;

import com.netbattletech.nbt.*;

public class LobbyData implements ILobbyDataModel {
    ILobby lobby;
    Chat chat;
    Boolean isReady;
    IPlayer self;
    Boolean launchEnabled;
    Boolean rejoinEnabled;

    public LobbyData(ILobby lobby, IPlayer self) {
        this.lobby = lobby;
        this.chat = new Chat();
        this.self = self;
        this.isReady = false;
        this.launchEnabled = false;
        this.rejoinEnabled = false;
    }

    @Override
    public IChat chat() {
        return this.chat;
    }

    @Override
    public ILobby lobby() {
        return this.lobby;
    }

    @Override
    public Boolean ready() {
        if (isReady == null) {
            return false;
        }

        return isReady;
    }

    @Override
    public Boolean isOwner() {
        if (self == null) {
            return false;
        }

        return self.id().equals(lobby.owner().id());
    }

    @Override
    public void postChat(IChatEntry chatEntry) {
        this.chat.append(chatEntry);
    }

    @Override
    public void updateLobby(ILobby lobby) {
        this.lobby.update(lobby);
    }

    public void setReady(Boolean ready) {
        this.isReady = ready;
    }

    @Override
    public boolean isLaunchEnabled() { return this.launchEnabled; }

    public void setLaunchEnabled(boolean enabled) { this.launchEnabled = enabled; }

    @Override
    public boolean isRejoinEnabled() { return this.rejoinEnabled; }

    public void setRejoinEnabled(boolean enabled) { this.rejoinEnabled = enabled; }
}
