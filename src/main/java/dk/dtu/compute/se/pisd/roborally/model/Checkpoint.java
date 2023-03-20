package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Checkpoint extends FieldAction {
    private Space space;
    private final int token;
    private int checkPointID;



    public Checkpoint( int checkpointID) {
        this.token = 1;
        this.checkPointID = checkpointID;
    }



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
