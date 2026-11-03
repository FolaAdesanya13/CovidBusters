package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/*class for the human cell, controls its movement and rotation 
 * and also contains the projectile as an inner class 
 * so to spawn projectiles from the human cell position
*/
public class HumanCell extends Polygon implements KeyListener {
	private boolean upKey, downKey, rightKey, leftKey, rightRotate, leftRotate;
	public ArrayList<Projectile> projectiles = new ArrayList<>();
	private long lastShotTime = 0, cooldown = 200;

	
	/* Human cell constructor, calls the Polygon constructor to create the cell 
	 * @param inShape  array of point objects this controls the shape of the cell
	 * @param inPosition Point object that controls the coordinates of the cell
	 * @param inRotation Point object that controls the rotation of the cell*/
	public HumanCell(Point[] inShape, Point inPosition, double inRotation) {
		super(inShape, inPosition, inRotation);
	}
	
	
	/*
	 * This method 'draws' the cell by creating points of the cell and then uses the
	 * fillPolygon method to essentially just fill it out and allows for the
	 * projectile to spawn starting at the human cell
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
        
        brush.setColor(Color.WHITE);
        brush.fillPolygon(xPoints, yPoints, points.length);
        
        for (Projectile p : projectiles) {
            p.projectilePaint(brush);
        }
	}
	
	/*
	 * This method allows the cell to move according to what key is pressed, it also
	 * handles the projectiles movements and once it reaches the edge of the screen
	 * it gets delted so as to not take up memory
	 */	
	public void move() {
		int amtToMove = 5;
		
		if(upKey) {
			this.position.y -= amtToMove;
		}
		if(rightKey) {
			this.position.x += amtToMove;
		}
		if(downKey) {
			this.position.y += amtToMove;
		}
		if(leftKey) {
			this.position.x -= amtToMove;
	    }
		
		for (int i = 0; i < projectiles.size(); i++) {
	        projectiles.get(i).projectileMove();
	        
	        if (projectiles.get(i).position.x < 0 || projectiles.get(i).position.x > 800 || 
	            projectiles.get(i).position.y < 0 || projectiles.get(i).position.y > 600) {
	            projectiles.remove(i);
	            i--;
	        }
		}
	}
	
	/*
	 * This method handles the rotation of the cell depending on which direction it
	 * is supposed to be rotated in
	 */	
	public void rotate() {
		int rightRotation = -5;
		int leftRotation = 5;
		
		if (rightRotate) {
			rotate(rightRotation);
		}
		if (leftRotate) {
			rotate(leftRotation);
		}
	}
	public void keyTyped(KeyEvent e) {}

	/*
	 * this method handles all the keyboard inputs, if the corresponding key is
	 * pressed then the cell will move or be rotated or a projectile will be spawned
	 * @param e from the KeyEvent interface reads keyboard input
	 */	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP){
			upKey = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT){
			leftKey = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT){
			rightKey = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN){
			downKey = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_A) {
			rightRotate = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_D) {
			leftRotate = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			long currentTime = System.currentTimeMillis();
			
	        Point[] projectile = {new Point(0,0), new Point(5,0), new Point(5,5), new Point(0,5)};
	        
	        if (currentTime - lastShotTime > cooldown) {
	            lastShotTime = currentTime;
	            
	            Projectile p = new Projectile(projectile, new Point(this.position.x, this.position.y), this.rotation);
		        projectiles.add(p);
	        }
	    }
	}

	
	/*
	 * this method checks if the corresponding key has been released
	 * and stops the movement or rotation of the cell
	 * @param e from the KeyEvent interface reads keyboard input
	 */	
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP){
			upKey = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT){
			leftKey = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT){
			rightKey = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN){
			downKey = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_A) {
			rightRotate = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_D) {
			leftRotate = false;
		}
	}
	
	
	/*
	 * Projectile is an inner class of human cell that allows the human cell to
	 * spawn projectiles and shoot at covid
	 */	
	public class Projectile extends Polygon{
		private int speed = -5;

		/* Projectile constructor, calls the Polygon constructor to create the projectile 
		 * @param inShape  array of point objects this controls the shape of the cell
		 * @param inPosition Point object that controls the coordinates of the cell
		 * @param inRotation Point object that controls the rotation of the cell
		 */
		public Projectile(Point[] inShape, Point inPosition, double inRotation) {
			super(inShape, inPosition, inRotation);
		}
		
		
		/*
		 * This method 'draws' the projectile by creating points of the cell and then uses the
		 * fillPolygon method to essentially just fill it out and allows for the
		 * @param brush  uses Java's graphic API to 'draw' the game objects on the screen
		 */
		public void projectilePaint(Graphics brush) {
            Point[] points = getPoints();
            int[] xPoints = new int[points.length];
            int[] yPoints = new int[points.length];

            for (int i = 0; i < points.length; i++) {
                xPoints[i] = (int) points[i].x;
                yPoints[i] = (int) points[i].y;
            }
            
            brush.setColor(Color.CYAN);
            brush.fillPolygon(xPoints, yPoints, points.length);
		}
		
		/*
		 * This method controls the movement of the projectile by getting the position
		 * and rotation of the human cell and then changing the coordinates of the
		 * projectile by the speed so it moves in the correct direction
		 */
		public void projectileMove() {
			double radians = Math.toRadians(rotation);
			this.position.x += Math.sin(-radians) * speed;
	        this.position.y += Math.cos(radians) * speed;
		}
		
	}

}
