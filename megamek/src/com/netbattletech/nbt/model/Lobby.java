package com.netbattletech.nbt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.netbattletech.nbt.ILobby;
import com.netbattletech.nbt.IPlayer;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Lobby implements ILobby {
    private Integer id;
    private Integer leagueId;
    private Player owner;
    private List<Player> players = new ArrayList<>();
    private Integer upperLimit;
    private Integer lowerLimit = 0;

    @JsonIgnore
    Listener listener;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
        onLobbyChanged();
    }

    public Integer getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
        onLobbyChanged();
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.removeIf(p -> (p.getCallsign().equals(player.getCallsign())));
    }

    void clearPlayers() {
        players.clear();
    }

    protected void onLobbyChanged() {
        if (listener != null) {
            listener.onLobbyUpdated(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        Lobby other = (Lobby)o;
        return getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String toString() {
        Player owner = getOwner();
        if (owner == null) {
            return "(unknown)";
        }

        return owner.getCallsign();
    }

    @Override
    public IPlayer owner() {
        return getOwner();
    }

    @Override
    public int playerCount() {
        return getPlayers().size();
    }

    @Override
    public IPlayer player(int index) {
        if (index < 0 || index > getPlayers().size() - 1) {
            return null;
        }

        return getPlayers().get(index);
    }

    @Override
    public int lowerLimit() {
        return getLowerLimit();
    }

    @Override
    public int upperLimit() {
        return getUpperLimit();
    }

    @Override
    public void addPlayer(IPlayer player) {
        addPlayer((Player)player);
    }

    @Override
    public void removePlayer(IPlayer player) {
        removePlayer((Player)player);
    }

    @Override
    public void update(ILobby other) {
        Lobby _lobby = (Lobby)other;
        this.setId(_lobby.getId());
        this.setLeagueId(_lobby.getLeagueId());
        this.setLowerLimit(_lobby.getLowerLimit());
        this.setUpperLimit(_lobby.getUpperLimit());
        this.setOwner(_lobby.getOwner());
        this.clearPlayers();
        for (Player player : _lobby.getPlayers()) {
            this.addPlayer(player);
        }
    }

    public interface Listener {
        void onLobbyUpdated(ILobby lobby);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
