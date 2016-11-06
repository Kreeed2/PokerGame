package de.TimBrian;

import de.TimBrian.enums.Role;
import handChecker.HandChecker;
import handChecker.HandValue;
import handChecker.PokerCard;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Player {
    public Stack hand = new Stack();
    private HandValue hv;
    private int chips = 10000;
    private String name;
    private Role currentRole = Role.DEFAULT;
    private Pot playerpot = new Pot(1);

    public Player(int chips, String name){
        this.chips = chips;
        this.name = name;
    }

    public Player(int number) {
        name = "Felix " + number;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int amount) {
        chips = chips - amount;
        //playerpot.setChips(amount);
    }

    public void raise(int amount) {
        chips = chips - amount;
        playerpot.raise(amount);
    }

    public int getPot() {
        return playerpot.getChips();
    }

    public Role getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(Role r) {
        currentRole = r;
    }

    private List<PokerCard> getCompleteHand(List<PokerCard> openCards) {
        return Stream.concat(hand.getCards().stream(), openCards.stream()).collect(Collectors.toList());
    }

    public HandValue getHandValue(List<PokerCard> openCards) {
        if (hv == null) {
            HandChecker handChecker = new HandChecker();
            hv = handChecker.check(getCompleteHand(openCards));
        }
        return hv;

    }

    public String toString() {
        return name + ":\n" + hand.toString() + "\tchips:\t\t" + chips + "\n\tplayerpot:\t" + playerpot.getChips();
    }
}
