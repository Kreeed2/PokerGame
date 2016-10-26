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

    public String toString() {
        switch (value) {
            case 11:
                return "jack of " + color.toString().toLowerCase();
            case 12:
                return "queen of " + color.toString().toLowerCase();
            case 13:
                return "king of " + color.toString().toLowerCase();
            case 14:
                return "ace of " + color.toString().toLowerCase();
            default:
                return value + " of " + color.toString().toLowerCase();
        }
    }
}

