package GameLogic;


import GameLogic.enums.Role;
import Network.Handler;
import Network.Message;
import Network.VarObserver;
import com.sun.javafx.collections.MappingChange;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Table {
    private final Stack cardStack = new Stack();
    private final List<Player> players = new LinkedList<>();
    private final Stack openCards = new Stack();

    HashSet<VarObserver> observers = new HashSet<>();

    protected int maxBet = 0;
    private int blind = 100;
    private int roundCounter = 0;
    private int turnCounter = 0;
    private int dealerPos;

    public void broadcastToPlayers(String message) {
       for (Player out : players) {
           out.handler.sendData("MESSAGE", message);
       }
    }

    public int playerAmount() {
        return players.size();
    }

    public Stack getCardStack() {
        return cardStack;
    }

    public int getRoundCounter() {

        return roundCounter;
    }

    public void addPlayer(Player p) {
        players.add(p);

        VarObserver obs = new VarObserver(this);
        p.addObserver(obs);
        observers.add(obs);
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

        //SERVER
        turnMessage();
        //

        distributeCards();

        //SERVER
        handCardMessage();
        openCardMessage();
        //
        if (turnCounter == 0) {
            assignRole();
            preFlop();
            //SERVER
            roleMessage();
            //
        } else if (turnCounter == 3 || countActivePlayers() == 1) {
            //SERVER
            openHandCardsMessage();
            //
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

        //int blind = 100;
        if (players.size() > 2) {
            players.get((dealerPos + 1) % players.size()).placeBet(blind / 2, this);
            players.get((dealerPos + 2) % players.size()).placeBet(blind, this);

            firstDefault = (dealerPos + 3) % players.size();

            //SERVER
            blindMessage();
            //
        } //only 2 players
        else {
            players.get(dealerPos).placeBet(blind / 2, this);
            players.get((dealerPos + 1) % players.size()).placeBet(blind, this);
        }
        for (int j = firstDefault, i = 0; i < players.size(); i++, j = (j + 1) % players.size()) {
            while (players.get(j).isInRound()) {
                int bet = getUserInput(players.get(j));
                if (players.get(j).placeBet(bet/*getUserInput(players.get(j))*/, this)) {

                    //SERVER
                    betMessage(players.get(j).getName(), bet);
                    //
                    break;
                }
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

        //SERVER
        aktivePlayerMessage();
        //

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
                int bet = getUserInput(players.get(j));
                if (players.get(j).placeBet(bet/*getUserInput(players.get(j))*/, this)) {

                    //SERVER
                    betMessage(players.get(j).getName(), bet);
                    //
                    break;
                }
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

    //SERVER

    public void sendToHandler(Player p, String header, Object payload){
        p.handler.sendData(header, payload);
    }

    public void turnMessage() {
        Map<String, Integer> playerChips = new HashMap<>();
        for(Player p: players){
            playerChips.put(p.getName(), p.getChips());
        }
        for (Player p : players) {
            sendToHandler(p, "PLAYERCHIPS", playerChips);
        }

        int pot = 0;
        for (Player p: players) {
            pot += p.getPlayerPot();
        }
        for (Player p : players) {
            sendToHandler(p, "POT", pot);
        }
    }

    public void roleMessage() {
        Map<String, Integer> roles = new HashMap<>();
        for(Player p: players){
            roles.put(p.getName(), p.getCurrentRole().ordinal());
        }

        for (Player p : players) {
            sendToHandler(p, "ROLE", roles);
        }
    }

    public void handCardMessage(){
        if (turnCounter == 0) {
            for (Player p : players) {
                sendToHandler(p, "HANDCARDS", p.getHand());
            }
        }
    }

    public void openCardMessage() {
        for (Player p : players) {
            sendToHandler(p, "OPENCARDS", openCards);
        }
    }

    public void betMessage(String name, int bet) {
        Map<String, Integer> playerBet = new HashMap<>();
        playerBet.put(name, bet);
        for (Player p: players) {
            sendToHandler(p, "BET", playerBet);
        }
    }

    public void blindMessage() {
        for (Player p: players) {
            sendToHandler(p, "BLINDS", blind);
        }
    }

    public void aktivePlayerMessage() {
        List<Player> aktivePlayer = new LinkedList<>();
        for (Player p: players) {
            if (p.isInRound())
                aktivePlayer.add(p);
        }
        for (Player p: players) {
            sendToHandler(p, "AKTIVEPLAYER", aktivePlayer);
        }
    }

    public void openHandCardsMessage() {
        if (countActivePlayers() > 2) {
            Map<String, Stack> handCards = new HashMap<>();
            for (Player p: players) {
                if (p.isInRound())
                    handCards.put(p.getName(), p.getHand());
            }
            for (Player p: players) {
                sendToHandler(p, "RIVERHAND", handCards);
            }
        }
    }
}
