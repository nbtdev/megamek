package com.netbattletech.nbt.model;

import com.netbattletech.nbt.IChatEntry;
import com.netbattletech.nbt.IPlayer;

public class ChatEntry implements IChatEntry {
    private Player author;
    private String message;

    public ChatEntry() {

    }

    public ChatEntry(Player author, String message) {
        this.setAuthor(author);
        this.setMessage(message);
    }

    @Override
    public IPlayer author() {
        return this.author;
    }

    @Override
    public String message() {
        return this.message;
    }

    public void setAuthor(Player author) {
        this.author = author;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
