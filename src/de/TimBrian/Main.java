package de.TimBrian;

public class Main {

    public static void main(String[] args) {
        Table table = new Table();
        for (int i = 0; i < 4; i++) {
            table.addPlayer(new Player(i + 1));
        }
        table.nextTurn();
        table.nextTurn();
        int test = 0;
    }
}
