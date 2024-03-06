package edu.uob;

import java.util.ArrayList;

public class OXOModel {

    private final ArrayList<ArrayList<OXOPlayer>> cells;
    private final ArrayList<OXOPlayer> players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        cells = new ArrayList<>();
        players = new ArrayList<>();
        initial(numberOfRows, numberOfColumns);
    }


    public int getNumberOfPlayers() {
        return players.size();
    }

    public void addPlayer(OXOPlayer player) {
        players.add(player);
    }

    public OXOPlayer getPlayerByNumber(int number) {

        return players.get(number);
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.size();
    }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber, player);
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn() {
        gameDrawn = true;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

    public void initial(int rowNumber, int colNumber) {
        for (int i = 0; i < rowNumber; i++) {
            ArrayList<OXOPlayer> list = new ArrayList<>();
            for (int j = 0; j < colNumber; j++) {
                list.add(null);
            }
            cells.add(list);
        }
    }

    public void addRow() {
        ArrayList<OXOPlayer> list = new ArrayList<>();
        int rows = getNumberOfRows();
        if (rows < 9) {
            int cols = getNumberOfColumns();
            for (int i = 0; i < cols; i++) {
                list.add(null);
            }
            cells.add(list);
        }
    }

    public void removeRow() {
        int rows = getNumberOfRows();
        if (rows > 1) {
            cells.remove(rows - 1);
        }
    }

    public void addColumn() {
        int cols = getNumberOfColumns();
        if (cols < 9) {
            int rows = getNumberOfRows();
            for (int i = 0; i < rows; i++) {
                this.cells.get(i).add(null);
            }
        }
    }

    public void removeColumn() {
        int cols = getNumberOfColumns();
        int rows = getNumberOfRows();
        if (cols > 1) {
            for (int i = 0; i < rows; i++) {
                cells.get(i).remove(cols - 1);
            }
        }
    }
}

