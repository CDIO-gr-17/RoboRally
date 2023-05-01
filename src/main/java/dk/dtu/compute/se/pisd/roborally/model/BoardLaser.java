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
    /**
     * Damages the players in the line of sight of the BoardLaser.
     * If the player reached 0 health, the players tokens(amount of checkpoints reached) will be reset.
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return true if the action has been performed
     * @author Philip Muff @s224566
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        boolean isLaserDisrupted = false;
        Space nextSpace = space.board.getNeighbour(space,heading);
        while(!isLaserDisrupted) {
            for (Heading wallHeading : nextSpace.getWalls()) {
                if (wallHeading == heading || wallHeading.ordinal() == heading.ordinal() + 2 % 4) {
                    isLaserDisrupted = true;
                }
            }
            if (nextSpace.getPlayer()!=null) {
                nextSpace.getPlayer().reduceHealth();
                if(nextSpace.getPlayer().getPlayerHealth() == 0){
                    nextSpace.getPlayer().resetTokens();
                }
                isLaserDisrupted = true;
            }
            for (FieldAction action: nextSpace.getActions()) {
                if (action.getClass().getSimpleName().equals("BoardLaser")) {
                    isLaserDisrupted = true;
                }
            }
            nextSpace = nextSpace.board.getNeighbour(nextSpace,heading);
        }
        return true;
    }
}