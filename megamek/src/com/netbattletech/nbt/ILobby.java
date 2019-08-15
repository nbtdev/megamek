package com.netbattletech.nbt;

public interface ILobby {
    int id();
    IPlayer owner();
    int playerCount();
    IPlayer player(int index);
    String serverUrl();
    int lowerLimit();
    int upperLimit();
    void addPlayer(IPlayer player);
    void removePlayer(IPlayer player);
    void update(ILobby other);
}
