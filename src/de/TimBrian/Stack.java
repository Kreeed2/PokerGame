package de.TimBrian;

import handChecker.PokerCard;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Stack {
    //TODO Getter to make cards private
    public List<PokerCard> cards = new LinkedList<>();

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

    public PokerCard remove(int index) {
        return cards.remove(index);
    }

    public PokerCard get(int index) {
        return cards.get(index);
    }

    public void add(PokerCard c) {
        cards.add(c);
    }

    public List<PokerCard> getCards() {
        return cards;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PokerCard c : cards) {
            sb.append("\t" + c.toString() + "\n");
        }
        return sb.toString();
    }
}
