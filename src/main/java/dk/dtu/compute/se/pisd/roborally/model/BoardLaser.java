package dk.dtu.compute.se.pisd.roborally.model;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;
public class BoardLaser extends FieldAction{
    private Heading heading;
    public BoardLaser(@NotNull Heading heading){
        this.heading = heading;
    }
    public Heading getHeading() {
        return heading;
    }
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        boolean isLaserDisrupted = false;
        Space nextSpace = space.board.getNeighbour(space,heading);
        while(!isLaserDisrupted) {
            for (Heading wallHeading : nextSpace.getWalls()) {
                if (wallHeading == heading || wallHeading.ordinal() == heading.ordinal() + 2 % 4);
                isLaserDisrupted = true;
            }
            if (nextSpace.getPlayer()!=null) {
                nextSpace.getPlayer().reduceHealth();
                if(nextSpace.getPlayer().getPlayerHealth() == 0){
                    nextSpace.getPlayer().resetTokens();
                }
                isLaserDisrupted = true;
            }
            nextSpace = nextSpace.board.getNeighbour(nextSpace,heading);
        }
        return true;
    }
}