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

import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This class is the bridge between logic and view as per the MVC design pattern
 * All logic should therefore have a connection to this
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class GameController {

    final public Board board;

    /**
     * <p>Constructor for GameController.</p>
     *
     * @param board a {@link dk.dtu.compute.se.pisd.roborally.model.Board} object.
     */
    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        // TODO Assignment V1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free()
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if and when the player is moved (the counter and the status line
        //     message needs to be implemented at another place)

        Player currentPlayer = board.getCurrentPlayer();
        if (currentPlayer!=null){
            int nextPlayerNumber = (board.getPlayerNumber(currentPlayer) + 1) % board.getPlayersNumber();
            Player nextPlayer = board.getPlayer(nextPlayerNumber);
            if (space.getPlayer() ==null) {
            currentPlayer.setSpace(space);
            board.setCurrentPlayer(nextPlayer);
            board.setCounter(board.getCounter() + 1);
        }
        }
    }


    // XXX: V2
    /**
     * Method sets current player to player one for this player to start programming
     * It also removes the previous programmed cards and makes the fields visible
     * Makes the player "draw" new programming cards and makes them visible
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }
    /**
     * Pulls a random command from an array of all possible commands and returns it as a "commandcard"
     * to be used in the programming phase and later executed
     *
     * @return  a commandcard to be used in programming phase
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }
    /**
     * Makes all programming field (and therefore cards) invisible and thereafter makes only one visible
     * Changes to activation phase and sets currentplayer to player one for this player to start executing
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    /**
     * Announces the winner of the game and start the Final phase.
     * This freezes the game but allows the users to view the state is was in when a winner was found
     *
     * @author Jakob Agergaard s224570
     */
    public void startFinalisationPhase(){
        makeProgramFieldsInvisible();
        board.setPhase(Phase.FINALISATION);
        if (board.getPhase()== Phase.FINALISATION){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Winner found!");
            alert.setHeaderText("The winner is " + board.getWinner());
            Optional<ButtonType> result = alert.showAndWait();

            System.out.println("der er fundet en vinder");
        }
    }

    /**
     * Makes one programming cardfield visible for all players
     *
     * @param register  which cardfield should be made visible
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /**
     * makes all programming cardfields invisible
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }
    /**
     * Sends all planned programming cards to execution
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }
    /**
     * sends the next planned programming card for the current player to execution
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }
    /**
     * Continues execution of cards as long as activation phase is active and stepmode is off
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }
    /**
     * Executes the programmed commandcard for all players step by step
     * @author Ekki
     * @author Jakob Agergaard s224570
     */
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    executeEntities();
                    for (int i = 0; i < board.getPlayersNumber(); i++) {
                        if(board.getPlayer(i).getPlayerToken()==board.getMaxCheckpoints()){
                            board.setWinner(board.getPlayer(i).getName());
                            startFinalisationPhase();
                            break;
                        }
                    }
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * Executes the chosen command of an interactive command-card
     *
     * @param option the command which should be executed
     * @author Jakob Agergaard
     */
    public void executeCommandOptionAndContinue(@NotNull Command option) {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.PLAYER_INTERACTION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                board.setPhase(Phase.ACTIVATION);
                executeCommand(currentPlayer, option);

                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }


    /**
     * Reads the command and executes the appropriate method
     * @param player    The player on which the command should be executed for or on
     * @param command   The command which needs to be executed
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case BACK_UP:
                    this.backUp(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**
     * Executes all entities on the board. This method decides the order of the execution og the entities.
     * This means a lot to the playthrough experience
     *
     * @author Jakob Agergaard
     */
    public void executeEntities(){
        executeConveyorbelts();
        executePushpanel();
        executeGear();
        executeBoardLasers();
        executeCheckpoints();
    }

    /**
     * executes the doAction() method for all conveyorbelts on the board
     *
     * @author Philip Muff
     * @author Jakob Agergaard
     */
    public void executeConveyorbelts() {
        List<Player> moved = new ArrayList<>();
        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space currentSpace = board.getSpace(i,j);
                Player player = currentSpace.getPlayer();
                if (moved.contains(player)){
                    return;
                }
                if (player!=null) {
                    moved.add(player);
                }
                for (FieldAction action : currentSpace.getActions()) {
                    if (action.getClass()==ConveyorBelt.class) {
                        action.doAction(this, currentSpace);
                    }
                }
            }
        }
    }

    /**
     * executes the doAction() method for all gears on the board
     */
    public void executeGear() {
        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space currentSpace = board.getSpace(i,j);
                for (FieldAction action : currentSpace.getActions()) {
                    if (action.getClass()==Gear.class) {
                        action.doAction(this, currentSpace);
                    }
                }

            }
        }
    }

    /**
     * executes the doAction() method for all pushPanels on the board
     */
    public void executePushpanel() {
        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space currentSpace = board.getSpace(i,j);
                for (FieldAction action : currentSpace.getActions()) {
                    if (action.getClass()== Pushpanel.class) {
                        action.doAction(this, currentSpace);
                    }
                }
            }
        }
    }

    /**
     * executes the doAction() method for all checkpoints on the board
     *
     * @author Jakob Agergaard
     */
    public void executeCheckpoints() {
        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space currentSpace = board.getSpace(i,j);
                for (FieldAction action: currentSpace.getActions()) {
                    if (action.getClass()==Checkpoint.class){
                        action.doAction(this,currentSpace);
                    }
                }
            }
        }
    }

    /**
     * executes the doAction() method for all lasers on the board
     */
    public void executeBoardLasers(){

        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space currentSpace = board.getSpace(i,j);
                for (FieldAction action: currentSpace.getActions()) {
                    if (action.getClass()==BoardLaser.class){
                        action.doAction(this,currentSpace);
                    }
                }
            }
        }
    }

    /**
     * Moves a player to a specific space in a direction. If another player is on the space a player is trying to move onto,
     * the second player is pushed in the heading of the first player
     *
     * @param player the player to move
     * @param space the space to move the player to
     * @param heading the heading to move the player in
     * @throws dk.dtu.compute.se.pisd.roborally.controller.ImpossibleMoveException thrown if something is obstructing the player to move
     * @author Jakob Agergaard
     */
    public void moveToSpace(
            @NotNull Player player,
            @NotNull Space space,
            @NotNull Heading heading) throws ImpossibleMoveException {
        Player targetPlayer = space.getPlayer();
        Space prevSpace = board.getNeighbour(space,heading.next().next());
        Space nextSpace = board.getNeighbour(space, heading);
        if (prevSpace.isWallObstructing(heading)) {     //checks for walls on current space in direction and target space in reverse direction
            throw new ImpossibleMoveException(player,space,heading);
        }
        if (space.isWallObstructing(heading.next().next())){
            throw new ImpossibleMoveException(player,space,heading);
        }
        for (FieldAction action : space.getActions()) {
            if (action.getClass().getSimpleName().equals("BoardLaser")) {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        if (targetPlayer != null) {
            if (nextSpace != null) {
                    // XXX Note that there might be additional problems
                    // with infinite recursion here!
                    moveToSpace(targetPlayer, nextSpace, heading);
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        player.setSpace(space);
    }

    /**
     * Moves the given player a space forward in the heading of the player
     *
     * @param player the player to move
     */
    public void moveForward(@NotNull Player player) {
        Space space = player.getSpace();
        if (space != null) {
            Heading heading = player.getHeading();
            Space target = board.getNeighbour(space,heading);
            if (target != null){
                try {
                    moveToSpace(player,target,heading);
                }catch(ImpossibleMoveException e){

                }

            }
        }

    }

    /**
     * Moves the given player two spaces forward in the heading of the player
     *
     * @param player the player to move
     */
    public void fastForward(@NotNull Player player) {
        for (int i = 0; i < 2;i++) {
            moveForward(player);
        }
    }

    /**
     * Moves the given player a space backward without changing the heading
     *
     * @param player a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     * @author Philip Muff
     */
    public void backUp(@NotNull Player player){
        Space currentSpace = player.getSpace();
        if (currentSpace != null) {
            Heading headingBack = player.getHeading().next().next();
            Space nextSpace = board.getNeighbour(currentSpace, headingBack);
            if (nextSpace != null) {
                try {
                    moveToSpace(player, nextSpace, headingBack);
                }
                catch(ImpossibleMoveException e){
                }
            }
        }
    }

    /**
     * Turns the player to the right
     *
     * @param player the player to move
     */
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());

    }

    /**
     * Turns the player to the left
     *
     * @param player the player to move
     */
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
    }

    /**
     * Turns the player 180degrees around itself(u-turn)
     *
     * @param player the player to turn
     * @author Philip Muff
     */
    public void uTurn(@NotNull Player player){
        player.setHeading(player.getHeading().next().next());
    }

    /**
     * Moves programming card from one field to another if this is allowed. (the field is empty)
     *
     * @param source    the field containing the command the player wishes to move
     * @param target    the firld to which the command should be moved
     * @return          True if the card is allowed to be moved
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
