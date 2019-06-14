package com.netbattletech.nbt;

public interface ISessionViewControl {
    void requestLobbyCreate(IPlayer owner, int lowerLimit, int upperLimit);
    void joinLobby(ILobby lobby);
    void postChat(String text);
    void onViewClose();
}
