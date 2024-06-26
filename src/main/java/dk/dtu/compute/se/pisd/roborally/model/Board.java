/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * <p>Board class.</p>
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;
    private String gameName;
    private int maxCheckpoints;
    private String winner;

    /**
     * Creates a board with a name, width and height and creates spaces for each coordinate on the board
     *
     * @param width     the width of the wanted board
     * @param height    the height of the wanted board
     * @param boardName The name of the wanted board
     */
    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }

        this.stepMode = false;
    }

    /**
     * <p>Constructor for Board.</p>
     *
     * @param width a int.
     * @param height a int.
     */
    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    /**
     * <p>Getter for the field <code>gameId</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getGameId() {
        return gameId;
    }

    /**
     * Assigns an integer as a unique Identifier for the current game.
     *
     * @param gameId    The integer whished as Identifier
     */
    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    /**
     * Method returns the space on the board that belongs to and is placed on x and y
     *
     * @param x Integer that represents the horizontal axis
     * @param y Integer that represents the vertical axis
     * @return the space that belongs to the coordinates x and y
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    /**
     * Returns the amount of current players
     *
     * @return  int representing amount of players
     */
    public int getPlayersNumber() {
        return players.size();
    }

    /**
     * Adds a player to the game as long as the player belongs in this game and has not already been added
     *
     * @param player    The player who should be added to the game
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    /**
     * <p>getPlayer.</p>
     *
     * @param i a int.
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * <p>getCurrentPlayer.</p>
     *
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * <p>setCurrentPlayer.</p>
     *
     * @param player a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    /**
     * <p>Getter for the field <code>phase</code>.</p>
     *
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Phase} object.
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * <p>Setter for the field <code>phase</code>.</p>
     *
     * @param phase a {@link dk.dtu.compute.se.pisd.roborally.model.Phase} object.
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    /**
     * <p>Getter for the field <code>step</code>.</p>
     *
     * @return a int.
     */
    public int getStep() {
        return step;
    }

    /**
     * <p>Setter for the field <code>step</code>.</p>
     *
     * @param step a int.
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    /**
     * <p>isStepMode.</p>
     *
     * @return  whether the player wants the whole planned program executed or one command at a time
     */
    public boolean isStepMode() {
        return stepMode;
    }

    /**
     * Set whether the player wants to run the program continuosly og in steps for each command
     *
     * @param stepMode  True for stepmode, false for not
     */
    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * <p>getPlayerNumber.</p>
     *
     * @param player a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     * @return a int.
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    /**
     * <p>getNextCheckpointnr.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getNextCheckpointnr(){
        return "Next checkpoint: " + (getCurrentPlayer().getPlayerToken()+1);
    }
    /**
     * <p>getHealth.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHealth(){
        return "Health: " + getCurrentPlayer().getPlayerHealth();
    }
    /**
     * <p>getStatusMessage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // XXX: V1 add the move count to the status message
        // XXX: V2 changed the status so that it shows the phase, the current player and the number of steps
        return "Phase: " + getPhase().name() +
                ", Current player: " + getCurrentPlayer().getName()+
                ", Total moves: " + getCounter();

    }
    private int counter;

    /**
     * method for counting number of moves that has been made in a game
     *
     * @param number    the number of moves this counter should be set to
     */
    public void setCounter(int number){
        if (number!=counter){
            counter = number;
            notifyChange();
        }
    }
    /**
     * <p>Getter for the field <code>counter</code>.</p>
     *
     * @return a int.
     */
    public int getCounter(){
        return counter;
    }


    /**
     * <p>Getter for the field <code>gameName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * <p>Setter for the field <code>gameName</code>.</p>
     *
     * @param gameName a {@link java.lang.String} object.
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * <p>Getter for the field <code>maxCheckpoints</code>.</p>
     *
     * @return a int.
     */
    public int getMaxCheckpoints() {
        return maxCheckpoints;
    }
    /**
     * <p>addMaxCheckpoints.</p>
     */
    public void addMaxCheckpoints() {
        this.maxCheckpoints++;
    }
    /**
     * <p>Getter for the field <code>winner</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getWinner() {
        return winner;
    }

    /**
     * <p>Setter for the field <code>winner</code>.</p>
     *
     * @param winner a {@link java.lang.String} object.
     */
    public void setWinner(String winner) {
        this.winner = winner;
    }
}

