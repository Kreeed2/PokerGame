package de.TimBrian;

/**
 * Created by Brian on 06.11.2016.
 */
public class Pot {
    private int chips = 0;
    private int number;
    private String name;

    public Pot(int number) {
        if(number==0) {
            name = "Main-Pot";
        }
        else {
            name = "Sidepot " + number;
        }
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public void raise(int amount) {
        chips = chips + amount;
    }

    /*public void addChips(Player p, int playerchips){
        this.chips = this.chips + playerchips;
        p.setChips(playerchips);
    }*/

    public String toString() {
        return name + ":\n" + "\tchips: " + chips;
    }
}
