package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Checkpoint extends FieldAction {
    private Space space;
    private int checkPointID;


    /**
     * gives the checkpoint a value 'checkpointID' and creates a token for players to collect
     * @author Jarl Boyd Roest, s224556@dtu.dk og Esben Elnegaard, s224555@dtu.dk
     * @param checkpointID
     */
    public Checkpoint( int checkpointID) {
        this.checkPointID = checkpointID;
    }


    /**
     * Returns a 'checkpoint token' if the player lands on the same space as the checkpoint and if the checkpointID
     * is equal to the players checkpoint tokens +1.
     * @author Jarl Boyd Roest, s224556@dtu.dk og Esben Elnegaard, s224555@dtu.dk
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return true if the player is allowed to collect the checkpoint otherwise the player is landed on a wrong
     * checkpoint and it'll return false
     */
    @Override
    public boolean doAction(GameController gameController, @NotNull Space space) {
       Player player =  space.getPlayer();
        if(player!= null){
            if (player.getPlayerToken() + 1 ==checkPointID)
                player.addToken();
            return true;
        }
        else
        return false;
    }
}
