package dk.dtu.compute.se.pisd.roborally.model;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;
/**
 * <p>BoardLaser class.</p>
 *
 * @author jakob
 * @version $Id: $Id
 */
public class BoardLaser extends FieldAction{
    private Heading heading;
    /**
     * <p>Constructor for BoardLaser.</p>
     *
     * @param heading a {@link dk.dtu.compute.se.pisd.roborally.model.Heading} object.
     */
    public BoardLaser(@NotNull Heading heading){
        this.heading = heading;
    }
    /**
     * <p>Getter for the field <code>heading</code>.</p>
     *
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Heading} object.
     */
    public Heading getHeading() {
        return heading;
    }
    /**
     * {@inheritDoc}
     *
     * Damages the players in the line of sight of the BoardLaser.
     * If the player reached 0 health, the players tokens(amount of checkpoints reached) will be reset.
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
