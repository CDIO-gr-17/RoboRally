/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.view;

import com.sun.javafx.iio.common.ImageLoaderImpl;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * This class draws the space and all the entities on it. Mostly using pictures
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class SpaceView extends StackPane implements ViewObserver {

    /** Constant <code>SPACE_HEIGHT=75</code> */
    final public static int SPACE_HEIGHT = 75; // 60; // 75;
    /** Constant <code>SPACE_WIDTH=75</code> */
    final public static int SPACE_WIDTH = 75;  // 60; // 75;

    public final Space space;
    private ClassLoader classLoader = SpaceView.class.getClassLoader();

    /**
     * Creates a visual space in the app and sets its height and width
     * Sets its color to either black or white
     *
     * @param space  the space which should be visualized
     */
    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);


        /*
        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }
        */
        // updatePlayer();
        // This space view should listen to changes of the space


        /*
        InputStream input = classLoader.getResourceAsStream("pictures/blank.PNG");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(SPACE_HEIGHT);
        imageView.setFitWidth(SPACE_WIDTH);
        */
        InputStream input = classLoader.getResourceAsStream("pictures/blank.PNG");
        Image image = new Image(input);
        BackgroundSize backgroundSize = new BackgroundSize(SPACE_HEIGHT,SPACE_WIDTH,true,true,true,false);
        BackgroundImage backgroundImage = new BackgroundImage(image,BackgroundRepeat.REPEAT ,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,backgroundSize);
        Background background = new Background(backgroundImage);
        this.setBackground(background);








        space.attach(this);
        update(space);
        //this.getChildren().add(imageView);
    }


    private void updatePlayer() {
        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }
    }

    /**
     * Draw the conveyorbelt on this space
     * @param conveyorBelt the conveyorbelt to be drawn (gets the heading of conveyorbelt)
     * @author Philip Muff
     * @author Jakob Agergaard
     */

    private void drawConveyorbelt(ConveyorBelt conveyorBelt){
        if(conveyorBelt!=null) {
            InputStream input = classLoader.getResourceAsStream("pictures/conveyor.JPG");
            Image image = new Image(input);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(SPACE_HEIGHT);
            imageView.setFitWidth(SPACE_WIDTH);
            imageView.setRotate((90 * conveyorBelt.getHeading().ordinal()+180) % 360);
            this.getChildren().add(imageView);



/*
            Polygon arrow = new Polygon(0.0, 0.0,
                    5.0, 10.0,
                    10.0, 0.0);
            arrow.setFill(Color.GREY);
            arrow.setRotate((90 * conveyorBelt.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);

 */
        }
    }

    /**
     *  Draws a gear on the space if one should be drawn
     * @param gear
     * @author Jakob Agergaard
     */
    private void drawGear(Gear gear) {
        if (gear != null) {
            InputStream input;
            if (gear.isClockwise()){
                input = classLoader.getResourceAsStream("pictures/clockwise.jpg");
            } else {
                input = classLoader.getResourceAsStream("pictures/counterclockwise.jpg");
            }
            Image image = new Image(input);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(SPACE_WIDTH);
            imageView.setFitHeight(SPACE_HEIGHT);
            this.getChildren().add(imageView);

/*
             Arc arc = new Arc();
            arc.setCenterX(50.0f);
            arc.setCenterY(10.0f);
            arc.setRadiusX(15.0f);
            arc.setRadiusY(15.0f);
            arc.setStartAngle(45.0f);
            arc.setLength(270.0f);
            arc.setType(ArcType.OPEN);
            arc.setFill(Color.DARKBLUE);
            this.getChildren().add(arc);

 */
        }
    }


    /**
     * Draws all walls belonging to this space
     *
     * @param walls     A list of walls that is placed on the space being created
     * @author Jakob Agergaard
     */
    private void drawWalls(List<Heading> walls){
        for(Heading wall:walls){
            InputStream input;
            switch(wall){
                case SOUTH: input = classLoader.getResourceAsStream("pictures/wallsouth.png");
                    break;
                case WEST: input = classLoader.getResourceAsStream("pictures/wallwest.png");
                    break;
                case NORTH: input = classLoader.getResourceAsStream("pictures/wallnorth.png");
                    break;
                case EAST: input = classLoader.getResourceAsStream("pictures/walleast.png");
                    break;
                default: input = null;
                break;
            }
            Image image = new Image(input);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(SPACE_WIDTH);
            imageView.setFitHeight(SPACE_HEIGHT);
            this.getChildren().add(imageView);


/*
            Pane pane = new Pane();
            Rectangle rectangle = new Rectangle();
            rectangle.setFill(Color.TRANSPARENT);
            pane.getChildren().add(rectangle);
            Line line = null;
            switch(wall){
                case SOUTH: line = new Line(2,SPACE_HEIGHT-2,SPACE_WIDTH-2,SPACE_HEIGHT-2);
                    break;
                case WEST: line = new Line (2,2,2,SPACE_HEIGHT-2);
                    break;
                case NORTH: line = new Line(2,2,SPACE_WIDTH-2,2);
                    break;
                case EAST: line = new Line(SPACE_WIDTH-2,SPACE_HEIGHT-2,SPACE_WIDTH-2,2);
                    break;
                default: line = null;
            }
            line.setStroke(Color.RED);
            line.setStrokeWidth(5);
            pane.getChildren().add(line);
            this.getChildren().add(pane);
            */

        }
    }

    /**
     * Draws a checkpoint on the space if one should be drawn
     * @param checkpoint
     * @author Jakob Agergaard
     */
    private void drawCheckPoint(Checkpoint checkpoint){
        if(checkpoint!=null) {
            int number = checkpoint.getCheckpointID();
            InputStream input = classLoader.getResourceAsStream("pictures/checkpoint"+number+".png");
            Image image = new Image(input);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(SPACE_WIDTH);
            imageView.setFitHeight(SPACE_HEIGHT);
            this.getChildren().add(imageView);

            /*
            Text checkpointText = new Text(""+checkpoint.getCheckpointID());
            Circle circle = new Circle(20,Color.GREEN);
            checkpointText.setFont(Font.font("Times New Roman", FontWeight.BOLD,20));
            this.getChildren().add(circle);
            this.getChildren().add(checkpointText);
             */
        }
    }

    /**
     * Draws a pushPanel on the space if one should be drawn
     * @param pushPanel
     */
    private void drawPushPanel(Pushpanel pushPanel){
        if(pushPanel!=null) {
            InputStream input = classLoader.getResourceAsStream("pictures/pushpanel.png");
            Image image = new Image(input);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(SPACE_HEIGHT);
            imageView.setFitWidth(SPACE_WIDTH);
            imageView.setRotate((90 * pushPanel.getHeading().ordinal()+180) % 360);
            this.getChildren().add(imageView);


 /*
            Arc arc = new Arc();
            arc.setCenterX(1.0f);
            arc.setCenterY(1.0f);
            arc.setRadiusX(20.0f);
            arc.setRadiusY(20.0f);
            arc.setStartAngle(90.0f);
            arc.setLength(180.0f);
            arc.setType(ArcType.OPEN);
            arc.setFill(Color.DARKCYAN);

            Rectangle r = new Rectangle();
            r.setX(150);
            r.setY(150);
            r.setWidth(25);
            r.setHeight(5);
            r.setArcWidth(10);
            r.setArcHeight(20);
            r.setFill(Color.DARKCYAN);

            this.getChildren().add(r);
            this.getChildren().add(arc);

  */
        }
    }

    /**
     * Goes through the actions belonging to the space and draws these
     * @param actions
     */
    private void drawActions(List<FieldAction> actions){
        for (FieldAction action : actions) {
            switch (action.getClass().getSimpleName()){
                case "ConveyorBelt":
                    drawConveyorbelt((ConveyorBelt) action);
                    break;
                case "Checkpoint":
                    drawCheckPoint((Checkpoint) action);
                    break;
                case "Pushpanel":
                    drawPushPanel((Pushpanel) action);
                    break;
                case "Gear":
                    drawGear((Gear) action);
                    break;
                case "BoardLaser":
                    drawBoardLaser((BoardLaser) action);
                    break;
                    default:
                    System.out.println("Action not drawn!");
            }
        }
    }

    /**
     * Draws a Boardlaser on the space if one should be drawn
     * @param boardLaser
     */
    private void drawBoardLaser(BoardLaser boardLaser){
        if(boardLaser!=null){
            InputStream input = classLoader.getResourceAsStream("pictures/boardlaser.png");
            Image image = new Image(input);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(SPACE_WIDTH);
            imageView.setFitHeight(SPACE_HEIGHT);
            imageView.setRotate((90 * boardLaser.getHeading().ordinal()+180) % 360);
            this.getChildren().add(imageView);

            /*
            Polygon arrow = new Polygon(0.0, 0.0,
                    15.0, 30.0,
                    30.0, 0.0);
            arrow.setFill(Color.RED);
            arrow.setRotate((90 * boardLaser.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);

             */
        }
    }
    /*

    private void drawLaser(BoardLaser boardLaser){
        if(boardLaser!=null) {
                for (int i = 0; i < boardLaser.stepCount; i++) {
                    Polygon arrow = new Polygon(0.0, 0.0,
                            2, 2,
                            2.0, 0.0);
                    arrow.setFill(Color.RED);
                    arrow.setRotate((90 * boardLaser.getHeading().ordinal()) % 360);
                    this.getChildren().add(arrow);
            }
        }
    }
     */
    /** {@inheritDoc} */
    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();
            drawActions(space.getActions());
            drawWalls(space.getWalls());
            updatePlayer();

        }
    }
}
