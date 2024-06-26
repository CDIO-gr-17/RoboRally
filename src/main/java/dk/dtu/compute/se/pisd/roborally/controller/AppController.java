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
package dk.dtu.compute.se.pisd.roborally.controller;

import com.sun.jdi.connect.Connector;
import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.dal.GameInDB;
import dk.dtu.compute.se.pisd.roborally.dal.IRepository;
import dk.dtu.compute.se.pisd.roborally.dal.RepositoryAccess;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.Label;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class controls the outer app the program i running in.
 * This means running the menubar and controlling user access to files EI. database later on
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    final private List<String> BOARDS = Arrays.asList("default", "test");
    private List<GameInDB> games;

    final private RoboRally roboRally;

    private GameController gameController;
    private IRepository repoAcces = RepositoryAccess.getRepository();
    /**
     * <p>Constructor for AppController.</p>
     *
     * @param roboRally a {@link dk.dtu.compute.se.pisd.roborally.RoboRally} object.
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * Creates board and players according to dialouge windows.
     * The player first chooses a number of players, then a board type,
     *  which is given set of premade boards
     *  that gets pulled from a JSON file.
     * The players then chooses a name for the game and names for the different players.
     * At last the state of the game is initiatet and given a new instance of a gamecontroller
     *
     * @Author Ekkart Kindler
     * @Author Jakob-SA
     * @Author Esben Elnegaard
     */
    public void newGame() {
        ChoiceDialog<Integer> playerDialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        playerDialog.setTitle("Player number");
        playerDialog.setHeaderText("Select number of players");
        Optional<Integer> numberResult = playerDialog.showAndWait();

        //asks player which map/board is wanted
        if(!numberResult.isPresent()) {
            return;
        }
        ChoiceDialog<String> boardDialog = new ChoiceDialog<>(BOARDS.get(0), BOARDS);
        boardDialog.setTitle("Board");
        boardDialog.setHeaderText("Select the board you want to play");
        Optional<String> boardResult = boardDialog.showAndWait();
        if(!boardResult.isPresent()) {
            return;
        }
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Name");
        nameDialog.setHeaderText("Give your game a name");
        Optional<String> nameResult = nameDialog.showAndWait();
        if(!nameResult.isPresent()) {
            return;
        }
        List<String> playerNameResult = new ArrayList<>();

        for (int i = 0; i < numberResult.get(); i++) {
            TextInputDialog playerNameDialog = new TextInputDialog();
            playerNameDialog.setTitle("Player name");
            playerNameDialog.setHeaderText("Write the name of player "+ (i+1));
            Optional<String> temp = playerNameDialog.showAndWait();
            if(!temp.isPresent()) {
                return;
            }
            if(temp.get().equals("")) {
                playerNameResult.add("Player " + (i+1));
            } else {
                playerNameResult.add(temp.get());
            }

        }

        if (numberResult.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }
            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.
            Board board = LoadBoard.loadBoard(boardResult.get()+"board");
            board.setGameName(nameResult.get());

            gameController = new GameController(board);
            int no = numberResult.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
                player.setName(playerNameResult.get(i));
            }

            // XXX: V2
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();
            roboRally.createBoardView(gameController);
            repoAcces.createGameInDB(gameController.board);

        }
    }

    /**
     *  Saves the game by updating the current game's info in the database
     *
     * @author Jakob Agergaard
     */
    public void saveGame() {
        // XXX needs to be implemented eventually

        repoAcces.updateGameInDB(gameController.board);

    }

    /**
     * Loads a game from the database based on the choosen gameID and gameName
     *
     * @author Jakob Agergaard
     */
    public void loadGame() {
        games = repoAcces.getGames();

        //Lets player choose which game should be loaded
        ChoiceDialog<GameInDB> boardDialog = new ChoiceDialog<>(games.get(0), games);
        boardDialog.setTitle("Game");
        boardDialog.setHeaderText("Select which saved game you want to continue playing");
        Optional<GameInDB> boardResult = boardDialog.showAndWait();
        if(!boardResult.isPresent()) {
            return;
        }

        // XXX needs to be implememted eventually
        // for now, we just create a new game
        if (gameController == null) {
            Board board = repoAcces.loadGameFromDB(boardResult.get().id);
            gameController = new GameController(board);
            roboRally.createBoardView(gameController);


        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * Closes the game after confirmation pop-up
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    /**
     * Checks if gamecontroller is present
     *
     * @return Boolean that is true when the game is running or the gamecontroller has not been set null
     */
    public boolean isGameRunning() {
        return gameController != null;
    }


    /** {@inheritDoc} */
    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
