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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;

import java.util.Optional;

/**
 * This class contains what should be visible to all the players of the game. The information that they share
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class PlayersView extends TabPane implements ViewObserver {

    private Board board;

    private PlayerView[] playerViews;

    /**
     * <p>Constructor for PlayersView.</p>
     *
     * @param gameController a {@link dk.dtu.compute.se.pisd.roborally.controller.GameController} object.
     */
    public PlayersView(GameController gameController) {
        board = gameController.board;

        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        playerViews = new PlayerView[board.getPlayersNumber()];
        for (int i = 0; i < board.getPlayersNumber();  i++) {
            playerViews[i] = new PlayerView(gameController, board.getPlayer(i));
            this.getTabs().add(playerViews[i]);
        }
        board.attach(this);
        update(board);
    }

    /** {@inheritDoc} */
    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            Player current = board.getCurrentPlayer();
            this.getSelectionModel().select(board.getPlayerNumber(current));
        }
    }

}
