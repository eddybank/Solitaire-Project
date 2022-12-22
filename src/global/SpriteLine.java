package global;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JPanel;

public class SpriteLine extends JPanel{
	
	private AnimationP a;
	private int x;
	private int y;
	//private ArrayList<Points> p;
	
	//Constructor
	public SpriteLine(AnimationP a) {
		this.a = a;
	}
	
	//change position
	public void update(long timePassed, ArrayList<Points> pt) {
		//System.out.println("x: "+x+" - y: "+y);
		Points ad = new Points(x, y);
		int index = pt.indexOf(ad);
		//System.out.println("index: "+index+", b: "+b);
		boolean next = pt.listIterator(index).hasNext();
		
		//System.out.println("next: "+next+", index: "+index);
		if(next && (index+1) < pt.size()) {
			//System.out.println("next.x: "+pt.listIterator(index+1).next().x+", next.y: "+pt.listIterator(index+1).next().y);
			x = pt.listIterator(index+1).next().x;
			y = pt.listIterator(index+1).next().y;
		}
		a.update(timePassed);
	}
	
	//get x pos
	public int getX() {
		return x;
	}
	
	//get y pos
	public int getY() {
		return y;
	}
	
	//get point
	public Points getPoint() {
		return new Points(x,y);
	}
	
	//set sprite x position
	public void setX(int x) {
		this.x = x;
	}
	
	//set sprite x position
	public void setY(int y) {
		this.y = y;
	}
	
	//get sprite width
	public int getWidth() {
		return a.getImage().getWidth(null);
	}
	
	//get sprite height
	public int getHeight() {
		return a.getImage().getHeight(null);
	}
	
	//get sprite / image
	public Image getImage() {
		return a.getImage();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		System.out.println("sprite loc: ("+this.getX()+", "+this.getY()+")");
		g2D.drawImage(getImage(), this.getX(), this.getY(), 100, 150, null);
		//repaint();
	}
}