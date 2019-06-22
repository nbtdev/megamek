package com.netbattletech.nbt;

import java.util.List;

public interface ISessionDataModel {
    List<ILobby> lobbies();
    IChat chat();
    IPlayer player();
    void addLobby(ILobby lobby);
    void removeLobby(ILobby lobby);
    void postChat(IChatEntry chatEntry);
    void updatePlayer(IPlayer player);
    void clearLobbies();
}
