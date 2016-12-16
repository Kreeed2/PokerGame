package GameLogic;

import GameLogic.enums.Role;
import Network.Handler;
import handChecker.HandChecker;
import handChecker.HandValue;
import handChecker.PokerCard;

import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Player extends Observable {
    private final Stack hand = new Stack();
    Logger log = Logger.getGlobal();
    Handler handler;
    private Role currentRole = Role.DEFAULT;
    private HandValue hv;
    private boolean inRound = true;
    private int playerPot = 0;

    private String name = "ERROR";
    private int chips = 10000;

    public String getName() {
        return name;
    }

    public Player(Socket socket) {
        handler = new Handler(socket, this);
        handler.start();
        handler.sendData("NAMEADD", null);
    }

    //TEST
    @Override
    protected void finalize() throws Throwable {
        log.info("Spieler " + name + " wird entfernt");
        handler.sendData("REMOVE", null);
        handler.close();
        super.finalize();
    }

    public void setName(String name) {
        this.name = name;
        setChanged();
        notifyObservers(name);
    }

    public int getChips() {
        return chips;
    }

    public void addChips(int amount) {
        chips += amount;
    }

    public int getPlayerPot() {
        return playerPot;
    }

    public int subtractPlayerPot(int amount) {
        if (playerPot >= amount) {
            playerPot -= amount;
            return amount;
        } else {
            int out = playerPot;
            playerPot = 0;
            return out;
        }
    }

    public void clearHand() {
        hand.clearCards();
    }

    public void addCardToHand(PokerCard card) {
        hand.add(card);
        handler.sendData("CARDADD", card);
    }

    /**
     * function evaluates given amount
     * @param amount amount the player wants to increment playerPot
     * @param table current Table player is playing on
     * @return whether or not amount was correct
     */
    public boolean placeBet(int amount, Table table) {
        //fold
        if (amount < 0) {
            inRound = false;
            return true;
        }
        //enough chips and raise/call/check
        if (amount <= chips) {
            if (table.maxBet <= amount + playerPot) {
                playerPot += amount;
                chips -= amount;
                table.maxBet = playerPot;
            } //Allin
            else if (chips == amount) {
                playerPot += amount;
                chips -= amount;
            } else
                return false;
            return true;
        } else
            return false;
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

    public boolean isInRound() {
        return inRound;
    }

    public void leaveRound() {
        inRound = false;
    }

}