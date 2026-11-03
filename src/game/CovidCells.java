package game;

import java.awt.Color;
import java.awt.Graphics;

/*Class for the covid cells, the 'enemy' of the game*/
public class CovidCells extends Polygon{
	
	/* Covid constructor, calls the Polygon constructor to create the cell 
	 * @param inShape  array of point objects this controls the shape of the cell
	 * @param inPosition Point object that controls the coordinates of the cell
	 * @param inRotation Point object that controls the rotation of the cell
	 */
	public CovidCells(Point[] inShape, Point inPosition, double inRotation) {
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
        
        brush.setColor(Color.GREEN);
        brush.fillPolygon(xPoints, yPoints, points.length);
    }

    
    /*
	 * This method controls the movement of the cell 
	 * by changing the coordinates of the cell continuously
	 */
    public void move() {
        double radians = Math.toRadians(rotation);
        position.x += Math.cos(radians) * 2;
        position.y += Math.sin(radians) * 2;
    }
}
