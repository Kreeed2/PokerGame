package de.TimBrian;

import java.util.List;

public class Table {
    List<Player> players;
    Stack cardStack;
    int turnCounter;

    public Table() {
        cardStack = new Stack(true);
        turnCounter = 0;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    //TODO Thread
    public void nextTurn() {

    }
}
