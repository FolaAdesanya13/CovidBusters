package game;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/*main game class that works as the 'canvas' for everything*/ 
class CovidBusters extends Game {
	private static final long serialVersionUID = 1L;
	private int spawnLoad = 0;
	private int score = 0;
	private HumanCell cell;
	private ArrayList<CovidCells> covid = new ArrayList<>();
	private ArrayList<BloodStream> blood = new ArrayList<>();
	private hpCheck health;
	private boolean gameOver;

	/*
	 * game constructor that makes an instance of the game, it assigns the human
	 * cell its points and positions and spawns the covid cells and the red blood cells
	 *  into the game
	 */
	public CovidBusters() {
		super("CovidBusters!", 800, 600);
		this.setFocusable(true);
		this.requestFocus();
		
		cell = createHumanCell();
		addKeyListener(cell);
		spawnCovid();
		bloodStream();
		health = new hpCheck(100);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				java.awt.Point gamePoint = toGamePoint(event);
				if (gameOver && replayButton().contains(gamePoint)) {
					replay();
				}
			}
		});
	}

	/* creates the screen and allows for each game object to appear on the screen
	 * also contains annonymous class to manage health and healing
	 * @param brush  uses Java's graphic API to 'draw' the game objects on the screen*/
	public void paint(Graphics brush) {
		brush.setColor(Color.black);
		brush.fillRect(0, 0, width, height);

		if (gameOver) {
			paintGameOver(brush);
			return;
		}
		
		covidInitialize(brush);
		covidSpawnRate();
		
		cell.move();
		cell.rotate();
		cell.paint(brush);
		
		covidCollisionCheck(brush);
		
		bloodFlow(brush);
		
		health.isAlive(brush);
		
			//anonymous class
			hpChange damage = new hpChange() {
				public void change() {
					if(humanCollisionCheck()) {
						health.damage(10);
					}
				}
			};
			
		//lambda function
		hpChange heal = () -> {
			if(bloodCollisionCheck()) {
				health.heal(1);
			}
		};
		damage.change();
		if (health.hp <= 0) {
			gameOver = true;
		} else {
			heal.change();
		}

		if (gameOver) {
			paintGameOver(brush);
		}
	}

	private void paintGameOver(Graphics brush) {
		Graphics2D overlay = (Graphics2D) brush.create();
		overlay.setColor(new Color(0, 0, 0, 190));
		overlay.fillRect(0, 0, width, height);

		overlay.setColor(Color.WHITE);
		overlay.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 42));
		String message = "GAME OVER";
		FontMetrics metrics = overlay.getFontMetrics();
		overlay.drawString(message, (width - metrics.stringWidth(message)) / 2, height / 2 - 35);

		overlay.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		String finalScore = "Final score: " + score;
		metrics = overlay.getFontMetrics();
		overlay.drawString(finalScore, (width - metrics.stringWidth(finalScore)) / 2, height / 2);

		Rectangle button = replayButton();
		overlay.setColor(new Color(40, 150, 70));
		overlay.fillRoundRect(button.x, button.y, button.width, button.height, 12, 12);
		overlay.setColor(Color.WHITE);
		overlay.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		metrics = overlay.getFontMetrics();
		String replayText = "REPLAY";
		overlay.drawString(replayText, button.x + (button.width - metrics.stringWidth(replayText)) / 2,
				button.y + (button.height - metrics.getHeight()) / 2 + metrics.getAscent());
		overlay.dispose();
	}

	private Rectangle replayButton() {
		int buttonWidth = 180;
		int buttonHeight = 48;
		return new Rectangle((width - buttonWidth) / 2, height / 2 + 25, buttonWidth, buttonHeight);
	}

	private void replay() {
		removeKeyListener(cell);
		cell = createHumanCell();
		addKeyListener(cell);
		covid.clear();
		blood.clear();
		score = 0;
		spawnLoad = 0;
		health = new hpCheck(100);
		gameOver = false;
		spawnCovid();
		bloodStream();
		requestFocus();
		repaint();
	}

	private HumanCell createHumanCell() {
		Point[] cellPoints = {
				new Point(0, 0),
				new Point(10, -10),
				new Point(20, 0),
				new Point(20, 15),
				new Point(0, 15)
		};
		return new HumanCell(cellPoints, new Point(width / 2, height / 2), 0);
	}
	
	
	/*
	 * this method checks if the human cell and covid cell have collided, when
	 * collided, it removes the covid cell.
	 *
	 * @return it returns true if collided and false otherwise.
	 */
	public boolean humanCollisionCheck() {
		for (int j = 0; j < covid.size(); j++) {
			CovidCells c = covid.get(j);
			if (cell.collides(c)) {
				covid.remove(j);
				return true;
			}
		}
		return false;
	}
	/*
	 * this method checks if the human cell and the blood stream have collided, when
	 * collided it removes the blood cell
	 *
	 * @return it returns true if collided and false otherwise.
	 */
	public boolean bloodCollisionCheck() {
		for (int j = 0; j < blood.size(); j++) {
			BloodStream b = blood.get(j);
			if (cell.collides(b)) {
				return true;
			}
		}
		return false;
	}
	
	
	/*
	 * This method is the logic for spawning the covid cells, assigns points to
	 * create a circular shape for the covid cells. It generates a random number
	 * between 0 and 2 and then depending on that number the covid cell will be
	 * spawned from either the top, right, or left side of the screen and then
	 * calculates the distance between the cell's spawn coordinates and the center
	 * of the screen and rotates the cell until it reaches the center
	 */	
	public void spawnCovid() {
	    Point[] covidShape = {
	        new Point(0, 5), new Point(5, 0), new Point(10, 0), 
	        new Point(15, 5), new Point(15, 10), new Point(10, 15), 
	        new Point(5, 15), new Point(0, 10)
	    };

	    double spawnX = 0, spawnY = 0;
	    int edge = (int) (Math.random() * 3);

	    if (edge == 0) {
	        spawnX = Math.random() * width;
	        spawnY = -30;
	    }else if (edge == 1) {
	        spawnX = -30;
	        spawnY = Math.random() * height;
	    } else {
	        spawnX = width + 30;
	        spawnY = Math.random() * height;
	    }

	    double angleToCenter = Math.toDegrees(Math.atan2(height / 2.0 - spawnY, width / 2.0 - spawnX));
	    covid.add(new CovidCells(covidShape, new Point(spawnX, spawnY), angleToCenter));
	}
	
	/*
	 * this method controls the spwan rate of the covid cells, it spawns a covid
	 * cell every 50 frames
	 */	
	 public void covidSpawnRate() {
		spawnLoad++;
		if (spawnLoad % 50 == 0) {
	        spawnCovid();
	    }
	}
	
	/*
	 * This method allows for the covid cells to appear on screen and if they go too
	 * far offscreen then that cell is deleted so it doesn't take up space in memory
	 * @param brush uses Java's graphic API to 'draw' the game objects on the screen
	 */	
	 public void covidInitialize(Graphics brush) {
		for (int i = 0; i < covid.size(); i++) {
		    CovidCells c = covid.get(i);
		    c.move();
		    c.paint(brush);
		    if (c.position.x < -100 || c.position.x > width + 100
		    		|| c.position.y < -100 || c.position.y > height + 100) {
	            covid.remove(i);
	            i--;
	        }
		}
	}
	
	/*
	 * This method checks whether the covid cell has collided with a projectile, if
	 * it did then delete both the cell and the projectile and increment the score
	 * by 1
	 * @param brush  uses Java's graphic API to 'draw' the game objects on the screen
	 */	
	 public void covidCollisionCheck(Graphics brush) {
		brush.setColor(Color.white);
		brush.drawString("SCORE: " + score, 20, 20);
		
		for (int i = 0; i < cell.projectiles.size(); i++) {
	        HumanCell.Projectile p = cell.projectiles.get(i);

	        for (int j = 0; j < covid.size(); j++) {
	            CovidCells c = covid.get(j);

	            if (p.collides(c)) {
	            	score++;
	                covid.remove(j);
	                cell.projectiles.remove(i);
	                
	                i--;
	                break;
	            }
	        }
		}
	}
	
	/* This controls and spawns the red blood cells randomly from the top of the screen */
	public void bloodStream() {        
		Point[] bloodShape = {
			    new Point(0, 2), new Point(2, 0), new Point(4, 0), 
			    new Point(6, 2), new Point(6, 4), new Point(4, 6), 
			    new Point(2, 6), new Point(0, 4)
		};
		
		double spawnX = Math.random() * width;
	    double spawnY = -30;
	    
	    double direction = Math.random() * 360;
	    
	    blood.add(new BloodStream(bloodShape, new Point(spawnX, spawnY), direction));
	}
	
	/*
	 * This spawns the red blood cells every 10 frames and deletes it once it goes
	 * offscreen
	 * @param brush  uses Java's graphic API to 'draw' the game objects on the screen
	 */	
	public void bloodFlow(Graphics brush) {
	    if (spawnLoad % 4 == 0) {
	        bloodStream();
	    }

	    for (int i = 0; i < blood.size(); i++) {
	        BloodStream b = blood.get(i);
	        b.move();
	        b.paint(brush);
  
	        if (b.position.x < -50 || b.position.x > width + 50 || b.position.y < -50
	        		|| b.position.y > height + 50) {
	            blood.remove(i);
	            i--;
	        }
	    }
	}
	
	/*
	 * this interface acts as the blueprint in changing hp
	 */	
	interface hpChange{
		void change();
	}
	
	/* this inner class for covidBusters
	 * checks for the current state of the human cell hp */
	public class hpCheck{
		int hp;
		
		/*
		 * contructor sets the hp for human cell
		 *
		 * @param set the amount of hp to the parameter
		 */
		hpCheck(int hp){
			this.hp = hp;
		}
		/*
		 * draws the hp amount onto the screen
		 *
		 * @param brush uses Java's graphic API to 'draw' the game objects on the screen
		 */		
		void isAlive(Graphics brush) {
			brush.setColor(Color.white);
			brush.drawString("HEALTH: " + hp, 20, 40);
		}
		/*
		 * heals the player when hp is below 100
		 *
		 * @param amount healed
		 */
		void heal(int i) {
			if(hp < 100) {
				hp+=i;
			}else {
				hp = 100;
			}
		}
		/*
		 * removes damage amount from hp
		 *
		 * @param amount of damage taken
		 */
		void damage(int i) {
			hp = Math.max(0, hp - i);
		}
	}

	public static void main(String[] args) {
		CovidBusters a = new CovidBusters();
		a.repaint();
	}
}