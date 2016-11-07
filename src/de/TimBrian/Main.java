package de.TimBrian;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Table table = new Table();
        table.cardStack.fillStack();
        int blind = 1000;

        for (int i = 0; i < 4; i++) {
            table.addPlayer(new Player(i + 1));
        }

        table.addPot(new Pot(0));

        for (Player p : table.players) {
            int maxPlayerPot = 0;
            int amount = 1000;
            if (maxPlayerPot > amount && 0 != amount) {
                System.out.println("Minimum chips: " + maxPlayerPot);
            }
            else if (amount == 0) {}//spieler spielt nächste Runde nicht mit
            else {
                maxPlayerPot = amount;
            }
            p.raise(amount);
        }

        for (Pot po : table.pots) {
            System.out.println(po.toString());
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
