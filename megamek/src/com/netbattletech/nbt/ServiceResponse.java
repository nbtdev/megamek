package com.netbattletech.nbt;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

enum ResponseReason {
    PUSH,
    COMMAND,
    ERROR,
    CHAT,
    ANNOUNCEMENT,
    LAUNCH,
}

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ServiceResponse<T> {
    public ResponseReason reason;
    public T content;
    public List<T> listContent;
}
