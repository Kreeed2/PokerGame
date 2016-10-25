package de.TimBrian;

import de.TimBrian.enums.Role;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Table {
    List<Player> players = new LinkedList<>();
    Stack cardStack = new Stack(true);
    int turnCounter = 0;

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    //TODO Thread, solve role assignment after all but two players are defeated
    public void nextTurn() {
        if (turnCounter == 0 && players.size() > 1) {
            int dealerPos = new Random(System.currentTimeMillis()).nextInt(players.size());
            //special case initialization
            if (players.size() == 2) {
                players.get(dealerPos).setCurrentRole(Role.DEALERSPECIAL);
                players.get((dealerPos + 1) % players.size()).setCurrentRole(Role.BIG);
            } else {
                players.get(dealerPos).setCurrentRole(Role.DEALER);
                players.get((dealerPos + 1) % players.size()).setCurrentRole(Role.SMALL);
                players.get((dealerPos + 2) % players.size()).setCurrentRole(Role.BIG);
            }
        }
    }
}
