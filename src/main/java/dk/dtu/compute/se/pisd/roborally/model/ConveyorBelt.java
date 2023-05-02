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
 * A conveyorbelt that can be placed on the board and move a player
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class ConveyorBelt extends FieldAction {

    private Heading heading;
    /**
     * <p>Constructor for ConveyorBelt.</p>
     *
     * @param heading a {@link dk.dtu.compute.se.pisd.roborally.model.Heading} object.
     */
    public ConveyorBelt(@NotNull Heading heading){
        this.heading = heading;
    }

    /**
     * <p>Getter for the field <code>heading</code>.</p>
     *
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Heading} object.
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * {@inheritDoc}
     *
     * Moves the player a single space in the heading of the conveyorbelt
     * @author Philip Muff @s224566
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
