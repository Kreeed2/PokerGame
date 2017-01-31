package GameLogic;

import handChecker.PokerCard;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Stack implements Serializable {
    private List<PokerCard> cards = new LinkedList<>();

    /***
     * fill the object with Card objects and shuffles the list
     */
    public void fillStack() {
        cards.clear();
        long seed = System.nanoTime();

        for (PokerCard.Color c : PokerCard.Color.values()) {
            for (PokerCard.Value v : PokerCard.Value.values()) {
                cards.add(new Card(v, c));
            }
        }
        Collections.shuffle(cards, new Random(seed));
    }

    public void addStack(Stack stack) {
        stack.cards.forEach(pokerCard -> this.cards.add(pokerCard));
    }

    /***
     * removes the card at index 0 and returns it
     * @return the removed card
     */
    PokerCard remove() {
        return cards.remove(0);
    }

    public void add(PokerCard c) {
        cards.add(c);
    }

    public List<PokerCard> getCards() {
        return cards;
    }

    public void clearCards() {
        cards.clear();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PokerCard c : cards) {
            sb.append("\t").append(c.toString()).append("\n");
        }
        return sb.toString();
    }
}
