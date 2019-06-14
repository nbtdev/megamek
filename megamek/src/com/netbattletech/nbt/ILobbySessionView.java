package com.netbattletech.nbt;

public interface ILobbySessionView {
    void activateSessionView(ISessionDataModel model, LobbySessionController controller);
    void activateLobbyView(ILobbyDataModel model, LobbySessionController controller);
    void updateActiveView();
    void close();
    void postError(String message);
    void postAnnouncement(String message);
    void postChat(IPlayer author, String message);
}
