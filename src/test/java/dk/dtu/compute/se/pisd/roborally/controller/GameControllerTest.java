package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
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
            Player player = new Player(board, null, "Player " + i);
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
     *
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
     * @author Jakob Agergaard
     */
    @Test
    void testWalls() {
        Board board = gameController.board;
        Player currentPlayer = board.getCurrentPlayer();
        currentPlayer.setSpace(board.getSpace(0, 0));            //secures player is on correct space

        board.getSpace(0, 0).createWall(Heading.WEST);
        board.getSpace(1, 0).createWall(Heading.WEST);
        board.getSpace(0, 1).createWall(Heading.NORTH);
        board.getSpace(0, 0).createWall(Heading.NORTH);

        for (int i = 0; i < 4; i++) {                       // tries to move player through every wall in every direction
            gameController.moveForward(currentPlayer);
            gameController.fastForward(currentPlayer);
            gameController.turnLeft(currentPlayer);
        }
        Assertions.assertEquals(board.getSpace(0, 0), currentPlayer.getSpace(), "Player should be in the same space");
    }

    /**
     * 3 checkpoints is created with different values and on different spaces.
     * The players location (space) is set to be on the same as the checkpoint, and in the same order.
     * The players token should be collected and added to the players collected tokens.
     * @author Jarl Boyd Roest, s224556@dtu.dk and Mads Fogelberg, s224563@dtu.dk
     */
    @Test
    void testCheckpoints() {
        Board board = gameController.board;
        Player currentPlayer = board.getCurrentPlayer();
        FieldAction checkpoint1 = new Checkpoint(1);
        FieldAction checkpoint2 = new Checkpoint(2);
        FieldAction checkpoint3 = new Checkpoint(3);

        board.getSpace(2, 3).getActions().add(checkpoint1);
        board.getSpace(3, 4).getActions().add(checkpoint2);;
        board.getSpace(4, 3).getActions().add(checkpoint3);;

        gameController.executeEntities();
        Assertions.assertEquals(0, currentPlayer.getPlayerToken());      //test player does not get when not standing on checkpoint
        currentPlayer.setSpace(board.getSpace(2, 3));
        gameController.executeEntities();
        Assertions.assertEquals(1, currentPlayer.getPlayerToken());      //test player gets one token after first checkpoint
        currentPlayer.setSpace(board.getSpace(4, 3));
        gameController.executeEntities();
        Assertions.assertEquals(1, currentPlayer.getPlayerToken());      //test player cant grab a checkpoint of higher order than is meant
        currentPlayer.setSpace(board.getSpace(3, 4));
        gameController.executeEntities();
        Assertions.assertEquals(2, currentPlayer.getPlayerToken());      //normal continuation of incrementel checkpoint grabbing
        currentPlayer.setSpace(board.getSpace(4, 3));
        gameController.executeEntities();
        Assertions.assertEquals(3, currentPlayer.getPlayerToken());
    }


    //   The following tests should be used later for assignment V2


    /**
     * 2 players is created and placed on the board. The old space is checked and if the player has not moved
     * the test will print out that the space (0,0) should be empty. If it is empty, the  player has moved.
     *
     * @author Jarl Boyd Roest, @s224556@dtu.dk and Mads Fogelberg, s224563@dtu.dk
     */
    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() + "!");
    }



    /**
     * A board and a player are created. The method moveForward is used to move the player one space in the direction
     * the player is pointing. The test tests for the direction and the space the player is moving to.
     *
     * @author Jarl Boyd Roest, @s224556@dtu.dk and Mads Fogelberg, s224563@dtu.dk
     */
    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }


    /**
     * Test for Assignment V1 (can be deleted later once V1 was shown to the teacher)
     *
     * @author Jakob Agergaard
     */

    @Test
    void testFastForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.fastForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player is not in the correct space");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player should be heading south");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty");
    }


    /**
     * Test for Assignment V1 (can be deleted later once V1 was shown to the teacher)
     *
     * @author Jakob Agergaard
     */
    @Test
    void turnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnRight(current);

        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player is not in the correct space");
        Assertions.assertEquals(Heading.WEST, current.getHeading(), "Player should be heading West");
        Assertions.assertNotNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should not be empty");
    }


    /**
     * Test for Assignment V1 (can be deleted later once V1 was shown to the teacher)
     *
     * @author Jakob Agergaard
     */
    @Test
    void turnLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        gameController.turnLeft(current);

        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player is not in the correct space");
        Assertions.assertEquals(Heading.EAST, current.getHeading(), "Player should be heading east");
        Assertions.assertNotNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should not be empty");
    }


    /**
     * A player is placed on the same space as a conveyorbelt. The conveyorbelt is executed and the player
     * should be moved one space in the direction the conveyorbelt faces.
     * The player still has the original direction and is not turn the same direction as the conveyorbelt.
     *
     * @author Philip Muff @s224566
     **/

    @Test
    void conveyorbeltMove() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        FieldAction conveyorBelt = new ConveyorBelt(Heading.SOUTH);

        board.getSpace(0, 0).getActions().add(conveyorBelt);
        gameController.executeConveyorbelts();

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should be in space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }


    /**
     * Test of an u-turn. The players direction vil turn 180 degrees
     *
     * @author Mads Fogelberg @s224563
     */
    @Test
    void uTurn() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        gameController.uTurn(current);

        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player is not in the correct space");
        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player should be heading NORTH");
        Assertions.assertNotNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should not be empty");
    }

    /**
     * Test of an backup
     *
     * @author Mads Fogelberg @s224563
     */
    @Test
    void backUp() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.setSpace(board.getSpace(5, 5));
        gameController.backUp(current);

        Assertions.assertEquals(current, board.getSpace(5, 4).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(5, 5).getPlayer(), "Space (0,0) should be empty!");
    }


    /**
     * A Board, a player and a pushpanel are created. The player and the pushpanel are placed on the same space.
     * The test checks for the player's location. The player and the pushpanel starts with a direction south, and the pushpanel pushes
     * the player one space south.
     *
     * @author Jarl Boyd Roest, @s224556@dtu.dk and Mads Fogelberg, s224563@dtu.dk
     */
    @Test
    void pushpanelMove() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        FieldAction pushpanel = new Pushpanel(Heading.SOUTH);

        board.getSpace(0, 0).getActions().add(pushpanel);
        gameController.executePushpanel();

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }


    /**
     * A Board, a player and a gear are created. The player and the gear are placed on the same space.
     * The test checks for the player's direction. The player starts with a direction south, and the gear turns
     * the player once counter-clockwise. The test tests if the player is indeed heading west.
     *
     * @author Jarl Boyd Roest, @s224556@dtu.dk and Mads Fogelberg, s224563@dtu.dk
     */
    @Test
    void gear() {
            Board board = gameController.board;
            Player current = board.getCurrentPlayer();
            FieldAction gear = new Gear();

            board.getSpace(0, 0).getActions().add(gear);;
            gameController.executeGear();

            Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player is not in the correct space");
            Assertions.assertEquals(Heading.WEST,current.getHeading(),"Player should be heading WEST");
            Assertions.assertNotNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should not be empty");
    }
}