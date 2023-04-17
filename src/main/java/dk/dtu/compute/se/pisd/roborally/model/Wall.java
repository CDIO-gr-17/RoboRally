package dk.dtu.compute.se.pisd.roborally.model;

public class Wall {

    private Space space;
    private Heading heading;



    public Wall (Heading heading, Space space){
        this.heading = heading;
        this.space = space;
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }



    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }
}
