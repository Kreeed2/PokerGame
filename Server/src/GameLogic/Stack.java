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

    /***
     * removes the card at index 0 and returns it
     * @return the removed card
     */
    public PokerCard remove() {
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
}
