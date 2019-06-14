package com.netbattletech.nbt;

import java.util.List;

public interface ISessionDataModel {
    List<ILobby> lobbies();
    IChat chat();
    void addLobby(ILobby lobby);
    void removeLobby(ILobby lobby);
    void postChat(IChatEntry chatEntry);
    void clearLobbies();
}
