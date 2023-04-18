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

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 75; // 60; // 75;
    final public static int SPACE_WIDTH = 75;  // 60; // 75;

    public final Space space;

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

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();
        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }


    private void updatePlayer() {
        this.getChildren().clear();

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
     */

    private void drawConveyorbelt(ConveyorBelt conveyorBelt){
        if(conveyorBelt!=null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    5.0, 10.0,
                    10.0, 0.0);
            arrow.setFill(Color.GREY);
            arrow.setRotate((90 * conveyorBelt.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }
    }

    /*private void drawGear(Gear gear){
        if(gear!=null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    7.0, 10.0,
                    7.0, 0.0);
            arrow.setFill(Color.DARKRED);
            this.getChildren().add(arrow);
        }
    }
    */

    /**
     * Draws all walls belonging to this space
     *
     * @param walls     A list of walls that is placed on the space being created
     * @author Jakob Agergaard
     */
    private void drawWalls(List<Heading> walls){
        for(Heading wall:walls){
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
        }
    }

    private void drawCheckPoint(Checkpoint checkpoint){
        if(checkpoint!=null) {
            Polygon diamond = new Polygon(10.0, 20.0,
                    30.0, 40.0,
                    40.0, 30.0);
            diamond.setFill(Color.GREEN);
            this.getChildren().add(diamond);
        }
    }
    private void drawPushPanel(Pushpanel pushPanel){
        if(pushPanel!=null) {
            CubicCurve cubic= new CubicCurve();
            cubic.setStartX(0.0f);
            cubic.setStartY(15.0f);
            cubic.setControlX1(25.0f);
            cubic.setControlY1(0.0f);
            cubic.setControlX2(35.0f);
            cubic.setControlY2(20.0f);
            cubic.setEndX(10.0f);
            cubic.setEndY(15.0f);
            cubic.setFill(Color.DARKBLUE);
            this.getChildren().add(cubic);
        }
    }
    private void drawActions(List<FieldAction> actions){
        for (FieldAction action : actions) {
            switch (action.getClass().getSimpleName()){
                case "ConveyorBelt":
                    drawConveyorbelt((ConveyorBelt) action);
                    break;
                case "Checkpoint":
                    drawCheckPoint((Checkpoint) action);
                    break;
                case "Boardlaser": ;
                    break;
                case "Pushpanel":
                    drawPushPanel((Pushpanel) action);
                case "BoardLaser":
                    drawBoardLaser((BoardLaser) action);
                    break;
                    default:
                    System.out.println("Action not drawn!");
            }
        }
    }


    private void drawBoardLaser(BoardLaser boardLaser){
        if(boardLaser!=null){
            Polygon arrow = new Polygon(0.0, 0.0,
                    15.0, 30.0,
                    30.0, 0.0);
            arrow.setFill(Color.RED);
            arrow.setRotate((90 * boardLaser.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }
    }
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




    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }
        drawWalls(space.getWalls());
        drawLaser(space.getBoardLaser());
        drawActions(space.getActions());


    }

}
