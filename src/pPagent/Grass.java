package pPagent;


import java.awt.Rectangle;


public class Grass extends BaseModel {

	
	public Rectangle getBounds() {
		Rectangle r;
		r = new Rectangle((int)getX()-1, (int)getY()-1, 4, 4);
		return r;
	}
	
	
	Grass() {
		setShape(new Rectangle(0, 0, 1, 1));
	}
}
