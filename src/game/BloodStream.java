package game;

import java.awt.Color;
import java.awt.Graphics;

public class BloodStream extends Polygon {

	public BloodStream(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
    }

	
	/*
	 * This method 'draws' the cell by creating points of the cell and then uses the
	 * fillPolygon method to essentially just fill it out and allows for the
	 * @param brush  uses Java's graphic API to 'draw' the game objects on the screen
	 */
    public void paint(Graphics brush) {
        Point[] points = getPoints();
        int[] xPoints = new int[points.length];
        int[] yPoints = new int[points.length];
        
        for (int i = 0; i < points.length; i++) {
            xPoints[i] = (int) points[i].x;
            yPoints[i] = (int) points[i].y;
        }
        
        brush.setColor(Color.RED);
        brush.fillPolygon(xPoints, yPoints, points.length);
    }

    
    /*
	 * This method controls the movement of the cell 
	 * by changing the y position of the cell continuously
	 */
    public void move() {
        position.y += 7;
    }
}
