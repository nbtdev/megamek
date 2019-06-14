package com.netbattletech.nbt;

public interface ILobbyViewControl {
    void leaveLobby(ILobby lobby);
    void setReady(Boolean ready);
    void postChat(String text);
    void launch(ILobby lobby);
    void kick(ILobby lobby, IPlayer player);
    void onViewClose();
}
