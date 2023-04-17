package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;

public class BoardLaser extends FieldAction{
    private Heading heading;
    public Boolean isLaserDisrupted;
    public BoardLaser(@NotNull Heading heading){
        this.heading = heading;
    }

    public Heading getHeading() {
        return heading;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        isLaserDisrupted = false;
        Space nextSpace = space.board.getNeighbour(space,heading);
        while(!isLaserDisrupted) {
            for (Heading wallHeading : nextSpace.getWalls()) {
                if (wallHeading == heading || wallHeading.ordinal() == heading.ordinal() + 2 % 4) ;
                isLaserDisrupted = true;
            }
            if (nextSpace.getPlayer()!=null) {
                nextSpace.getPlayer().reduceHealth();
                System.out.println(nextSpace.getPlayer().getName() + " has taken damage. Health is now: " + nextSpace.getPlayer().getPlayerHealth());
                if(nextSpace.getPlayer().getPlayerHealth() == 0){
                    nextSpace.getPlayer().resetTokens();
                    System.out.println(nextSpace.getPlayer().getName() + " has reached 0 health. Tokens has been reset to " + nextSpace.getPlayer().getPlayerToken() + " , and health is now " + nextSpace.getPlayer().getPlayerHealth());
                }
                isLaserDisrupted = true;
            }
            nextSpace = nextSpace.board.getNeighbour(nextSpace,heading);
        }
        return true;
    }
}
