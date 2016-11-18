package de.TimBrian;

class Main {

    public static void main(String[] args) {
        Table table = new Table();
        table.cardStack.fillStack();

        for (int i = 0; i < 4; i++) {
            table.addPlayer(new Player(i));
        }

        while (table.roundCounter < 5) {
            table.nextTurn();
        }
    }
}
