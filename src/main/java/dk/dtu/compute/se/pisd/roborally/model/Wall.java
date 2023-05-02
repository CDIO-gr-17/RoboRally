package dk.dtu.compute.se.pisd.roborally.model;

/**
 * <p>Wall class.</p>
 *
 * @author jakob
 * @version $Id: $Id
 */
public class Wall {

    private Space space;
    private Heading heading;



    /**
     * <p>Constructor for Wall.</p>
     *
     * @param heading a {@link dk.dtu.compute.se.pisd.roborally.model.Heading} object.
     * @param space a {@link dk.dtu.compute.se.pisd.roborally.model.Space} object.
     */
    public Wall (Heading heading, Space space){
        this.heading = heading;
        this.space = space;
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
     * <p>Setter for the field <code>heading</code>.</p>
     *
     * @param heading a {@link dk.dtu.compute.se.pisd.roborally.model.Heading} object.
     */
    public void setHeading(Heading heading) {
        this.heading = heading;
    }



    /**
     * <p>Getter for the field <code>space</code>.</p>
     *
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Space} object.
     */
    public Space getSpace() {
        return space;
    }

    /**
     * <p>Setter for the field <code>space</code>.</p>
     *
     * @param space a {@link dk.dtu.compute.se.pisd.roborally.model.Space} object.
     */
    public void setSpace(Space space) {
        this.space = space;
    }
}
