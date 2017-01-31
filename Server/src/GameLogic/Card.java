package GameLogic;

import handChecker.PokerCard;

import java.io.Serializable;

import GUI.Texture;

public class Card implements PokerCard, Serializable {
    private Value value;
    private Color color;
    private Texture texture;

    public Card(Value value, Color color) {
        this.value = value;
        this.color = color;
    }

    public Texture getTexture(int width, int height) {
        if (texture == null)
            texture = new Texture(value.name().toUpperCase() + "_" + color.name().toUpperCase() + ".png", width, height);
        return texture;
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