package pPagent;

import java.awt.*;
import java.awt.geom.*;


public class Predator extends Prey {
	
	private int xRef;
	private int yRef;

	
	public int getXRef() {return xRef;}
	public void setXRef(int xRef) {this.xRef = xRef;}
	public int getYRef() {return yRef;}
	public void setYRef(int yRef) {this.yRef = yRef;}

	
	public void decEnergy(int energy) {this.decEnergy((energy*this.getSpeed())+1);}

	
	private Ellipse2D sightRadius;
	public Ellipse2D getSightRadius() {return sightRadius;}
	public void setSightRadius(Ellipse2D sightRadius) {this.sightRadius = sightRadius;}

	
	Predator() {
		setColor(Color.RED);
		setXRef(1);
		setYRef(1);
		super.setSpeed(2);
		setSightRadius(new Ellipse2D.Double(this.getX(), this.getY(), this.getSi()/2, this.getSi()/2));
	}
}
