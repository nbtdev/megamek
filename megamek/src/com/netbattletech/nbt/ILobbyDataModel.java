package com.netbattletech.nbt;

public interface ILobbyDataModel {
    IChat chat();
    ILobby lobby();
    Boolean ready();
    Boolean isOwner();
    void postChat(IChatEntry chatEntry);
    void updateLobby(ILobby lobby);
}
