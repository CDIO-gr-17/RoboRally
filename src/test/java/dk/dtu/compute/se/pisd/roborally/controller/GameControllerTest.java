package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;

    }

    /**
     * Test for Assignment V1 (can be deleted later once V1 was shown to the teacher)
     * @author Jakob Agergaard
     */
    @Test
    void testV1() {
        Board board = gameController.board;

        Player player = board.getCurrentPlayer();
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player, board.getSpace(0, 4).getPlayer(), "Player " + player.getName() + " should beSpace (0,4)!");
    }

    /**
     *@author Jakob Agergaard
     */
    @Test
    void testWalls(){
        Board board = gameController.board;
        Player currentPlayer = board.getCurrentPlayer();
        currentPlayer.setSpace(board.getSpace(0,0));            //secures player is on correct space

        board.getSpace(0,0).createWall(Heading.WEST);
        board.getSpace(1,0).createWall(Heading.WEST);
        board.getSpace(0,1).createWall(Heading.NORTH);
        board.getSpace(0,0).createWall(Heading.NORTH);

        for (int i = 0; i < 4; i++) {                       // tries to move player through every wall in every direction
            gameController.moveForward(currentPlayer);
            gameController.fastForward(currentPlayer);
            gameController.turnLeft(currentPlayer);
        }
        Assertions.assertEquals(board.getSpace(0,0),currentPlayer.getSpace(),"Player should be in the same space");
    }

    /**
     * @author Jakob Agergaard
     */
    @Test
    void testCheckpoints () {
        Board board = gameController.board;
        Player currentPlayer = board.getCurrentPlayer();


        board.getSpace(2,3).createCheckpoint(1);
        board.getSpace(3,4).createCheckpoint(2);
        board.getSpace(4,3).createCheckpoint(3);

        gameController.executeEntities();
        Assertions.assertEquals(0,currentPlayer.getPlayerToken());      //test player does not get when not standing on checkpoint
        currentPlayer.setSpace(board.getSpace(2,3));
        gameController.executeEntities();
        Assertions.assertEquals(1,currentPlayer.getPlayerToken());      //test player gets one token after first checkpoint
        currentPlayer.setSpace(board.getSpace(4,3));
        gameController.executeEntities();
        Assertions.assertEquals(1,currentPlayer.getPlayerToken());      //test player cant grab a checkpoint of higher order than is meant
        currentPlayer.setSpace(board.getSpace(3,4));
        gameController.executeEntities();
        Assertions.assertEquals(2,currentPlayer.getPlayerToken());      //normal continuation of incrementel checkpoint grabbing
        currentPlayer.setSpace(board.getSpace(4,3));
        gameController.executeEntities();
        Assertions.assertEquals(3,currentPlayer.getPlayerToken());
    }


     //   The following tests should be used later for assignment V2

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }
    @Test
    void testFastForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.fastForward(current);

        Assertions.assertEquals(current,board.getSpace(0,2).getPlayer(),"Player is not in the correct space");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(),"Player should be heading south");
        Assertions.assertNull(board.getSpace(0,0).getPlayer(),"Space (0,0) should be empty");
    }

    @Test
    void turnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnRight(current);

        Assertions.assertEquals(current,board.getSpace(0,0).getPlayer(),"Player is not in the correct space");
        Assertions.assertEquals(Heading.WEST, current.getHeading(),"Player should be heading West");
        Assertions.assertNotNull(board.getSpace(0,0).getPlayer(),"Space (0,0) should not be empty");
    }

    @Test
    void turnLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        gameController.turnLeft(current);

        Assertions.assertEquals(current,board.getSpace(0,0).getPlayer(),"Player is not in the correct space");
        Assertions.assertEquals(Heading.EAST, current.getHeading(),"Player should be heading east");
        Assertions.assertNotNull(board.getSpace(0,0).getPlayer(),"Space (0,0) should not be empty");
    }
    @Test
    void conveyorbeltMove() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        board.getSpace(0,0).createConveyorbelt(Heading.SOUTH);
        gameController.executeConveyorbelts();

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }
    @Test
    void uTurn() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        gameController.uTurn(current);

        Assertions.assertEquals(current,board.getSpace(0,0).getPlayer(),"Player is not in the correct space");
        Assertions.assertEquals(Heading.NORTH, current.getHeading(),"Player should be heading NORTH");
        Assertions.assertNotNull(board.getSpace(0,0).getPlayer(),"Space (0,0) should not be empty");
    }
    @Test
    void backUp() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.setSpace(board.getSpace(5,5));
        gameController.backUp(current);

        Assertions.assertEquals(current, board.getSpace(5, 4).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(5, 5).getPlayer(), "Space (0,0) should be empty!");
    }


}