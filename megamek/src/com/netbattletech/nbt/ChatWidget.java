package com.netbattletech.nbt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ChatWidget extends JPanel {
    JTextField chatEntry;
    JTextArea chat;

    public interface Listener {
        void onChat(String message);
    }

    ArrayList<Listener> listeners = new ArrayList<>();

    public ChatWidget() {
        GridBagConstraints c = new GridBagConstraints();
        setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;

        chat = new JTextArea();
        chat.setEditable(false);
        chat.setRows(6);
        c.weighty = 1.0;
        c.gridy = 0;
        JScrollPane chatScroller = new JScrollPane(chat);
        add(chatScroller, c);

        chatEntry = new JTextField();
        c.weighty = 0.0;
        c.gridy = 1;
        add(chatEntry, c);

        chatEntry.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Listener l : listeners) {
                    l.onChat(chatEntry.getText());
                }

                chatEntry.setText("");
            }
        });
    }

    public void onIncomingChat(IPlayer author, String message) {
        String authorName;
        if (author == null) {
            authorName = "SERVER";
        } else {
            authorName = author.callsign();
        }

        chat.append(String.format("[%s] %s\n", authorName, message));
    }

    public void addActionListener(Listener listener) {
        listeners.add(listener);
    }
}
