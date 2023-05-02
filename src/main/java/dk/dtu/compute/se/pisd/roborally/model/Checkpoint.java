package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Checkpoint class.</p>
 *
 * @author jakob
 * @version $Id: $Id
 */
public class Checkpoint extends FieldAction {
    private Space space;
    private int checkPointID;

    /**
     * gives the checkpoint a value 'checkpointID' and creates a token for players to collect
     *
     * @author Jarl Boyd Roest, s224556@dtu.dk og Esben Elnegaard, s224555@dtu.dk
     * @param checkPointID a int.
     */
    public Checkpoint( int checkPointID) {
        this.checkPointID = checkPointID;
    }
    /**
     * <p>getCheckpointID.</p>
     *
     * @return a int.
     */
    public int getCheckpointID(){return checkPointID;}


    /**
     * {@inheritDoc}
     *
     * Returns a 'checkpoint token' if the player lands on the same space as the checkpoint and if the checkpointID
     * is equal to the players checkpoint tokens +1.
     * @author Jarl Boyd Roest, s224556@dtu.dk og Esben Elnegaard, s224555@dtu.dk
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
