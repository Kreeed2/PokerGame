package de.TimBrian;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by le24wov on 21.10.16.
 */
public class Stack {
    public List<Card> cards;

    public Stack() {
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
}
