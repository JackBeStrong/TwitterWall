package jack.twitter_wall;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import twitter4j.TwitterException;

public class App extends JFrame implements Runnable {

	private static final long serialVersionUID = 650586756696454165L;

	public static int width = 1600;
	public static int height = 900;
	public static int bodyCount = 15;
	public static int maxBodyCount = 150;
	public static int maxBodySize = 20;
	public static double restitution = 0.9;
	public static Font font;

	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private JFrame frame;
	private Canvas canvas;
	private World world;
	private boolean running;
	private long last;
	private FontMetrics fontMetrics;
	public TwitterDealer twitterDealer;
	private long total = 0;

	public App() {
		frame = new JFrame("Twitter wall!");
		canvas = new Canvas();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.setSize(width, height);
		frame.add(canvas);
		frame.pack();
		frame.setVisible(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		canvas.addMouseListener(new MouseAction(this));
		initialize();
	}

	private void initialize() {
		this.canvas.createBufferStrategy(3);
		Graphics2D g = (Graphics2D) canvas.getBufferStrategy()
				.getDrawGraphics();
		font = new Font(GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames()[0], Font.PLAIN, 30);
		g.setFont(font);
		// fontMetrics = g.getFontMetrics(font);
		fontMetrics = g.getFontMetrics();
		g.dispose();
		world = new World(new AxisAlignedBounds(width, height));

		// set up twitter
		twitterDealer = new TwitterDealer();
		// add bodies
		for (int i = 0; i < bodyCount; i++) {

			addBody();
		}
		// add floor
		TwitterBody floor = new TwitterBody(Color.white);
		floor.addFixture(new Rectangle(width, 1));
		floor.translate(0, -height / 2);
		floor.setMass(MassType.INFINITE);
		world.addBody(floor);
		// add left wall
		TwitterBody leftWall = new TwitterBody(Color.white);
		leftWall.addFixture(new Rectangle(1, height));
		leftWall.translate(-width / 2, 0);
		leftWall.setMass(MassType.INFINITE);
		world.addBody(leftWall);
		// add right wall
		TwitterBody rightWall = new TwitterBody(Color.white);
		rightWall.addFixture(new Rectangle(1, height));
		rightWall.translate(width / 2, 0);
		rightWall.setMass(MassType.INFINITE);
		world.addBody(rightWall);

		// start thread
		Thread thread1 = new Thread(this);
		thread1.start();
	}

	public void run() {
		running = true;
		last = System.nanoTime();
		while (running) {
			render();
			update();
		}
	}

	private synchronized void update() {
		long time = System.nanoTime();
		long diff = time - last;
		world.update(diff / 1E9);
		// remove earlier bodies when more than maximum body number allowed
		if (world.getBodyCount() >= maxBodyCount) {
			for (Body b : world.getBodies()) {
				if (!b.getMass().isInfinite()) {
					world.removeBody(b);
					break;
				}
			}
		}
		total += diff;
		if (total / 1E9 >= 1) {
			addBody();
			total = 0;
		}
		last = System.nanoTime();
	}

	private synchronized void render() {
		Graphics2D g = (Graphics2D) this.canvas.getBufferStrategy()
				.getDrawGraphics();
		g.setFont(font);
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		// g.setColor(Color.white);
		// g.drawString("JACK", width/2, height/2);
		AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
		AffineTransform move = AffineTransform.getTranslateInstance(width / 2,
				-height / 2);
		g.transform(yFlip);
		g.transform(move);

		// refresh background

		for (Body body : world.getBodies()) {
			((TwitterBody) body).render(g);
		}
		g.dispose();
		BufferStrategy strategy = this.canvas.getBufferStrategy();
		if (!strategy.contentsLost()) {
			strategy.show();
		}
	}

	public String randomString() {
		StringBuilder builder = new StringBuilder();
		int count = (int) (Math.random() * 50 + 1);
		while (count-- != 0) {
			int character = (int) (Math.random()
					* ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}

	public synchronized void addBody() {
		String text = "";
		try {
			text = twitterDealer.nextTweetString();
		}
		catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (text.isEmpty()) {
			return;
		}
		System.out.println(text);
		int textWidth = fontMetrics.stringWidth(text);
		int textHeight = fontMetrics.getAscent();
		if (textWidth <= 0 || textHeight <= 0) {
			return;
		}
		TwitterBody body = new TwitterBody(text, textWidth, textHeight);

		body.addFixture(new Rectangle(textWidth, textHeight));
		body.translate(Math.random() * width - width / 2, height / 2);
		body.setMass(MassType.NORMAL);
		body.getFixture(0).setRestitution(restitution);
		world.addBody(body);
	}

	public static void main(String[] args) {
		new App();
	}

}