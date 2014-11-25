package pPagent;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class Life extends JFrame implements Runnable, MouseListener,
		KeyListener {

	// petla glowna
	Thread gameloop;

	BufferedImage backbuffer;
	Graphics2D g2d;

	int width = 900;
	int height = 600;

	int CREATURES = 50;
	List<Creature> creature = Collections
			.synchronizedList(new ArrayList<Creature>());

	int CARNIVORES = 8;
	List<Carnivore> carnivore = Collections
			.synchronizedList(new ArrayList<Carnivore>());

	int GRASS = 2000;
	List<Grass> grass = Collections.synchronizedList(new ArrayList<Grass>());

	AffineTransform identity = new AffineTransform();
	Random rand = new Random();

	// number of frames past
	int dataNum = 0;

	// MouseListener variables
	int clickX, clickY;
	int mouseButton;

	// font for displaying data
	Font font = new Font("Courier", Font.PLAIN, 12);
	// best creature/carnivore
	Creature bestC = new Creature();
	Carnivore bestCa = new Carnivore();

	// toggles
	boolean data = false;
	boolean photonDraw = false;
	boolean sightCircle = false;

	public static void main(String[] args) {
		new Life();
	}

	// default constructor
	public Life() {
		super("Life_2.0");
		setSize(900, 632); // 32 is for JFrame top bar
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameloop = new Thread(this);
		gameloop.start();
		init();
	}

	// application init event
	public void init() {

		for (int n = 0; n < CREATURES; n++) {
			Creature c = new Creature();
			// set up creature variables
			c.setX(rand.nextInt(906) + 5); // separates them nicely
			c.setY(rand.nextInt(600));
			c.setAlive(true);
			c.setSight(10);
			c.setEnergy(150);
			creature.add(c);
		}
		for (int n = 0; n < CARNIVORES; n++) {
			Carnivore c = new Carnivore();
			// set up creature variables
			c.setX(rand.nextInt(906) + 5); // separates them nicely
			c.setY(rand.nextInt(600));
			c.setAlive(true);
			c.setSpeed(1);
			c.setSp(2);
			c.setSi(width / 4);
			c.setEnergy(150);
			// c.setDebug(true);
			carnivore.add(c);
		}
		for (int n = 0; n < GRASS; n++) {
			Grass p = new Grass();
			// set up photon variables
			p.setX(rand.nextInt(width));
			p.setY(rand.nextInt(height));
			p.setAlive(true);
			// p.setSpeed(rand.nextInt(2)+1);
			p.setEnergy(200);
			grass.add(p);
		}
		// create the backbuffer for smooth-ass graphics
		backbuffer = new BufferedImage(width + 200, height,
				BufferedImage.TYPE_INT_RGB);
		g2d = backbuffer.createGraphics();
		// for mouse input
		addMouseListener(this);
		// for keyboard input
		addKeyListener(this);
	}

	// repaint event draws the backbuffer
	public void paint(Graphics g) {
		// draw the backbuffer to the window
		g.drawImage(backbuffer, 0, 29, this);
		// start off transforms at identity

		g2d.setTransform(identity);
		// erase the background
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(900, 0, 0, height);
		// draw the things
		drawCreatures();
		drawCarnivores();
		if (!photonDraw) {
			drawPhotons();
		}
	}

	// drawCreatures called by update
	public void drawCreatures() {
		for (int n = 0; n < creature.size(); n++) {
			Creature c = creature.get(n);
			if (c.isAlive()) {
				// draw the creature
				g2d.setTransform(identity);
				g2d.translate(c.getX(), c.getY());
				g2d.rotate(Math.toRadians(c.getMoveAngle()));
				if (sightCircle) {
					// sightRadius circle with R, G, B, Transparency
					g2d.setColor(new Color(0, 0, 1, 0.3f));
					g2d.fill(new Ellipse2D.Double(-c.getSi() / 2,
							-c.getSi() / 2, c.getSi(), c.getSi()));
				}
				g2d.setColor(c.getColor());
				g2d.draw(c.getShape());
			}
		}
	}

	// drawCarnivores called by update
	public void drawCarnivores() {
		if (carnivore.size() > 0) {
			for (int n = 0; n < carnivore.size(); n++) {
				Carnivore ca = carnivore.get(n);
				if (ca.isAlive()) {
					// draw the carnivore
					g2d.setTransform(identity);
					g2d.translate(ca.getX(), ca.getY());
					g2d.rotate(Math.toRadians(ca.getMoveAngle()));
					if (sightCircle) {
						// sightRadius circle with R, G, B, Transparency
						g2d.setColor(new Color(0, 0, 1, 0.3f));
						g2d.fill(new Ellipse2D.Double(-ca.getSi() / 2, -ca
								.getSi() / 2, ca.getSi(), ca.getSi()));
					}
					g2d.setColor(ca.getColor());
					g2d.draw(ca.getShape());
				}
			}
		}
	}

	// drawPhotons called by update
	public void drawPhotons() {
		for (int n = 0; n < grass.size(); n++) {
			Grass p = grass.get(n);
			if (p.isAlive()) {
				// draw the photon
				g2d.setTransform(identity);
				g2d.translate(p.getX(), p.getY());
				g2d.setColor(new Color(0, 100, 0));
				g2d.draw(p.getShape());
			}
		}
	}

	public void printData() {
		g2d.setTransform(identity);
		// indent
		g2d.translate(15, 10);
		g2d.setColor(new Color(0, 0, 1, 0.5f));
		g2d.setFont(font);
		// creature/carnivore ratio bar
		g2d.setColor(new Color(1, 1, 1, 0.5f));
		Rectangle2D white = new Rectangle2D.Double(0, 0, creature.size()
				* (200 / (creature.size() + carnivore.size())), 20);
		g2d.fill(white);
		g2d.setColor(new Color(1, 0, 0, 0.5f));
		Rectangle2D red = new Rectangle2D.Double(
				creature.size() * (200 / (creature.size() + carnivore.size())),
				0,
				carnivore.size() * (200 / (creature.size() + carnivore.size())),
				20);
		g2d.fill(red);
		// set font
		g2d.setColor(new Color(0, 0, 1, 0.5f));
		g2d.setFont(font);
		// populations
		g2d.translate(0, 30);
		g2d.drawString("Creatures: " + creature.size(), 5, 0);
		g2d.drawString("Carnivores: " + carnivore.size(), 5, 15);
		g2d.drawString("Photons: " + grass.size(), 5, 30);
		// best creature
		g2d.translate(0, 60);
		g2d.drawString("Best Creature:", 5, 0);
		g2d.drawString("Position: " + bestC.getX() + ", " + bestC.getY(), 5, 15);
		// g2d.drawString("MutatedShapeCheck: "+bestC.getMutatedShapeCheck(), 5,
		// 30);
		g2d.drawString("Sight: " + bestC.getSi(), 5, 45);
		g2d.drawString("speed: " + bestC.getSp(), 5, 60);
		g2d.drawString("Up/Down Bias: " + bestC.getudB(), 5, 75);
		g2d.drawString("Left/Right Bias: " + bestC.getlrB(), 5, 90);
		// g2d.drawString("Number of Mutations: "+bestC.getMutateNum(), 5, 105);
		// draw best creature
		g2d.translate(50, 150);
		int[] x1 = new int[8];
		int[] y1 = new int[8];
		for (int n = 0; n < bestC.getCrX().length; n++) {
			x1[n] = bestC.getCrX()[n] * 4;
			y1[n] = bestC.getCrY()[n] * 4;
		}
		Polygon bigC = new Polygon(x1, y1, 8);
		g2d.draw(bigC);
		// best carnivore
		g2d.translate(-50, 80);
		g2d.drawString("Best Carnivore:", 5, 0);
		g2d.drawString("Position: " + bestCa.getX() + ", " + bestCa.getY(), 5,
				15);
		// g2d.drawString("MutatedShapeCheck: "+bestCa.getMutatedShapeCheck(),
		// 5, 30);
		g2d.drawString("Sight: " + bestCa.getSi(), 5, 45);
		g2d.drawString("speed: " + bestCa.getSp(), 5, 60);
		g2d.drawString("Up/Down Bias: " + bestCa.getudB(), 5, 75);
		g2d.drawString("Left/Right Bias: " + bestCa.getlrB(), 5, 90);
		// g2d.drawString("Number of Mutations: "+bestCa.getMutateNum(), 5,
		// 105);
		// draw best carnivore
		g2d.translate(50, 150);
		int[] x2 = new int[8];
		int[] y2 = new int[8];
		for (int n = 0; n < bestCa.getCrX().length; n++) {
			x2[n] = bestCa.getCrX()[n] * 4;
			y2[n] = bestCa.getCrY()[n] * 4;
		}
		Polygon bigCa = new Polygon(x2, y2, 8);
		g2d.draw(bigCa);
	}

	public void run() {
		// aquire the current thread
		Thread t = Thread.currentThread();
		// keep going as long as the thread is alive
		while (t == gameloop) {
			try {
				gameUpdate();
				// target framerate is 50fps
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}

	// stop thread event
	public void stop() {
		// kill the gameloop thread
		gameloop = null;
	}

	// move and animate objects in the game
	public void gameUpdate() {
		// updateGrass();
		updateCreatures();
		updateCarnivores();
		checkCollisions();
		deadCollect();
		spawnGrass();
		spawnCreatureCheck();
		resetCheck();
		dataNum++;
	}

	/*
	 * public void updateGrass() { for(int n=0; n<grass.size(); n++) { Grass p =
	 * grass.get(n);
	 * 
	 * } }
	 */

	public void updateCreatures() {
		double targetX = 0;
		double targetY = 0;

		Grass g = null;
		int otherwise = 0;

		for (int n = 0; n < creature.size(); n++) {
			Creature c = creature.get(n);
			if (c.isAlive()) {
				double best = c.getSi();
				if (rand.nextInt(4) <= 2 || c.isDebug()) {
					for (int j = 0; j < grass.size(); j++) {
						g = grass.get(j);
						if (Math.abs(g.getX() - c.getX()) <= c.getSight()
								|| Math.abs(g.getY() - c.getY()) <= c
										.getSight()) {
							double distance = getDistance(c, g);
							if (distance < best) {
								c.setXRef(calcRef(c.getX(), g.getX(), width));
								c.setYRef(calcRef(c.getY(), g.getY(), height));
								targetX = g.getX();
								targetY = g.getY();
								best = distance;
							} else {
								otherwise++;
							}
						}
					}

					if (otherwise < grass.size()) {
						if (Math.abs(c.getX() - targetX) > 0) {
							c.incX(c.getXRef() * c.getSp());
						}
						if (Math.abs(c.getY() - targetY) > 0) {
							c.incY(c.getYRef() * c.getSp());
						}
					} else if (!c.isDebug()) {
						randomMove(c);
					}
				} else if (!c.isDebug()) {
					randomMove(c);
				}

				// top/bottom
				if (c.getY() <= 0) {
					c.setY(height);
				} else if (c.getY() >= height) {
					c.setY(0);
				}
				// left/right
				if (c.getX() <= 0) {
					c.setX(width);
				} else if (c.getX() >= width) {
					c.setX(0);
				}
				// spadek energii
				if (!c.isDebug()) {
					c.decEnergy(1 + (0.5 * c.getSpeed()) + (0.1 * c.getSight()));
				}
				// smierc
				if (c.getEnergy() <= 0) {
					c.setAlive(false);
				}

			}
		}
	}

	public void updateCarnivores() {
		double targetX = 0;
		double targetY = 0;
		for (int i = 0; i < carnivore.size(); i++) {
			Carnivore ca = carnivore.get(i);
			// nie zawsze poluje
			if (rand.nextInt(4) <= 2) {
				double best = ca.getSi();
				int otherwise = 0;
				for (int j = 0; j < creature.size(); j++) {
					Creature c = creature.get(j);
					double distance = getDistance(ca, c);
					if (distance < best) {
						ca.setXRef(calcRef(ca.getX(), c.getX(), width));
						ca.setYRef(calcRef(ca.getY(), c.getY(), height));
						targetX = c.getX();
						targetY = c.getY();
						best = distance;
					} else {
						otherwise++;
					}
				}
				if (otherwise < creature.size()) {
					if (Math.abs(ca.getX() - targetX) >= 3) {
						ca.incX(ca.getXRef() * ca.getSp());
					}
					if (Math.abs(ca.getY() - targetY) >= 3) {
						ca.incY(ca.getYRef() * ca.getSp());
					}
				} else if (!ca.isDebug()) {
					randomMove(ca);
				}
			} else if (!ca.isDebug()) {
				randomMove(ca);
			}

			// top/bottom
			if (ca.getY() <= 0) {
				ca.setY(height);
			} else if (ca.getY() >= height) {
				ca.setY(0);
			}
			// left/right
			if (ca.getX() <= 0) {
				ca.setX(width);
			} else if (ca.getX() >= width) {
				ca.setX(0);
			}

			ca.decEnergy(1 + (0.3 * ca.getSpeed()) + (0.1 * ca.getSight()));
			if (ca.getEnergy() <= 0) {
				ca.setAlive(false);
			}
		}
	}

	public void randomMove(Carnivore ca) {
		if (rand.nextInt(31) + 1 < 9 + ca.getudB()) {
			ca.incY(ca.getSp());
		} else if (rand.nextInt(31) + 1 > 20 + ca.getudB()) {
			ca.incY(-ca.getSp());
		} else {
			if (rand.nextInt(2) + 1 <= 1) {
				ca.incX(ca.getSp());
			} else {
				ca.incX(-ca.getSp());
			}
		}
		if (rand.nextInt(31) + 1 < 9 + ca.getlrB()) {
			ca.incX(ca.getSpeed());
		} else if (rand.nextInt(31) + 1 > 20 + ca.getlrB()) {
			ca.incX(-ca.getSpeed());
		} else {
			if (rand.nextInt(2) + 1 <= 1) {
				ca.incX(ca.getSpeed());
			} else {
				ca.incX(-ca.getSpeed());
			}
		}
	}

	public void randomMove(Creature ca) {
		if (rand.nextInt(31) + 1 < 9 + ca.getudB()) {
			ca.incY(ca.getSp());
		} else if (rand.nextInt(31) + 1 > 20 + ca.getudB()) {
			ca.incY(-ca.getSp());
		} else {
			if (rand.nextInt(2) + 1 <= 1) {
				ca.incX(ca.getSp());
			} else {
				ca.incX(-ca.getSp());
			}
		}
		if (rand.nextInt(31) + 1 < 9 + ca.getlrB()) {
			ca.incX(ca.getSpeed());
		} else if (rand.nextInt(31) + 1 > 20 + ca.getlrB()) {
			ca.incX(-ca.getSpeed());
		} else {
			if (rand.nextInt(2) + 1 <= 1) {
				ca.incX(ca.getSpeed());
			} else {
				ca.incX(-ca.getSpeed());
			}
		}
	}

	// odl miedzy
	public double getDistance(Carnivore ca, Creature c) {
		double xSeparation = Math.abs(ca.getX() - c.getX());
		double ySeparation = Math.abs(ca.getY() - c.getY());
		if (Math.abs(ca.getX() - c.getX()) > width / 2) {
			xSeparation = width - xSeparation;
		}
		if (Math.abs(ca.getY() - c.getY()) > height / 2) {
			ySeparation = height - ySeparation;
		}
		double totalSeparation = Math.sqrt(Math.pow(xSeparation, 2)
				+ Math.pow(ySeparation, 2));
		return totalSeparation;
	}

	public double getDistance(Creature ca, Grass c) {
		double xSeparation = Math.abs(ca.getX() - c.getX());
		double ySeparation = Math.abs(ca.getY() - c.getY());
		// look through walls
		if (Math.abs(ca.getX() - c.getX()) > width / 2) {
			xSeparation = width - xSeparation;
		}
		if (Math.abs(ca.getY() - c.getY()) > height / 2) {
			ySeparation = height - ySeparation;
		}
		double totalSeparation = Math.sqrt(Math.pow(xSeparation, 2)
				+ Math.pow(ySeparation, 2));
		return totalSeparation;
	}

	// method to figure out proper directions
	public int calcRef(double ca, double c, double size) {
		if ((ca < c && Math.abs(ca - c) < size / 2)
				|| (ca > c && Math.abs(ca - c) > size / 2)) {
			return 1;
		} else if ((ca < c && Math.abs(ca - c) > size / 2)
				|| (ca > c && Math.abs(ca - c) < size / 2)) {
			return -1;
		} else {
			return 0;
		}
	}

	// check collisions
	public void checkCollisions() {
		// creatures vs photons
		synchronized (creature) {
			Iterator<Creature> it = creature.iterator();
			while (it.hasNext()) {
				Creature c = (Creature) it.next();
				if (c.isAlive()) {
					synchronized (grass) {
						Iterator<Grass> itToo = grass.iterator();
						while (itToo.hasNext()) {
							Grass p = (Grass) itToo.next();
							if (p.isAlive()) {
								if (Math.abs(c.getX() - p.getX()) < 10
										&& Math.abs(c.getY() - p.getY()) < 10) {
									if (c.getBounds().intersects(p.getBounds())){
										p.setAlive(false);
										c.incEnergy(p.getEnergy());
										itToo.remove();
									}
								}
							}
						}
					}
				}
			}
		}
		// carnivores vs creatures
		synchronized (carnivore) {
			Iterator<Carnivore> carn = carnivore.iterator();
			while (carn.hasNext()) {
				Carnivore ca = (Carnivore) carn.next();
				if (ca.isAlive()) {
					synchronized (creature) {
						Iterator<Creature> prey = creature.iterator();
						while (prey.hasNext()) {
							Creature cr = (Creature) prey.next();
							if (cr.isAlive()) {
								if (ca.getBounds().intersects(cr.getBounds())) {
									cr.setAlive(false);
									ca.incEnergy(cr.getEnergy());
									prey.remove();
								}
							}
						}
					}
				}
			}
		}
	}

	public void deadCollect() {
		synchronized (creature) {
			Iterator<Creature> it = creature.iterator();
			while (it.hasNext()) {
				Creature c = (Creature) it.next();
				if (!c.isAlive()) {
					it.remove();
				}
			}
		}
		synchronized (carnivore) {
			Iterator<Carnivore> itToo = carnivore.iterator();
			while (itToo.hasNext()) {
				Carnivore ca = (Carnivore) itToo.next();
				if (!ca.isAlive()) {
					itToo.remove();
				}
			}
		}
	}

	public void spawnGrass() {
		double r = ((Math.sin(Math.toRadians(dataNum / 5))) + 3) * 9;
		if (grass.size() < 1500) {
			for (int n = 0; n < r; n++) {
				Grass p = new Grass();
				p.setX(rand.nextInt(width));
				p.setY(rand.nextInt(height));
				p.setAlive(true);
				p.setEnergy(200);
				grass.add(p);
			}
		}
	}

	public void spawnCreatureCheck() {
		for (int n = 0; n < creature.size(); n++) {
			Creature c = creature.get(n);
			if (c.getEnergy() > 250) {
				spawnChild(c);
			}
		}
		for (int n = 0; n < carnivore.size(); n++) {
			Carnivore ca = carnivore.get(n);
			if (ca.getEnergy() > 300) {
				spawnChild(ca);
			}
		}
	}

	public void spawnChild(Creature c_) {

		Creature c = new Creature();
		c.setX(c_.getX() + 1);
		c.setY(c_.getY() + 1);
		c.setAlive(true);
		c.setSpeed(c_.getSpeed());
		c.setEnergy(150);
		c.setSight(c_.getSight());
		c.setShape(c_.getShape());
		c.setlrB(c_.getlrB());
		c.setudB(c_.getudB());
		creature.add(c);
		c_.setEnergy(150);
	}

	public void spawnChild(Carnivore ca_) {
		Carnivore ca = new Carnivore();

		ca.setX(ca_.getX() + 1);
		ca.setY(ca_.getY() + 1);
		ca.setAlive(true);
		ca.setSpeed(ca_.getSpeed());
		ca.setEnergy(150);
		ca.setSight(ca_.getSight());
		ca.setSi(ca_.getSi());
		ca.setShape(ca_.getShape());
		ca.setlrB(ca_.getlrB());
		ca.setudB(ca_.getudB());

		carnivore.add(ca);

		ca_.setEnergy(150);
	}

	// restart
	public void resetCheck() {
		if (creature.size() == 0 && carnivore.size() == 0) {
			init();
		}
	}

	// custom method to get button status
	public void checkButton(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON1:
			mouseButton = 1;
			break;
		case MouseEvent.BUTTON2:
			mouseButton = 2;
			break;
		case MouseEvent.BUTTON3:
			mouseButton = 3;
			break;
		default:
			mouseButton = 0;
		}
	}

	// MouseListener methods
	public void mouseClicked(MouseEvent e) {
		clickX = e.getX();
		clickY = e.getY();
		checkButton(e);
		if (mouseButton == 1) {
			Creature c = new Creature();
			// set up variables
			c.setX(clickX);
			c.setY(clickY - 32);
			c.setAlive(true);
			c.setSpeed(1);
			c.setSi(width);
			c.setEnergy(150);
			creature.add(c);
		} else if (mouseButton == 2) {
			creature.clear();
			carnivore.clear();
		} else if (mouseButton == 3) {
			Carnivore ca = new Carnivore();
			// set up variables
			ca.setX(clickX);
			ca.setY(clickY - 32);
			ca.setAlive(true);
			ca.setSpeed(1);
			ca.setSp(1);
			ca.setEnergy(150);
			ca.setSight(1);
			ca.setSi(20);
			ca.setDebug(true);
			carnivore.add(ca);
		}
	}


	public void keyPressed(KeyEvent k) {
		int keycode = k.getKeyCode();
		switch (keycode) {
		case KeyEvent.VK_P:
			photonDraw = !photonDraw;
			break;
		case KeyEvent.VK_S:
			sightCircle = !sightCircle;
			break;
		}
	}

	public void keyReleased(KeyEvent k) {
		int keycode = k.getKeyCode();
		switch (keycode) {
		}
	}

	public void keyTyped(KeyEvent k) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
