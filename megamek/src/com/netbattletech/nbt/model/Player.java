package com.netbattletech.nbt.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.netbattletech.nbt.IPlayer;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Player implements IPlayer {
    private String callsign;
    private Integer leagueId;
    private Integer userId;
    private Boolean ready;

    public Player() {
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public Integer getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return callsign;
    }

    @Override
    public String callsign() {
        return getCallsign();
    }

    @Override
    public Integer id() {
        return getUserId();
    }

    @Override
    public Boolean ready() { return getReady(); }

    public Boolean getReady() {
        if (ready == null) {
            return false;
        }

        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }
}
