package com.netbattletech.nbt;

import megamek.client.ui.swing.nbt.LobbyDialog;
import megamek.client.ui.swing.nbt.LookingForGameDialog;

import javax.swing.*;

public class LobbySessionView implements ILobbySessionView {
    public interface Target {
        void onAnnouncement(String message);
        void onChat(IPlayer author, String message);
        void onError(String message);
        void closeDialog();
        void update();
    }

    Target activeTarget;
    JFrame parentFrame;

    public LobbySessionView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    @Override
    public void activateSessionView(ISessionDataModel model, LobbySessionController controller) {
        if (activeTarget != null) {
            activeTarget.closeDialog();
        }

        LookingForGameDialog dlg = new LookingForGameDialog(parentFrame, model, controller);
        dlg.setVisible(true);
        activeTarget = dlg;
    }

    @Override
    public void activateLobbyView(ILobbyDataModel model, LobbySessionController controller) {
        if (activeTarget != null) {
            activeTarget.closeDialog();
        }

        LobbyDialog dlg = new LobbyDialog(parentFrame, model, controller);
        dlg.setVisible(true);
        activeTarget = dlg;
    }

    @Override
    public void updateActiveView() {
        activeTarget.update();
    }

    @Override
    public void close() {

    }

    @Override
    public void postError(String message) {

    }

    @Override
    public void postAnnouncement(String message) {
        activeTarget.onAnnouncement(message);
    }

    @Override
    public void postChat(IPlayer author, String message) {
        activeTarget.onChat(author, message);
    }
}
