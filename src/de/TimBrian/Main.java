package de.TimBrian;

public class Main {

    public static void main(String[] args) {
        Table table = new Table();

        for (int i = 0; i < 4; i++) {
            table.addPlayer(new Player(i + 1));
        }

        //Rollenverteilung und Handkarten Verteilung
        table.nextTurn();
        for (Player p : table.players) {
            System.out.println(p.toString());
        }


        //table.nextTurn();
        int test = 0;
    }
}
