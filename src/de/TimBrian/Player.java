package de.TimBrian;

import de.TimBrian.enums.Role;

public class Player {
    public Stack hand = new Stack(false);
    private int chips = 10000;
    private String name;
    private Role currentRole = Role.DEFAULT;

    public Player(int chips, String name){
        this.chips = chips;
        this.name = name;
    }

    public Player(int number) {
        name = "Dummy " + number;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int amount) {
        chips = amount;
    }

    public Role getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(Role r) {
        currentRole = r;
    }
}
