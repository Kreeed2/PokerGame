package de.TimBrian;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Table table = new Table();
        table.cardStack.fillStack();

        for (int i = 0; i < 2; i++) {
            table.addPlayer(new Player(i + 1));
        }

        //Rollenverteilung und Handkarten Verteilung
        table.nextTurn();
        table.nextTurn();
        table.nextTurn();
        table.nextTurn();

        //List<Player> test = table.decideWinner();

        int nmkfl = 0;
    }
}
