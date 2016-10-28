package de.TimBrian;

import handChecker.PokerCard;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Stack {
    //TODO Getter to make cards private
    public List<Card> cards = new LinkedList<>();

    /**
     * fills the card stack with a full deck and shuffles them
     */
    public void fillStack() {
        cards = new LinkedList<>();
        long seed = System.nanoTime();

        for (PokerCard.Color c : PokerCard.Color.values()) {
            for (PokerCard.Value v : PokerCard.Value.values()) {
                cards.add(new Card(v,c));
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
