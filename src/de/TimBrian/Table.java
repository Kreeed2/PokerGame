package de.TimBrian;

import de.TimBrian.enums.Role;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Table {
    List<Player> players = new LinkedList<>();
    Stack cardStack = new Stack();
    Stack openCards = new Stack();
    int turnCounter = 0;
    int round = 0;
    int dealerPos;
    int maxBet = 0;
    int blind = 100;

    public void addPlayer(Player p) {
        players.add(p);
    }

    //TODO solve role assignment after all but two players are defeated
    public void removePlayer(Player p) {
        if (p.getCurrentRole() == Role.DEALER)
            dealerPos--;

        players.remove(p);
    }

    public List<Player> decideWinner() {
        List<Player> comparePlayers = new LinkedList<>(players);
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

    /**
     * main game tick method; calls assignRole and distributeCards
     */
    public void nextTurn() {
        //maxBet = 0;
        distributeCards();
        if (turnCounter == 0) {
            assignRole();
            preFlop();
        } else
            playerBet();
        turnCounter++;
    }

    /**
     * special preflop bet
     */
    private void preFlop() {
        players.get((dealerPos + 1) % players.size()).placeBet(blind / 2, this);
        players.get((dealerPos + 2) % players.size()).placeBet(blind, this);

        int firstDefault = (dealerPos + 3) % players.size();
        for (int j = firstDefault, i = 0; i < players.size(); i++, j = (j + 1) % players.size()) {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            try {
                while (players.get(j).isInRound()) {
                    if (players.get(j).placeBet(Integer.parseInt(br.readLine()), this))
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fixBets())
            playerBet();
    }

    private void playerBet() {
        int posSmall = (dealerPos + 1) % players.size();
        for(int j = posSmall, i = 0; i < players.size(); i++, j = (j+1)%players.size()) {
                InputStreamReader isr = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader(isr);
            try {
                while (players.get(j).isInRound()) {
                    if (players.get(j).placeBet(Integer.parseInt(br.readLine()), this))
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fixBets())
            playerBet();
    }

    private boolean fixBets() {
        for (Player p : players) {
            if (p.isInRound() && p.getPlayerPot() != maxBet)
                return true;
        }
        return false;
    }


    /**
     * used for giving players and table cards according to game situation
     */
    private void distributeCards() {
        switch (turnCounter) {
            case 0:
                //zwei Karten an alle Spieler
                for (Player p : players) {
                    p.hand.add(cardStack.remove(0));
                    p.hand.add(cardStack.remove(1));
                }
                break;
            case 1:
                //drei offene Karten
                for (int i=0;i<3;i++)
                {
                    openCards.add(cardStack.remove(0));
                }
                break;
            default:
                openCards.add(cardStack.remove(0));
                break;
        }
    }

    /**
     * assigns roles to every player
     */
    private void assignRole() {
        if (round == 0)
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
