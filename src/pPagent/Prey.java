package pPagent;

import java.awt.*;


public class Prey extends BaseModel {

	private int xRef;
	private int yRef;
	
	private Color color = Color.WHITE;
	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}

	
	private int[] crX = {-2, 0, 2, 2, 2, 0, -2, -2};
	private int[] crY = {2, 2, 2, 0, -2, -2, -2, 0};
	public int[] getCrX() {return crX;}
	public int[] getCrY() {return crY;}
	public void setCrX(int[] crX) {this.crX = crX;}
	public void setCrY(int[] crY) {this.crY = crY;}

	//zas widzenia polowanie
	private double sight;
	private double si;
	public double getSight() {return sight;}
	public double getSi() {return si;}
	public void setSight(double sight) {this.sight = sight;}
	public void setSi(double si) {this.si = si;}
	public void incSight(double sight) {this.sight += sight;}
	public void incSi(double si) {this.si += si;}

	
	
	public int getXRef() {return xRef;}
	public void setXRef(int xRef) {this.xRef = xRef;}
	public int getYRef() {return yRef;}
	public void setYRef(int yRef) {this.yRef = yRef;}
	
	
	private double lrB;
	private double udB;
	public double getlrB() {return lrB;}
	public double getudB() {return udB;}
	public void setlrB(double bias) {this.lrB = bias;}
	public void setudB(double bias) {this.udB = bias;}
	public void inclrB(double bias) {this.lrB += bias;}
	public void incudB(double bias) {this.udB += bias;}


	
	private double sp;
	public double getSp() {return sp;}
	public void setSp(double sp) {this.sp = sp;}
	public void incSp(double sp) {this.sp += sp;}

	
	protected double rotVel;
	public double getRotationVelocity() {return rotVel;}
	public void setRotationVelocity(double v) {rotVel = v;}

	
	public Rectangle getBounds() {
		Rectangle r;
		r = new Rectangle((int)getX()-1, (int)getY()-1, 4, 4);
		return r;
	}
	
	
	Prey() {
		
		
		setShape(new Polygon(crX, crY, crX.length));
		setRotationVelocity(0.0);
		
		setSight(1);
		setSi(2);
		setXRef(1);
		setYRef(1);
		setlrB(2);
		setudB(2);
		setSpeed(1);
		setSp(1);
		
	}
}
