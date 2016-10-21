package de.TimBrian;

import de.TimBrian.enums.Color;

public class Card {
    private int value;
    private Color color;

    public Card(int value, Color color){
        this.value = value;
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }
}

