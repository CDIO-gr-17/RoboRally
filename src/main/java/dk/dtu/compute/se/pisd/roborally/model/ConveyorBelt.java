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
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class ConveyorBelt extends FieldAction {

    private Heading heading;
    public ConveyorBelt(@NotNull Heading heading){
        this.heading = heading;
    }

    public Heading getHeading() {
        return heading;
    }

    /**
     * Moves the player a single space in the heading of the conveyorbelt
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return true if an action is performed
     * @author Philip Muff
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