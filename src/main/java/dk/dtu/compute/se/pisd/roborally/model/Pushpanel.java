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

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.ImpossibleMoveException;
import org.jetbrains.annotations.NotNull;

/**
 * This class is where the pushpanel is created, that is an extention of the field action class.
 * The pushpanel is a function to the game, that is placed on a space, and activates if a player lands on it.
 * @author Jarl Boyd Roest, s224556@dtu.dk and Mads Fogelberg s224563@dtu.dk
 */
public class  Pushpanel extends FieldAction {

    private Heading heading;
    public Pushpanel(@NotNull Heading heading){
        this.heading = heading;
    }

    public Heading getHeading() {
        return heading;
    }

    /**
     * This boolean identifies the player's location, and validates if the location is in fact shared with
     * push panel's space. If this happen to be, the loop will return true, and the function of the push panel will
     * activate, and it will push the player one space in the push panel's direction.
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return true if an action is performed
     * @author Mads Fogelberg s224563@dtu.dk, Jarl Boyd Roest s224556@dtu.dk
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        try {
            if(space.getPlayer()!=null) {
                Player player = space.getPlayer();
                Space nextSpace = space.board.getNeighbour(space,heading);
                gameController.moveToSpace(player, nextSpace, heading);
            }
        } catch (ImpossibleMoveException e) {

        }
        return true;
    }
}