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
package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class handles all the data transmition to and from the database.
 *It implements the methods from the interface IRepository:
 * loadGameFromDB,updateGameInDB,createGameInDB and getGames
 * but it also handles all the prepared statements and private methods
 * which allows the player to create, store, update and load data from the database.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
class Repository implements IRepository {

	private static final String GAME_GAMEID = "gameID";
	private static final String BOARD_NAME = "boardname";

	private static final String GAME_NAME = "gamename";

	private static final String GAME_CURRENTPLAYER = "currentPlayer";

	private static final String GAME_PHASE = "phase";

	private static final String GAME_STEP = "step";

	private static final String PLAYER_PLAYERID = "playerID";

	private static final String PLAYER_NAME = "name";

	private static final String PLAYER_COLOUR = "colour";

	private static final String PLAYER_GAMEID = "gameID";

	private static final String PLAYER_POSITION_X = "positionX";

	private static final String PLAYER_POSITION_Y = "positionY";

	private static final String PLAYER_HEADING = "heading";
	private static final String PLAYER_CHECKPOINT = "checkpoint";
	private static final String PLAYER_HP = "healthpoints";

	private Connector connector;

	Repository(Connector connector){
		this.connector = connector;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Creates a game in the database and stores the values of the games attributes:
	 * Gamename, boardname(type), players, phase and step
	 */
	@Override
	public boolean createGameInDB(Board game) {
		if (game.getGameId() == null) {
			Connection connection = connector.getConnection();
			try {
				connection.setAutoCommit(false);

				PreparedStatement ps = getInsertGameStatementRGK();
				ps.setString(1, game.getGameName()); // instead of name
				ps.setString(2, game.boardName);
				ps.setNull(3, Types.TINYINT); // game.getPlayerNumber(game.getCurrentPlayer())); is inserted after players!
				ps.setInt(4, game.getPhase().ordinal());
				ps.setInt(5, game.getStep());

				int affectedRows = ps.executeUpdate();
				ResultSet generatedKeys = ps.getGeneratedKeys();
				if (affectedRows == 1 && generatedKeys.next()) {
					game.setGameId(generatedKeys.getInt(1));
				}
				generatedKeys.close();

				createPlayersInDB(game);
				createCardFieldsInDB(game);
				createProgrammingFieldsInDB(game);
				ps = getSelectGameStatementU();
				ps.setInt(1, game.getGameId());

				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
					rs.updateRow();
				} else {
					// TODO error handling
				}
				rs.close();

				connection.commit();
				connection.setAutoCommit(true);
				return true;
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
				System.err.println("Some DB error");

				try {
					connection.rollback();
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO error handling
					e1.printStackTrace();
				}
			}
		} else {
			System.err.println("Game cannot be created in DB, since it has a game id already!");
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Updates the games attributes in the databse:
	 * Phase, step, the players cards in hand and their programming fields
	 */
	@Override
	public boolean updateGameInDB(Board game) {
		assert game.getGameId() != null;

		Connection connection = connector.getConnection();
		try {
			connection.setAutoCommit(false);

			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, game.getGameId());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {

				rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
				rs.updateInt(GAME_PHASE, game.getPhase().ordinal());
				rs.updateInt(GAME_STEP, game.getStep());
				rs.updateRow();
			} else {
				// TODO error handling
			}
			rs.close();

			updatePlayersInDB(game);

			updateCardFieldsInDB(game);
			updateProgrammingFieldsFromDB(game);


            connection.commit();
            connection.setAutoCommit(true);
			return true;
		} catch (SQLException e) {
			// TODO error handling
			e.printStackTrace();
			System.err.println("Some DB error");

			try {
				connection.rollback();
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO error handling
				e1.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Loads the games attributes from the database and creates a game with the same:
	 *  phase,step, the players cards in hand and their programming field
	 * @Author Ekkart Kindler
	 * @Author Jakob SA
	 * @Author Esben Elnegaard
	 */
	@Override
	public Board loadGameFromDB(int id) {
		Board game;
		try {
			// TODO here, we could actually use a simpler statement
			//      which is not updatable, but reuse the one from
			//      above for the pupose
			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, id);

			ResultSet rs = ps.executeQuery();
			int playerNo = -1;
			if (rs.next()) {
                /*// TODO the width and height could eventually come from the database
				//int width = AppController.BOARD_WIDTH;
				int width = 8;
				//int height = AppController.BOARD_HEIGHT;
				int height = 8;
				game = new Board(width,height);
				// TODO and we should also store the used game board in the database
				//      for now, we use the default game board*/
				game = LoadBoard.loadBoard(rs.getString(BOARD_NAME));
				if (game == null) {
					return null;
				}
				playerNo = rs.getInt(GAME_CURRENTPLAYER);
				// TODO currently we do not set the games name (needs to be added)
				game.setPhase(Phase.values()[rs.getInt(GAME_PHASE)]);
				game.setStep(rs.getInt(GAME_STEP));

			} else {
				// TODO error handling
				return null;
			}
			rs.close();

			game.setGameId(id);
			loadPlayersFromDB(game);

			if (playerNo >= 0 && playerNo < game.getPlayersNumber()) {
				game.setCurrentPlayer(game.getPlayer(playerNo));
			} else {
				// TODO  error handling
				return null;
			}
			loadCardFieldsFromDB(game);
			loadProgrammingFieldsFromDB(game);

			return game;
		} catch (SQLException e) {
			// TODO error handling
			e.printStackTrace();
			System.err.println("Some DB error");
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public List<GameInDB> getGames() {
		// TODO when there many games in the DB, fetching all available games
		//      from the DB is a bit extreme; eventually there should a
		//      methods that can filter the returned games in order to
		//      reduce the number of the returned games.
		List<GameInDB> result = new ArrayList<>();
		try {
			PreparedStatement ps = getSelectGameIdsStatement();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(GAME_GAMEID);
				String name = rs.getString(GAME_NAME);
				result.add(new GameInDB(id,name));
			}
			rs.close();
		} catch (SQLException e) {
			// TODO proper error handling
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Stores the cards in hand in the database
	 * @param game            The game whoose playerhands we want to save
	 * @throws SQLException
	 * @author Jakob Agergaard
	 */
	private void createCardFieldsInDB(Board game) throws SQLException {
		PreparedStatement ps = getInsertHandStatement();
		for (int i = 0; i < game.getPlayersNumber(); i++) {

			ps.setInt(1,game.getGameId());
			ps.setInt(2,game.getPlayerNumber(game.getPlayer(i)));
			for (int j = 0; j < Player.NO_CARDS; j++) {
				String command = game.getPlayer(i).getCardField(j).getCard().command.name();
				ps.setString(j+3,command);
			}
			int affectedRows = ps.executeUpdate();
			ResultSet generatedKeys = ps.getGeneratedKeys();
		}
		ps.close();
	}

	/**
	 * Updates the hand cards in the database
	 * @param game        the game
	 * @throws SQLException
	 * @author Jakob Agergaard
	 */
	private void updateCardFieldsInDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectHandStatement();
		ps.setInt(1,game.getGameId());

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			for (int i = 0; i < Player.NO_CARDS; i++) {
				CommandCard card = game.getPlayer(playerId).getCardField(i).getCard();
				if (card == null){
					rs.updateString(i+3,null);

				} else {
					String command = card.command.name();
					rs.updateString(i + 3, command);
				}
				rs.updateRow();
			}
		}
		rs.close();
	}

	/**
	 * Loads cards from players hand into game
	 * @param game            The game which you want to assign the cards from the database too
	 * @throws SQLException
	 * @author Jakob Agergaard
	 */
	private void loadCardFieldsFromDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectHandStatement();
		ps.setInt(1,game.getGameId());

		ResultSet rs = ps.executeQuery();

		int i = 0;
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			if (i++ == playerId){
				for (int j = 0; j < Player.NO_CARDS; j++) {
					String commandInDB = rs.getString(j+3);
					if (commandInDB != null) {
						CommandCardField field = game.getPlayer(playerId).getCardField(j);
						CommandCard card;
						card = new CommandCard(Command.valueOf(commandInDB));
						field.setCard(card);
					}
				}
			}
		}
		rs.close();
	}

	/** Takes a prepared statement that inserts the values of the players already programmet cards in the tables of the database.
	 *
	 * @param game
	 * @throws SQLException
	 * @author Esben Elnegaard
	 * @author Jakob Agergaard
	 */
	private void createProgrammingFieldsInDB(Board game) throws SQLException{
		PreparedStatement ps = getInsertProgrammingFieldStatement();
		for (int i = 0; i < game.getPlayersNumber(); i++) {

			ps.setInt(1,game.getGameId());
			ps.setInt(2,game.getPlayerNumber(game.getPlayer(i)));
			for (int j = 0; j < Player.NO_REGISTERS; j++) {
				CommandCard card = game.getPlayer(i).getProgramField(j).getCard();
				if (card == null){
					ps.setString(j+3,null);
				} else {
					String command = card.command.name();
					ps.setString(j+3,command);
				}
			}
			int affectedRows = ps.executeUpdate();
			ResultSet generatedKeys = ps.getGeneratedKeys();
		}
		ps.close();
	}

	/** Takes the result of the prepared statement, which is
	 *  a query that pulls the values of the players stored
	 *  cards in the programming fields. The cards are then given the
	 *  value of the result of the query.
	 *
	 * @param game
	 * @throws SQLException
	 * @author Esben Elnegaard
	 * @author Jakob Agergaard
	 */
	private void loadProgrammingFieldsFromDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectProgrammingFieldStatement();
		ps.setInt(1,game.getGameId());
		ResultSet rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			if (i++ == playerId){
				for (int j = 0; j < Player.NO_REGISTERS; j++) {
					String commandInDB = rs.getString(j+3);
					if (commandInDB != null){
						CommandCardField field = game.getPlayer(playerId).getProgramField(j);
						CommandCard card = new CommandCard(Command.valueOf(commandInDB));
						field.setCard(card);
					}
				}
			}
		}
		rs.close();
	}

	/** Takes a query of the values already stored in the database of
	 * the programming fields
	 * and updates them with the new values.
	 *
	 * @param game
	 * @throws SQLException
	 * @author
	 * @author Jakob Agergaard
	 */
	private void updateProgrammingFieldsFromDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectProgrammingFieldStatement();
		ps.setInt(1,game.getGameId());

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			for (int i = 0; i < Player.NO_REGISTERS; i++) {
				CommandCard card = game.getPlayer(playerId).getProgramField(i).getCard();
				if (card == null){
					rs.updateString(i+3,null);

				} else {
					String command = card.command.name();
					rs.updateString(i + 3, command);
				}
				rs.updateRow();
			}
		}
		rs.close();
	}

	/**
	 * creates the players in the database and sets the values to the starting
	 * value of all the players attributes.
	 * @param game
	 * @throws SQLException
	 */
	private void createPlayersInDB(Board game) throws SQLException {
		// TODO code should be more defensive
		PreparedStatement ps = getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		for (int i = 0; i < game.getPlayersNumber(); i++) {
			Player player = game.getPlayer(i);
			rs.moveToInsertRow();
			rs.updateInt(PLAYER_GAMEID, game.getGameId());
			rs.updateInt(PLAYER_PLAYERID, i);
			rs.updateString(PLAYER_NAME, player.getName());
			rs.updateString(PLAYER_COLOUR, player.getColor());
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
			rs.insertRow();
		}

		rs.close();
	}

	/**
	 * Takes a prepared statement that Querys the database
	 * for at the players attributes and gives them to the
	 * players.
	 * @Author Ekkart Kindler
	 * @param game
	 * @throws SQLException
	 */
	private void loadPlayersFromDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersASCStatement();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			if (i++ == playerId) {
				// TODO this should be more defensive
				String name = rs.getString(PLAYER_NAME);
				String colour = rs.getString(PLAYER_COLOUR);
				Player player = new Player(game, colour ,name);
				game.addPlayer(player);

				int x = rs.getInt(PLAYER_POSITION_X);
				int y = rs.getInt(PLAYER_POSITION_Y);
				player.setSpace(game.getSpace(x,y));
				int heading = rs.getInt(PLAYER_HEADING);
				player.setHeading(Heading.values()[heading]);
				int checkpoint = rs.getInt(PLAYER_CHECKPOINT);
				player.setPlayerToken(checkpoint);
				int healthPoints = rs.getInt(PLAYER_HP);
				player.setPlayerHealth(healthPoints);


				// TODO  should also load players program and hand here
			} else {
				// TODO error handling
				System.err.println("Game in DB does not have a player with id " + i +"!");
			}
		}
		rs.close();
	}

	/**
	 * Takes a prepared statemnt that queryes the database
	 * for all the players attributes and update those
	 * values
	 * @Author Ekkart Kindler
	 * @param game
	 * @throws SQLException
	 */

	private void updatePlayersInDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			// TODO should be more defensive
			Player player = game.getPlayer(playerId);
			// rs.updateString(PLAYER_NAME, player.getName()); // not needed: player's names does not change
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
			rs.updateInt(PLAYER_CHECKPOINT,player.getPlayerToken());
			rs.updateInt(PLAYER_HP,player.getPlayerHealth());
			// TODO error handling
			// TODO take care of case when number of players changes, etc
			rs.updateRow();
		}
		rs.close();

		// TODO error handling/consistency check: check whether all players were updated
	}

	private static final String SQL_INSERT_GAME =
			"INSERT INTO Game(gamename, boardname, currentPlayer, phase, step) VALUES (?, ?, ?, ?, ?)";

	private PreparedStatement insert_game_stmt = null;

	private PreparedStatement getInsertGameStatementRGK() {
		if (insert_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				insert_game_stmt = connection.prepareStatement(
						SQL_INSERT_GAME,
						Statement.RETURN_GENERATED_KEYS);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return insert_game_stmt;
	}

	private static final String SQL_SELECT_GAME =
			"SELECT * FROM Game WHERE gameID = ?";

	private PreparedStatement select_game_stmt = null;

	private PreparedStatement getSelectGameStatementU() {
		if (select_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_game_stmt = connection.prepareStatement(
						SQL_SELECT_GAME,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling

				e.printStackTrace();
			}
		}
		return select_game_stmt;
	}

	private static final String SQL_SELECT_PLAYERS =
			"SELECT * FROM Player WHERE gameID = ?";

	private PreparedStatement select_players_stmt = null;

	private PreparedStatement getSelectPlayersStatementU() {
		if (select_players_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_players_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_players_stmt;
	}


	private static final String SQL_SELECT_PLAYERS_ASC =
			"SELECT * FROM Player WHERE gameID = ? ORDER BY playerID ASC";

	private PreparedStatement select_players_asc_stmt = null;

	private PreparedStatement getSelectPlayersASCStatement() {
		if (select_players_asc_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				// This statement does not need to be updatable
				select_players_asc_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS_ASC);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_players_asc_stmt;
	}

	private static final String SQL_SELECT_GAMES =
			"SELECT gameID, gamename FROM Game";

	private PreparedStatement select_games_stmt = null;

	private PreparedStatement getSelectGameIdsStatement() {
		if (select_games_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_games_stmt = connection.prepareStatement(
						SQL_SELECT_GAMES);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_games_stmt;
	}

	/**
	 * SQL insert query for inserting a hand of cards in the database
	 * @author Jakob Agergaard
	 *
	 */
	private static final String SQL_INSERT_HAND =
			"INSERT INTO HAND(gameID, playerID, CARD1, CARD2, CARD3, CARD4, CARD5, CARD6, CARD7, CARD8) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private PreparedStatement insert_hand_stmt = null;

	private PreparedStatement getInsertHandStatement(){
		if (insert_hand_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				insert_hand_stmt = connection.prepareStatement(
						SQL_INSERT_HAND,
						Statement.RETURN_GENERATED_KEYS);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return insert_hand_stmt;
	}

	/**
	 * SQL select query for the hand table
	 * @author Jakob Agergaard
	 */
	private static final String SQL_SELECT_HAND =
			"SELECT * FROM HAND WHERE gameID = ?";
	private PreparedStatement select_hand_stmt = null;

	private PreparedStatement getSelectHandStatement() {
		if(select_hand_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_hand_stmt = connection.prepareStatement(
						SQL_SELECT_HAND,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_hand_stmt;
	}

	private static final String SQL_INSERT_PROGRAMMINGFIELD =
			"INSERT INTO PROGRAMMINGFIELD(gameID, playerID, CARD1, CARD2, CARD3, CARD4, CARD5) VALUES (?, ?, ?, ?, ?, ?,?)";
	private PreparedStatement insert_programmingfield_stmt = null;

	private PreparedStatement getInsertProgrammingFieldStatement(){
		if (insert_programmingfield_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				insert_programmingfield_stmt = connection.prepareStatement(
						SQL_INSERT_PROGRAMMINGFIELD,
						Statement.RETURN_GENERATED_KEYS);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return insert_programmingfield_stmt;
	}

	private static final String SQL_SELECT_PROGRAMMINGFIELD =
			"SELECT * FROM PROGRAMMINGFIELD WHERE gameID = ?";
	private PreparedStatement select_programmingfield_stmt = null;

	private PreparedStatement getSelectProgrammingFieldStatement() {
		if(select_programmingfield_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_programmingfield_stmt = connection.prepareStatement(SQL_SELECT_PROGRAMMINGFIELD ,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_programmingfield_stmt;
	}








}
