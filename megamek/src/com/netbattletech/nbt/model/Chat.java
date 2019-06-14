package com.netbattletech.nbt.model;

import com.netbattletech.nbt.IChat;
import com.netbattletech.nbt.IChatEntry;

import java.util.ArrayList;

public class Chat implements IChat {
    ArrayList<IChatEntry> chatEntries = new ArrayList<>();

    @Override
    public int lineCount() {
        return chatEntries.size();
    }

    @Override
    public IChatEntry line(int index) {
        if (index < 0 || index > chatEntries.size() - 1) {
            return null;
        }

        return chatEntries.get(index);
    }

    public void append(IChatEntry entry) {
        if (entry == null) {
            return;
        }

        chatEntries.add(entry);
    }
}
