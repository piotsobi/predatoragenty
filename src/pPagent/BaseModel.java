package pPagent;

import java.awt.Shape;

public class BaseModel {


	private Shape shape;
	private boolean alive;
	private boolean debug;
	private double speed;
	private double energy;
	private double x, y;
	private double velX, velY;
	private double moveAngle, faceAngle;



	public Shape getShape() {return shape;}
	public boolean isAlive() {return alive;}
	public boolean isDebug() {return debug;}
	public double getSpeed() {return speed;}
	public double getEnergy() {return energy;}
	public double getX() {return x;}
	public double getY() {return y;}
	public double getVelX() {return velX;}
	public double getVelY() {return velY;}
	public double getMoveAngle() {return moveAngle;}
	public double getFaceAngle() {return faceAngle;}



	public void setShape(Shape shape) {this.shape = shape;}
	public void setAlive(boolean alive) {this.alive = alive;}
	public void setDebug(boolean debug) {this.debug = debug;}
	public void setSpeed(double speed) {this.speed = speed;}
	public void incSpeed(double speed) {this.speed += speed;}
	public void setEnergy(double energy) {this.energy = energy;}
	public void incEnergy(double energy) {this.energy += energy;}
	public void decEnergy(double energy) {this.energy -= energy*this.speed;}
	public void setX(double x) {this.x = x;}
	public void incX(double i) {this.x += i;}
	public void setY(double y) {this.y = y;}
	public void incY(double i) {this.y += i;}
	public void setVelX(double velX) {this.velX = velX;}
	public void incVelX(double i) {this.velX += i;}
	public void setVelY(double velY) {this.velY = velY;}
	public void incVelY(double i) {this.velY += i;}
	public void setFaceAngle(double angle) {this.faceAngle = angle;}
	public void incFaceAngle(double i) {this.faceAngle += i;}
	public void setMoveAngle(double angle) {this.moveAngle = angle;}
	public void	incMoveAngle(double i) {this.moveAngle += i;}

	BaseModel() {
		setShape(null);
		setAlive(false);
		setDebug(false);
		setSpeed(0);
		setEnergy(0);
		setX(0.0);
		setY(0.0);
		setVelX(0.0);
		setVelY(0.0);
		setMoveAngle(0.0);
		setFaceAngle(0.0);
	}
}
