package com.netbattletech.nbt;

import com.fasterxml.jackson.annotation.JsonInclude;

enum CommandResult {
    SUCCESS,
    FAILED,
}

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CommandResponse<T> extends ServiceResponse<T> {
    public String command;
    public CommandResult result;
}
