package GameLogic;

import GUI.Texture;
import handChecker.PokerCard;

import java.io.Serializable;

class Card implements PokerCard, Serializable {
    private final Value value;
    private final Color color;
    private final Texture texture;

    public Card(Value value, Color color) {
        this.value = value;
        this.color = color;

        texture = new Texture(value.name().toUpperCase() + "_" + color.name().toUpperCase() + ".png");
    }

    public Texture getTexture() {
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

