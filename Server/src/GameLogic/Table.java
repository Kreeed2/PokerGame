package GameLogic;


import GameLogic.enums.Role;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Table {
    private final Stack cardStack = new Stack();
    private final List<Player> players = new LinkedList<>();
    private final Stack openCards = new Stack();
    protected int maxBet = 0;
    private int roundCounter = 0;
    private int turnCounter = 0;
    private int dealerPos;

    public void addPlayer(Player p) {
        players.add(p);
    }

    private void removePlayer(Player p) {
        if (p.getCurrentRole() == Role.DEALER)
            dealerPos--;

        players.remove(p);
    }

    private List<Player> decideWinner() {
        List<Player> comparePlayers = players.stream().filter(Player::isInRound).collect(Collectors.toList());
        int pos = 0;
        while (comparePlayers.size() > 1) {
            if (pos == comparePlayers.size() - 1)
                break;
            else {
                switch (comparePlayers.get(pos).getHandValue(openCards.getCards()).compareTo(comparePlayers.get(pos + 1).getHandValue(openCards.getCards()))) {
                    case 1:
                        comparePlayers.remove(pos + 1);
                        break;
                    case -1:
                        comparePlayers.remove(pos);
                        break;
                    case 0:
                        pos++;
                        break;
                }
            }
        }
        return comparePlayers;
    }

    private void distributePot(List<Player> winners) {
        if (winners.size() == 1) {
            //Allin
            if (winners.get(0).getChips() == 0) {
                for (Player p : players) {
                    winners.get(0).addChips(p.subtractPlayerPot(winners.get(0).getPlayerPot()));
                }
                winners.get(0).leaveRound();
                distributePot(decideWinner());
            } else //no Allin
            {
                for (Player p : players) {
                    winners.get(0).addChips(p.subtractPlayerPot(winners.get(0).getPlayerPot()));
                }
            }
        } else {
            if (winners.get(0).getChips() == 0) {
                Player minPlayer = winners.stream().min((p1, p2) -> ((Integer) p1.getChips()).compareTo(p2.getChips())).orElseThrow(RuntimeException::new);

                for (Player p : players) {
                    minPlayer.addChips(p.subtractPlayerPot(winners.get(0).getPlayerPot()) / winners.size());
                }
                minPlayer.leaveRound();
                distributePot(decideWinner());
            } else {
                for (Player w : winners) {
                    for (Player p : players) {
                        w.addChips(p.subtractPlayerPot(winners.get(0).getPlayerPot()) / winners.size());
                    }
                }
            }
        }
    }

    /**
     * main game tick method; calls assignRole and distributeCards
     */
    public void nextTurn() {
        distributeCards();
        if (turnCounter == 0) {
            assignRole();
            preFlop();
        } else if (turnCounter == 3 || countActivePlayers() == 1) {
            distributePot(decideWinner());
            nextRound();
            return;
        } else {
            playerBet();
        }
        turnCounter++;

        System.out.println("-----------------------------------------------------------");
    }

    private void nextRound() {
        openCards.clearCards();
        cardStack.fillStack();

        players.forEach(Player::clearHand);
        players.stream().filter(player -> player.getChips() == 0).forEach(this::removePlayer);

        maxBet = 0;
        turnCounter = 0;
        roundCounter++;
    }

    /**
     * special preflop bet
     */
    private void preFlop() {
        int firstDefault = dealerPos;

        int blind = 100;
        if (players.size() > 2) {
            players.get((dealerPos + 1) % players.size()).placeBet(blind / 2, this);
            players.get((dealerPos + 2) % players.size()).placeBet(blind, this);

            firstDefault = (dealerPos + 3) % players.size();
        } //only 2 players
        else {
            players.get(dealerPos).placeBet(blind / 2, this);
            players.get((dealerPos + 1) % players.size()).placeBet(blind, this);
        }
        for (int j = firstDefault, i = 0; i < players.size(); i++, j = (j + 1) % players.size()) {
            while (players.get(j).isInRound()) {
                if (players.get(j).placeBet(getUserInput(players.get(j)), this))
                    break;
            }
        }
        if (fixBets())
            playerBet();
    }

    private int getUserInput(Player p) {

        System.out.println(openCards.toString() + "------------\n" + p.toString() + "\nBitte gib deinen Bet ein. (" + maxBet + ")");
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        while (true) {
            try {
                return Integer.parseInt(br.readLine());
            } catch (IOException e) {
                System.out.println("Bitte gib eine Zahl ein.");
            }
        }
    }

    private void playerBet() {
        int posSmall = dealerPos;
        if (players.size() > 2)
            posSmall = (dealerPos + 1) % players.size();
        /*
        j = current playing player
        i = sum of the players already played
        countActivePlayers() > 1 = make sure the last player in a roundCounter doesn't fold
         */
        for (int j = posSmall, i = 0; i < players.size() && countActivePlayers() > 1; i++, j = (j + 1) % players.size()) {
            while (players.get(j).isInRound()) {
                if (players.get(j).placeBet(getUserInput(players.get(j)), this))
                    break;
            }
        }
        if (fixBets())
            playerBet();
    }

    private boolean fixBets() {
        for (Player p : players) {
            if (p.isInRound() && (p.getPlayerPot() != maxBet ^ p.getChips() == 0))
                return true;
        }
        return false;
    }

    private int countActivePlayers() {
        int sum = 0;
        for (Player p : players) {
            if (p.isInRound())
                sum++;
        }
        return sum;
    }


    /**
     * used for giving players and table cards according to game situation
     */
    private void distributeCards() {
        switch (turnCounter) {
            case 0:
                //zwei Karten an alle aktiven Spieler
                players.stream().filter(Player::isInRound).forEach(player -> {
                    player.addCardToHand(cardStack.remove());
                    player.addCardToHand(cardStack.remove());
                });
                break;
            case 1:
                //drei offene Karten
                for (int i = 0; i < 3; i++) {
                    openCards.add(cardStack.remove());
                }
                break;
            default:
                openCards.add(cardStack.remove());
                break;
        }
    }

    /**
     * assigns roles to every player
     */
    private void assignRole() {
        if (roundCounter == 0)
            //Zufällige Postion des Dealers in der ersten Runde
            dealerPos = new Random(System.currentTimeMillis()).nextInt(players.size());
        else {
            //Postion um eins nach links in den darauf folgeneden Runden
            dealerPos++;
        }
        //special case initialization
        if (players.size() == 2) {
            players.get(dealerPos).setCurrentRole(Role.DEALERSPECIAL);
            players.get((dealerPos + 1) % players.size()).setCurrentRole(Role.BIG);
        } else {
            players.get(dealerPos).setCurrentRole(Role.DEALER);
            players.get((dealerPos + 1) % players.size()).setCurrentRole(Role.SMALL);
            players.get((dealerPos + 2) % players.size()).setCurrentRole(Role.BIG);
        }

    }
}
