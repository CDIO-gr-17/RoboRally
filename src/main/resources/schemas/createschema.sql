/* Need to switch off FK check for MySQL since there are crosswise FK references */
SET FOREIGN_KEY_CHECKS = 0;;



CREATE TABLE IF NOT EXISTS Game (
  gameID int NOT NULL UNIQUE AUTO_INCREMENT,

  gamename varchar(255),
  boardname varchar(20),
  phase tinyint,
  step tinyint,
  currentPlayer tinyint NULL,

  PRIMARY KEY (gameID),
  FOREIGN KEY (gameID, currentPlayer) REFERENCES Player(gameID, playerID)
);;


CREATE TABLE IF NOT EXISTS Player (
  gameID int NOT NULL,
  playerID tinyint NOT NULL,

  name varchar(255),
  colour varchar(31),
  
  positionX int,
  positionY int,
  checkpoint int,
  heading tinyint,
  
  PRIMARY KEY (gameID, playerID),
  FOREIGN KEY (gameID) REFERENCES Game(gameID)
);;



CREATE TABLE IF NOT EXISTS Hand (
    gameID int NOT NULL,
    playerID tinyint NOT NULL,

    CARD1 varchar(20),
    CARD2 varchar(20),
    CARD3 varchar(20),
    CARD4 varchar(20),
    CARD5 varchar(20),
    CARD6 varchar(20),
    CARD7 varchar(20),
    Card8 varchar(20),

    PRIMARY KEY (gameID, playerID),
    FOREIGN KEY (gameID, playerID) REFERENCES Player(gameID, playerID)
);;


SET FOREIGN_KEY_CHECKS = 1;;

    /*
// TODO still some stuff missing here
     */