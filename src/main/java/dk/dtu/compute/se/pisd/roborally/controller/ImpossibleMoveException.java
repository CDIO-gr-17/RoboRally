package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * This is an Exception thrown when the player tries to move to a space, but is not allowed.
 */
public class ImpossibleMoveException extends Exception{
    public ImpossibleMoveException(@NotNull Player player, @NotNull Space space, @NotNull Heading heading){
        System.out.println("Impossible Move: " + player +", " + space + ", "+  heading);
    }
}