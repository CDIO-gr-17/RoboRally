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

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * Class representing a player. Containing every info relevant to each player
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class Player extends Subject {

    /** Constant <code>NO_REGISTERS=5</code> */
    final public static int NO_REGISTERS = 5;
    /** Constant <code>NO_CARDS=8</code> */
    final public static int NO_CARDS = 8;

    final public Board board;

    private String name;
    private String color;

    final private int startHealth = 5;

    final private int startTokens = 0;
    private int playerToken = startTokens;
    private int playerHealth = startHealth;
    private Space space;

    private Heading heading = SOUTH;
    private CommandCardField[] program;

    private CommandCardField[] cards;
    /**
     * Creates a player and assigns a board, name and color.
     * Assigns new cardfields to a programming-cards and a hand-cards field-array.
     *
     * @param board the board to which the player should adhere
     * @param color the desired color of the player
     * @param name  the desired name of the player
     */
    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;

        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    /**
     * <p>Getter for the field <code>startHealth</code>.</p>
     *
     * @return a int.
     */
    public int getStartHealth() {
        return startHealth;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the name of the player if it is not the same as previous
     * Updates the view and space of
     *
     * @param name the desired name, should not be null
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * <p>addToken.</p>
     *
     * @author Jarl Boyd Roest, s224556@dtu.dk og Esben Elnegaard, s224555@dtu.dk
     * the counter for a player to be abel to collect checkpoints
     */
    public void addToken() {
        playerToken++;
    }

    /**
     * <p>Getter for the field <code>color</code>.</p>
     *
     * @return  The color of the player as a string
     */
    public String getColor() {
        return color;
    }

    /**
     * Change the color of a player
     *
     * @param color The desired color of the player as a string
     */
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    /**
     * <p>Getter for the field <code>space</code>.</p>
     *
     * @return  The space which the player is positioned on
     */
    public Space getSpace() {
        return space;
    }

    /**
     * <p>Setter for the field <code>space</code>.</p>
     *
     * @param space a {@link dk.dtu.compute.se.pisd.roborally.model.Space} object.
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    /**
     * <p>Getter for the field <code>heading</code>.</p>
     *
     * @return  The current heading of the player
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * Change the heading of the player
     *
     * @param heading   The whished heading og the player
     */
    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * <p>getProgramField.</p>
     *
     * @param i a int.
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.CommandCardField} object.
     */
    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    /**
     * <p>getCardField.</p>
     *
     * @param i a int.
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.CommandCardField} object.
     */
    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    /**
     * <p>Getter for the field <code>playerToken</code>.</p>
     *
     * @author Jarl Boyd Roest, s224556@dtu.dk og Esben Elnegaard, s224555@dtu.dk
     * @return the amount of tokens a player has
     */
    public int getPlayerToken() {
        return playerToken;
    }
    /**
     * <p>reduceHealth.</p>
     */
    public void reduceHealth(){
        playerHealth--;
    }
    /**
     * <p>Getter for the field <code>playerHealth</code>.</p>
     *
     * @return a int.
     */
    public int getPlayerHealth(){
        return playerHealth;
    }
    /**
     * <p>Setter for the field <code>playerHealth</code>.</p>
     *
     * @param playerHealth a int.
     */
    public void setPlayerHealth(int playerHealth){this.playerHealth = playerHealth;}

    /**
     * <p>Setter for the field <code>playerToken</code>.</p>
     *
     * @param playerToken a int.
     */
    public void setPlayerToken(int playerToken) {
        this.playerToken = playerToken;
    }
    /**
     * <p>resetTokens.</p>
     */
    public void resetTokens(){
        playerToken = startTokens;
        playerHealth = startHealth;
    }
}
