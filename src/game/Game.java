package game;

/*
CLASS: Game
DESCRIPTION: A painted canvas in its own window, updated every tenth second.
USAGE: Extended by YourGameName.
*/
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

abstract class Game extends Canvas {
	private static final long serialVersionUID = 1L;
	protected boolean on = true;
	protected int width, height;
	protected Image buffer;

	public Game(String name, int inWidth, int inHeight) {
		width = inWidth;
		height = inHeight;

		// Frame can be read as 'window' here.
		Frame frame = new Frame(name);
		frame.add(this);
		frame.setSize(width, height);
		frame.setResizable(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}

	// 'paint' will be called every tenth of a second that the game is on.
	abstract public void paint(Graphics brush);

	// 'update' paints to a buffer then to the screen, then waits a tenth of
	// a second before repeating itself, assuming the game is on. This is done
	// to avoid a choppy painting experience if repainted in pieces.
	public void update(Graphics brush) {
		int canvasWidth = getWidth();
		int canvasHeight = getHeight();
		if (canvasWidth <= 0 || canvasHeight <= 0) {
			return;
		}

		if (buffer == null || buffer.getWidth(this) != canvasWidth || buffer.getHeight(this) != canvasHeight) {
			buffer = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
		}

		Graphics2D graphics = (Graphics2D) buffer.getGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, canvasWidth, canvasHeight);
		double scale = renderScale();
		int offsetX = (int) ((canvasWidth - width * scale) / 2);
		int offsetY = (int) ((canvasHeight - height * scale) / 2);
		graphics.translate(offsetX, offsetY);
		graphics.scale(scale, scale);
		paint(graphics);
		graphics.dispose();

		brush.drawImage(buffer, 0, 0, this);
		if (on) {
			sleep(10);
			repaint();
		}
	}

	protected java.awt.Point toGamePoint(MouseEvent event) {
		double scale = renderScale();
		int offsetX = (int) ((getWidth() - width * scale) / 2);
		int offsetY = (int) ((getHeight() - height * scale) / 2);
		return new java.awt.Point(
				(int) ((event.getX() - offsetX) / scale),
				(int) ((event.getY() - offsetY) / scale));
	}

	private double renderScale() {
		return Math.min((double) getWidth() / width, (double) getHeight() / height);
	}

	// 'sleep' is a simple helper function used in 'update'.
	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception exc) {
		}
		;
	}
}