package Network;

import GameLogic.Player;
import GameLogic.Table;

import java.util.Observable;
import java.util.Observer;

public class VarObserver implements Observer {
    Table table;

    public VarObserver(Table table) {
        this.table = table;
    }

    @Override
    public void update(Observable o, Object arg) {
        table.broadcastToPlayers("MESSAGE", "Spieler " + arg + " ist dem Spiel beigetreten.");
    }
}
