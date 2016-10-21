package de.TimBrian;

import de.TimBrian.enums.Role;

public class Player {
    private int chips;
    private String name;
    private Stack hand;
    private Role currentRole;

    public Player(int chips, String name){
        this.chips = chips;
        this.name = name;
        hand = new Stack(false);
    }

    public void setCurrentRole(Role r) {
        currentRole = r;
    }

    public Role getCurrentRole() {
        return currentRole;
    }
}
