package global;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Card extends JPanel{
	public static enum Value
	{
		ACE("ACE"), TWO("TWO"), THREE("THREE"), FOUR("FOUR"), FIVE("FIVE"), SIX("SIX"), 
		SEVEN("SEVEN"), EIGHT("EIGHT"), NINE("NINE"), TEN("TEN"), JACK("JACK"), QUEEN("QUEEN"), KING("KING");
		
		private String asString;
		
	    Value(String value)
	    { 
	    	this.asString = value; 
	    }
	    
	    public String toString() 
	    { 
	    	return this.asString; 
	    }
	}

	public static enum Suit
	{
		SPADES("SPADES"), CLUBS("CLUBS"), DIAMONDS("DIAMONDS"), HEARTS("HEARTS");
		
		private String asString;
		
	    Suit(String value)
	    { 
	    	this.asString = value; 
	    }
	    
	    public String toString() 
	    { 
	    	return this.asString; 
	    }
	}
	
	public static enum Back
	{
		BLUE, GRAY, GREEN;
	}

	private Suit _suit;

	private Value _value;
	
	public Back _back;

	private Boolean _faceup;

	private Point _location; // location relative to container

	private Point whereAmI; // used to create abs postion rectangle for contains
	// functions

	private int x; // used for relative positioning within CardCStack Container
	private int y;

	private final int x_offset = 15;
	private final int y_offset = 20;
	private final int new_x_offset = x_offset + (CARD_WIDTH - 30);
	
	static public int CARD_HEIGHT = 150;

	static public int CARD_WIDTH = 100;

	final static public int CORNER_ANGLE = 25;

	public Card(Suit suit, Value value)
	{
		_suit = suit;
		_value = value;
		_faceup = false;
		_location = new Point();
		x = 0;
		y = 0;
		_location.x = x;
		_location.y = y;
		whereAmI = new Point();
	}

	public Card()
	{
		_suit = Card.Suit.CLUBS;
		_value = Card.Value.ACE;
		_faceup = false;
		_location = new Point();
		x = 0;
		y = 0;
		_location.x = x;
		_location.y = y;
		whereAmI = new Point();
	}

	public Suit getSuit()
	{
		switch (_suit)
		{
		case HEARTS:
			//System.out.println("Hearts");
			break;
		case DIAMONDS:
			//System.out.println("Diamonds");
			break;
		case SPADES:
			//System.out.println("Spades");
			break;
		case CLUBS:
			//System.out.println("Clubs");
			break;
		}
		return _suit;
	}

	public Value getValue()
	{
		switch (_value)
		{
		case ACE:
			//System.out.println(" Ace");
			break;
		case TWO:
			//System.out.println(" 2");
			break;
		case THREE:
			//System.out.println(" 3");
			break;
		case FOUR:
			//System.out.println(" 4");
			break;
		case FIVE:
			//System.out.println(" 5");
			break;
		case SIX:
			//System.out.println(" 6");
			break;
		case SEVEN:
			//System.out.println(" 7");
			break;
		case EIGHT:
			//System.out.println(" 8");
			break;
		case NINE:
			//System.out.println(" 9");
			break;
		case TEN:
			//System.out.println(" 10");
			break;
		case JACK:
			//System.out.println(" Jack");
			break;
		case QUEEN:
			//System.out.println(" Queen");
			break;
		case KING:
			//System.out.println(" King");
			break;
		}
		return _value;
	}

	public void setWhereAmI(Point p)
	{
		whereAmI = p;
	}

	public Point getWhereAmI()
	{
		return whereAmI;
	}

	public Point getXY()
	{
		return new Point(x, y);
	}

	public Boolean getFaceStatus()
	{
		return _faceup;
	}

	public void setXY(Point p)
	{
		x = p.x;
		y = p.y;

	}

	public void setSuit(Suit suit)
	{
		_suit = suit;
	}

	public void setValue(Value value)
	{
		_value = value;
	}

	public Card setFaceup()
	{
		_faceup = true;
		return this;
	}

	public Card setFacedown()
	{
		_faceup = false;
		return this;
	}
	@Override
	public boolean contains(Point p)
	{
		Rectangle rect = new Rectangle(whereAmI.x, whereAmI.y, Card.CARD_WIDTH, Card.CARD_HEIGHT);
		return (rect.contains(p));
	}

	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;

		String suit = "";
		String value = "";
		String color = "";
		URL imageP = null;
		Image img;
		// DRAW THE CardC SUIT AND VALUE IF FACEUP
		try {
			if (_faceup)
			{
				switch (_suit)
				{
				case HEARTS:
					suit = "H";
					break;
				case DIAMONDS:
					suit = "D";
					break;
				case SPADES:
					suit = "S";
					break;
				case CLUBS:
					suit = "C";
					break;
				}
				
				switch (_value)
				{
				case ACE:
					value = "A";
					break;
				case TWO:
					value = "2";
					break;
				case THREE:
					value = "3";
					break;
				case FOUR:
					value = "4";
					break;
				case FIVE:
					value = "5";
					break;
				case SIX:
					value = "6";
					break;
				case SEVEN:
					value = "7";
					break;
				case EIGHT:
					value = "8";
					break;
				case NINE:
					value = "9";
					break;
				case TEN:
					value = "10";
					break;
				case JACK:
					value = "J";
					break;
				case QUEEN:
					value = "Q";
					break;
				case KING:
					value = "K";
					break;
				}
			
				imageP = Card.class.getClassLoader().getResource(value+""+suit+".png");
				img = ImageIO.read(imageP);
				g.drawImage(img, _location.x, _location.y, CARD_WIDTH , CARD_HEIGHT, null);
			
			
			} else
			{
				// DRAW THE BACK OF THE CardC IF FACEDOWN
				if(_back != null)
				{
					switch(_back)
					{
					case BLUE:
						color = "blue";
						break;
					case GRAY:
						color = "gray";
						break;
					case GREEN:
						color = "green";
						break;
					}
					imageP = Card.class.getClassLoader().getResource(color+"_back.png");
					img = ImageIO.read(imageP);
					g.drawImage(img, _location.x, _location.y, CARD_WIDTH , CARD_HEIGHT, null);
				} else {
					RoundRectangle2D rect = new RoundRectangle2D.Double(_location.x, _location.y, CARD_WIDTH, CARD_HEIGHT,
							CORNER_ANGLE, CORNER_ANGLE);
					g2d.setColor(Color.LIGHT_GRAY);
					g2d.fill(rect);
					g2d.setColor(Color.black);
					g2d.draw(rect);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
