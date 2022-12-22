package global;

import java.awt.Point;

public class Points extends Point{
	int x;
	int y;
	
	public Points(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Points(Points p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	@Override
	public boolean equals(Object other) {
	    if(this == other)
	        return true;
	    if(!(other instanceof Points))
	        return false;
	    Points otherPoint = (Points)other;
	    if(this.x == (int) otherPoint.getX() && this.y == (int) otherPoint.getY())
	        return true;
	    return false;
	}
	
}
