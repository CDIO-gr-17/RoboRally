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

/**
 * Card that holds a command
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class CommandCard extends Subject {

    final public Command command;

    /**
     * Assigns a command to this commandcard
     *
     * @param command   The command that is to be assigned a card
     */
    public CommandCard(@NotNull Command command) {
        this.command = command;
    }

    /**
     * The name of the command assigned to the card
     *
     * @return  A String of the name of the command on the card
     */
    public String getName() {
        return command.displayName;
    }


}
