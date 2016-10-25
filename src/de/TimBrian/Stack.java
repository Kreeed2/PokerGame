package de.TimBrian;

import de.TimBrian.enums.Color;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Stack {
    private List<Card> cards = new LinkedList<>();

    //full boolean um leere Hände zu ermöglichen
    public Stack(boolean full) {
        if(full)
            fillStack();
    }

    private void fillStack() {
        cards = new LinkedList<>();
        long seed = System.nanoTime();

        for (Color c : Color.values()) {
            for (int j = 2; j < 15; j++) {
                cards.add(new Card(j,c));
            }
        }
        Collections.shuffle(cards, new Random(seed));
    }

    public Card removeCard(int index) {
        return cards.remove(index);
    }

    public void addCard(Card c) {
        cards.add(c);
    }
}
