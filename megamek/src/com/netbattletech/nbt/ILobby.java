package com.netbattletech.nbt;

public interface ILobby {
    IPlayer owner();
    int playerCount();
    IPlayer player(int index);
    int lowerLimit();
    int upperLimit();
    void addPlayer(IPlayer player);
    void removePlayer(IPlayer player);
    void update(ILobby other);
}
