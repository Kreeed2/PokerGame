package de.TimBrian;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Table table = new Table();
        table.cardStack.fillStack();

        for (int i = 0; i < 4; i++) {
            table.addPlayer(new Player(i));
        }

        //Rollenverteilung und Handkarten Verteilung
        table.nextTurn();
        table.nextTurn();
        table.nextTurn();
        table.nextTurn();

        int nmkfl = 0;
    }
}
