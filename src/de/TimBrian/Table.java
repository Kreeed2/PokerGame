package de.TimBrian;

import de.TimBrian.enums.Role;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    //TODO lvl of winners implement
    public List<Player> decideWinner(int lvl) {
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

    /*
    public List<Player> decideWinner() {
        //return players.stream().collect(Collectors.maxBy((p1, p2) -> p1.getHandValue(openCards.getCards()).compareTo(p2.getHandValue(openCards.getCards()))));
        return players.stream().sorted((p1, p2) -> p1.getHandValue(openCards.getCards()).compareTo(p2.getHandValue(openCards.getCards()))).collect(Collectors.toList());
    }
    */

    public void distributePot(List<Player> winners) {
        if (winners.size() == 1) {
            //Allin
            if (winners.get(0).getChips() == 0){
                for (Player p : players) {
                    winners.get(0).addChips(p.subtractPlayerPot(winners.get(0).getPlayerPot()));
                }
                distributePot(decideWinner(2));
            }
            else //no Allin
            {
                for (Player p : players) {
                    winners.get(0).addChips(p.subtractPlayerPot(winners.get(0).getPlayerPot()));
                }
            }
        } else {
            if (winners.get(0).getChips() == 0) {
                Player minPlayer = null;
                int posPot = 100000;
                for (Player p : winners) {
                    if (p.getPlayerPot() < posPot)
                        minPlayer = p;
                }

                for (Player p : players) {
                    minPlayer.addChips(p.subtractPlayerPot(winners.get(0).getPlayerPot()) / winners.size());
                }
                minPlayer.leaveRound();
                distributePot(decideWinner(1));
            }
            else {
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
        //maxBet = 0;
        distributeCards();
        if (turnCounter == 0) {
            assignRole();
            preFlop();
        } else if (turnCounter == 3 || countActivePlayers() == 1) {
            distributePot(decideWinner(1));
            //newRound function (all pplPot reset, etc)
        } else {
            playerBet();
        }
        turnCounter++;

        System.out.println("-----------------------------------------------------------");
    }

    /**
     * special preflop bet
     */
    private void preFlop() {
        int firstDefault = dealerPos;

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
        countActivePlayers() > 1 = make sure the last player in a round doesn't fold
         */
        for(int j = posSmall, i = 0; i < players.size() && countActivePlayers() > 1; i++, j = (j+1)%players.size()) {
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
    public void distributeCards() {
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
