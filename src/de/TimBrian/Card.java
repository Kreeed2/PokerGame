package de.TimBrian;

import handChecker.PokerCard;

class Card implements PokerCard {
    private final Value value;
    private final Color color;

    public Card(Value value, Color color){
        this.value = value;
        this.color = color;
    }

    public Value getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }

    public String toString() {
        return value.toString().toLowerCase() + " of " + color.toString().toLowerCase();
    }
}

