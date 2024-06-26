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

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a space. The board is made up of spaces.
 * This space contains all entities belonging to it and
 * can have many walls, one player, one checkpoint, one conveyorbelt and so on.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class Space extends Subject {


    private List<Heading> walls = new ArrayList<>();

    private List<FieldAction> actions = new ArrayList<>();

    public final Board board;
    public final int x;
    public final int y;
    private Player player;

    /**
     * <p>Constructor for Space.</p>
     *
     * @param board a {@link dk.dtu.compute.se.pisd.roborally.model.Board} object.
     * @param x a int.
     * @param y a int.
     */
    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
    }

    /**
     * <p>Getter for the field <code>player</code>.</p>
     *
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player belonging to this space
     *
     * @param player    The player which belongs to this space
     */
    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer && (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }
    //Tager en heading som parameter og laver en væg i en given heading til et space og adder det til listen

    /**
     * "creates" a wall by adding the walls heading to the list of walls on this space
     *
     * @param heading       The heading which the wall should be facing
     * @author Jakob Agergaard
     */
    public void createWall(Heading heading){
        walls.add(heading);
    }

    /**
     * Checks whether a wall i present on this space in the given heading
     *
     * @param heading   which direction you want to check
     * @return          True if there is a wall
     * @author Jakob Agergaard
     */
    public boolean isWallObstructing(Heading heading){
        for (Heading wall : walls) {
            if (heading == wall) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of all walls on this space
     *
     * @return      A list of walls on this space
     * @author Jakob Agergaard
     */
    public List<Heading> getWalls(){
        return walls;
    }
    /**
     * <p>Getter for the field <code>actions</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List <FieldAction> getActions() {
        return actions;
    }


    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

}
