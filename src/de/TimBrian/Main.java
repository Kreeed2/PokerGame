package de.TimBrian;

import handChecker.HandChecker;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Table table = new Table();
        table.cardStack.fillStack();
        HandChecker hc = new HandChecker();

        for (int i = 0; i < 4; i++) {
            table.addPlayer(new Player(i + 1));
        }

        //Rollenverteilung und Handkarten Verteilung
        table.nextTurn();
        for (Player p : table.players) {
            System.out.println(p.toString());
        }
        table.nextTurn();
        table.nextTurn();
        table.nextTurn();

        List<Player> test = table.decideWinner();

        //System.out.print(table.cardStack.toString());


        //table.nextTurn();
        int nmkfl = 0;
    }
}
