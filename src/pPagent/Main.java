package pPagent;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

import org.math.plot.*;

import javax.swing.*;

public class Main extends JFrame implements Runnable,
		KeyListener {

	// petla glowna
	Thread gameloop;

	BufferedImage backbuffer;
	Graphics2D g2d;

	int width = 900;
	int height = 600;
	
	ArrayList<Double> arrayx;
	ArrayList<Double> arrayy;
	ArrayList<Double> arraytime;
	
	double[] x;
	double[] y;
	double[] time;
	int pl = 0;
	
	static int czcr;
	static int czca;
	
	static int PREYS;
	List<Prey> prey = Collections.synchronizedList(new ArrayList<Prey>());

	static int PREDATORS;
	List<Predator> predator = Collections.synchronizedList(new ArrayList<Predator>());

	static int GRASS;
	List<Grass> grass = Collections.synchronizedList(new ArrayList<Grass>());

	AffineTransform identity = new AffineTransform();
	Random rand = new Random();

	// number of frames past
	int dataNum = 0;

	// MouseListener variables
	int clickX, clickY;
	int mouseButton;

	// font for displaying data
	Font font = new Font("Courier", Font.BOLD, 35);
	

	// toggles
	boolean data = false;
	boolean photonDraw = false;
	

	
	//plot
	Plot2DPanel plot = new Plot2DPanel() {
		public Dimension getPreferredSize() {
            return new Dimension(1000, 800);
        }
	};

	public static void main(String[] args) {
		getConfig();
		new Main();
	}

	// default constructor
	public Main() {
		
		super("PredatorPrey");
		arrayx = new ArrayList<Double>();
		arrayy = new ArrayList<Double>();
		arraytime = new ArrayList<Double>();
		System.out.println("carn" + PREDATORS);
		setSize(900, 632); // 32 is for JFrame top bar
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameloop = new Thread(this);
		gameloop.start();
		init();
	}
	private static Object lock = new Object();
	public static void getConfig(){
		
		//1. Create the frame.
		JFrame frame = new JFrame("Configure init");
		JPanel panel = new JPanel();
		JButton b1 = new JButton("Set config");
		final JTextField f_carnivores = new JTextField("Predator",5);
		final JTextField f_creatures  = new JTextField("Prey",5);
		final JTextField f_timelifecr   = new JTextField("Energy",5);
		final JTextField f_grass      = new JTextField("Grass",5);
		final JTextField f_timelifeca   = new JTextField("Energy",5);
		
		
		b1.addActionListener(new ActionListener()
		{
			  public void actionPerformed(ActionEvent e)
			  {
				  PREYS = Integer.parseInt(f_creatures.getText().toString());
					PREDATORS = Integer.parseInt(f_carnivores.getText().toString());
					GRASS = Integer.parseInt(f_grass.getText().toString());
					czcr = Integer.parseInt(f_timelifecr.getText().toString());
					czca = Integer.parseInt(f_timelifeca.getText().toString());
				  
				  try {
					  synchronized(lock) {
						  lock.notify();
						  
					  }
				  } catch (Exception ex){}
			   
			  }
			});
		
		panel.add(f_carnivores, BorderLayout.AFTER_LAST_LINE);
		panel.add(f_timelifecr,BorderLayout.AFTER_LAST_LINE);
		panel.add(f_creatures, BorderLayout.AFTER_LAST_LINE);
		panel.add(f_timelifeca,BorderLayout.AFTER_LAST_LINE);
		panel.add(f_grass, BorderLayout.AFTER_LAST_LINE);
		
		frame.setPreferredSize(new Dimension(500,300));
		frame.getContentPane().add(panel, BorderLayout.PAGE_END);
		frame.getContentPane().add(b1, BorderLayout.PAGE_START);
		
		
		
		frame.pack();

		
		frame.setVisible(true);
		try {
			synchronized (lock) {
				lock.wait();
				frame.dispose();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block	
		}
		
	}

	
	public void init() {

		for (int n = 0; n < PREYS; n++) {
			Prey c = new Prey();
			
			c.setX(rand.nextInt(906) + 5); 
			c.setY(rand.nextInt(600));
			c.setAlive(true);
			c.setKilling(10);
			c.setEnergy(czca);
			prey.add(c);
		}
		for (int n = 0; n < PREDATORS; n++) {
			Predator c = new Predator();
			
			c.setX(rand.nextInt(906) + 5); 
			c.setY(rand.nextInt(600));
			c.setAlive(true);
			c.setSpeed(1);
			c.setSp(2);
			c.setSi(width / 4);
			c.setEnergy(czcr);
			predator.add(c);
		}
		for (int n = 0; n < GRASS; n++) {
			Grass p = new Grass();
			
			p.setX(rand.nextInt(width));
			p.setY(rand.nextInt(height));
			p.setAlive(true);
			p.setEnergy(200);
			grass.add(p);
		}
		
		
		backbuffer = new BufferedImage(width + 200, height,
				BufferedImage.TYPE_INT_RGB);
		g2d = backbuffer.createGraphics();
		
		
		
		addKeyListener(this);
	}

	// odswiezanie buffera grafiki
	public void paint(Graphics g) {
		
		g.drawImage(backbuffer, 0, 29, this);
		
		g2d.setTransform(identity);
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(900, 0, 0, height);
		
		drawPreys();
		drawPredators();
		// klikniecia 
		if (!photonDraw) {
			drawGrass();
		}
		if(data){
			printData();
		}
	}

	
	public void drawPreys() {
		for (int n = 0; n < prey.size(); n++) {
			Prey c = prey.get(n);
			if (c.isAlive()) {
				
				g2d.setTransform(identity);
				g2d.translate(c.getX(), c.getY());
				g2d.rotate(Math.toRadians(c.getMoveAngle()));
				g2d.setColor(c.getColor());
				g2d.draw(c.getShape());
			}
		}
	}

	
	public void drawPredators() {
		if (predator.size() > 0) {
			for (int n = 0; n < predator.size(); n++) {
				Predator ca = predator.get(n);
				if (ca.isAlive()) {
					
					g2d.setTransform(identity);
					g2d.translate(ca.getX(), ca.getY());
					g2d.rotate(Math.toRadians(ca.getMoveAngle()));
					g2d.setColor(ca.getColor());
					g2d.draw(ca.getShape());
				}
			}
		}
	}

	public void drawGrass() {
		for (int n = 0; n < grass.size(); n++) {
			Grass p = grass.get(n);
			if (p.isAlive()) {
				g2d.setTransform(identity);
				g2d.translate(p.getX(), p.getY());
				g2d.setColor(new Color(0, 100, 0));
				g2d.draw(p.getShape());
			}
		}
	}

	public void printData() {
		g2d.setTransform(identity);
		g2d.translate(15, 10);
		g2d.setColor(new Color(0, 0, 1, 0.5f));
		g2d.setFont(font);
		g2d.setColor(new Color(1, 1, 1, 0.5f));
		Rectangle2D white = new Rectangle2D.Double(0, 0, prey.size()
				* (200 / (prey.size() + predator.size())), 20);
		g2d.fill(white);
		g2d.setColor(new Color(1, 0, 0, 0.5f));
		Rectangle2D red = new Rectangle2D.Double(
				prey.size() * (200 / (prey.size() + predator.size())),
				0,
				predator.size() * (200 / (prey.size() + predator.size())),
				20);
		g2d.fill(red);
		g2d.setColor(new Color(0, 0, 1, 0.5f));
		g2d.setFont(font);
		g2d.translate(0, 30);
		g2d.drawString("Preys: " + prey.size(), 5, 30);
		g2d.drawString("Predators: " + predator.size(), 5, 60);
		g2d.drawString("Grass: " + grass.size(), 5, 90);
		
	}

	public void run() {
		
		Thread t = Thread.currentThread();
		
		while (t == gameloop) {
			try {
				gameUpdate();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}

	
	public void stop() {
		gameloop = null;
	}

	
	public void gameUpdate() {
		
		updatePreys();
		updatePredators();
		checkCollisions();
		deadCollect();
		spawnGrass();
		spawnCheck();
		resetCheck();
		arrayx.add((double) prey.size());
		arrayy.add((double) predator.size());
		arraytime.add((double) dataNum);
		dataNum++;
	}


	public void plotThat(double[] x, double[] y, int i, String name){
		if (i==1) plot.addLinePlot(name, x, y);
		if (i==2) plot.addLinePlot(name, x, y);
		System.out.println("Odpalono mnie");
		
	}
	public void updatePreys() {
		double targetX = 0;
		double targetY = 0;

		Grass g = null;
		int otherwise = 0;

		for (int n = 0; n < prey.size(); n++) {
			Prey c = prey.get(n);
			if (c.isAlive()) {
				double best = c.getSi();
				if (rand.nextInt(4) <= 2 || c.isDebug()) {
					for (int j = 0; j < grass.size(); j++) {
						g = grass.get(j);
						if (Math.abs(g.getX() - c.getX()) <= c.getKilling()
								|| Math.abs(g.getY() - c.getY()) <= c
										.getKilling()) {
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
					c.decEnergy(1 + (0.5 * c.getSpeed()) + (0.1 * c.getKilling()));
				}
				// smierc
				if (c.getEnergy() <= 0) {
					c.setAlive(false);
				}

			}
		}
	}

	public void updatePredators() {
		double targetX = 0;
		double targetY = 0;
		for (int i = 0; i < predator.size(); i++) {
			Predator ca = predator.get(i);
			// nie zawsze poluje
			if (rand.nextInt(8) <= 2) {
				double best = ca.getSi();
				int otherwise = 0;
				for (int j = 0; j < prey.size(); j++) {
					Prey c = prey.get(j);
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
				if (otherwise < prey.size()) {
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

			ca.decEnergy(1 + (0.35 * ca.getSpeed()) + (0.2 * ca.getKilling()));
			if (ca.getEnergy() <= 0) {
				ca.setAlive(false);
			}
		}
	}

	public void randomMove(Predator ca) {
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

	public void randomMove(Prey ca) {
		if (rand.nextInt(31) + 1 < 9 + ca.getudB()) {
			ca.incY(ca.getSp());
		} else if (rand.nextInt(31) + 1 > 12 + ca.getudB()) {
			ca.incY(-ca.getSp());
		} else {
			if (rand.nextInt(2) + 2 <= 2) {
				ca.incX(ca.getSp());
			} else {
				ca.incX(-ca.getSp());
			}
		}
		if (rand.nextInt(31) + 1 < 9 + ca.getlrB()) {
			ca.incY(ca.getSpeed());
		} else if (rand.nextInt(31) + 1 > 12 + ca.getlrB()) {
			ca.incY(-ca.getSpeed());
		} else {
			if (rand.nextInt(2) + 1 <= 1) {
				ca.incX(ca.getSpeed());
			} else {
				ca.incX(-ca.getSpeed());
			}
		}
	}

	// odl miedzy
	public double getDistance(Predator ca, Prey c) {
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

	public double getDistance(Prey ca, Grass c) {
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


	public void checkCollisions() {
	
		synchronized (prey) {
			Iterator<Prey> it = prey.iterator();
			while (it.hasNext()) {
				Prey c = (Prey) it.next();
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
		synchronized (predator) {
			Iterator<Predator> carn = predator.iterator();
			while (carn.hasNext()) {
				Predator ca = (Predator) carn.next();
				if (ca.isAlive()) {
					synchronized (prey) {
						Iterator<Prey> crea = prey.iterator();
						while (crea.hasNext()) {
							Prey cr = (Prey) crea.next();
							if (cr.isAlive()) {
								if (ca.getBounds().intersects(cr.getBounds())) {
									cr.setAlive(false);
									ca.incEnergy(cr.getEnergy());
									crea.remove();
								}
							}
						}
					}
				}
			}
		}
	}

	public void deadCollect() {
		synchronized (prey) {
			Iterator<Prey> it = prey.iterator();
			while (it.hasNext()) {
				Prey c = (Prey) it.next();
				if (!c.isAlive()) {
					it.remove();
				}
			}
		}
		synchronized (predator) {
			Iterator<Predator> itToo = predator.iterator();
			while (itToo.hasNext()) {
				Predator ca = (Predator) itToo.next();
				if (!ca.isAlive()) {
					itToo.remove();
				}
			}
		}
	}

	public void spawnGrass() {
		double r = ((Math.sin(Math.toRadians(dataNum / 5))) + 3) * 9;
		if (grass.size() < 2000) {
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

	public void spawnCheck() {
		for (int n = 0; n < prey.size(); n++) {
			Prey c = prey.get(n);
			if (c.getEnergy() > czca+300) {
				spawnChild(c);
			}
		}
		for (int n = 0; n < predator.size(); n++) {
			Predator ca = predator.get(n);
			if (ca.getEnergy() > czcr+400) {
				spawnChild(ca);
			}
		}
	}

	public void spawnChild(Prey cParent) {

		Prey c = new Prey();
		c.setX(cParent.getX() + 1);
		c.setY(cParent.getY() + 1);
		c.setAlive(true);
		c.setSpeed(cParent.getSpeed());
		c.setEnergy(czca);
		c.setKilling(cParent.getKilling());
		c.setShape(cParent.getShape());
		c.setlrB(cParent.getlrB());
		c.setudB(cParent.getudB());
		prey.add(c);
		cParent.setEnergy(czca);
	}

	public void spawnChild(Predator caParent) {
		Predator ca = new Predator();

		ca.setX(caParent.getX() + 1);
		ca.setY(caParent.getY() + 1);
		ca.setAlive(true);
		ca.setSpeed(caParent.getSpeed());
		ca.setEnergy(czcr);
		ca.setKilling(caParent.getKilling());
		ca.setSi(caParent.getSi());
		ca.setShape(caParent.getShape());
		ca.setlrB(caParent.getlrB());
		ca.setudB(caParent.getudB());

		predator.add(ca);

		caParent.setEnergy(czcr);
	}

	// restart
	public void resetCheck() {
		
		if ((prey.size() == 0 || predator.size() == 0) && pl==0){
			
			x = new double[arrayx.size()];
			y = new double[arrayy.size()];
			time = new double[arraytime.size()];
			
			for( int i=0; i<arrayy.size(); i++){
				x[i] = arrayx.get(i);
				y[i] = arrayy.get(i);
				time[i] = arraytime.get(i);
			}
			
			
			plotThat(time,x,1,"Creature");
			plotThat(time,y,2,"Carnivore");
			pl = 1;
			JFrame frame = new JFrame("Prey predator");
			frame.setContentPane(plot);
			frame.setPreferredSize(new Dimension(1000,700));
			frame.setSize(new Dimension(1000,700));
			frame.setVisible(true);
		}
		
		if (prey.size() == 0 && predator.size() == 0) {
			init();
		}
	}


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

	

	public void keyPressed(KeyEvent k) {
		int keycode = k.getKeyCode();
		switch (keycode) {
		case KeyEvent.VK_P:
			photonDraw = !photonDraw;
			break;
		case KeyEvent.VK_D:
			data = !data;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
