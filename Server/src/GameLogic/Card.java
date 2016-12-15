package GameLogic;

import handChecker.PokerCard;

import java.io.Serializable;

class Card implements PokerCard, Serializable {
    private final Value value;
    private final Color color;

    public Card(Value value, Color color) {
        this.value = value;
        this.color = color;
    }

    public Value getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }
}

