package de.TimBrian;

import de.TimBrian.enums.Color;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Stack {
    private List<Card> cards = new LinkedList<>();

    /**
     * Creates a new card stack
     *
     * @param full whether or not the card stack should be filled
     */
    public Stack(boolean full) {
        if(full)
            fillStack();
    }

    /**
     * fills the card stack with a full deck and shuffles them
     */
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

    public Card remove(int index) {
        return cards.remove(index);
    }

    public Card get(int index) {
        return cards.get(index);
    }

    public void add(Card c) {
        cards.add(c);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card c : cards) {
            sb.append("\t" + c.toString() + "\n");
        }
        return sb.toString();
    }
}
