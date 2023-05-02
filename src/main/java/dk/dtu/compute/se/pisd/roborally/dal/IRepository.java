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

import dk.dtu.compute.se.pisd.roborally.model.Board;

import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public interface IRepository {
	
 	/**
 	 * <p>createGameInDB.</p>
 	 *
 	 * @param game a {@link dk.dtu.compute.se.pisd.roborally.model.Board} object.
 	 * @return a boolean.
 	 */
 	boolean createGameInDB(Board game);
	
	/**
	 * <p>updateGameInDB.</p>
	 *
	 * @param game a {@link dk.dtu.compute.se.pisd.roborally.model.Board} object.
	 * @return a boolean.
	 */
	boolean updateGameInDB(Board game);
	
	/**
	 * <p>loadGameFromDB.</p>
	 *
	 * @param id a int.
	 * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Board} object.
	 */
	Board loadGameFromDB(int id);
	
	/**
	 * <p>getGames.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	List<GameInDB> getGames();

}
