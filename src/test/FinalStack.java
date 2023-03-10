package test;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;

/*
 * Adapts the CardCStack to be used as the final
 * (foundation) stack
 */
public class FinalStack extends CardStackK
{
	public FinalStack()
	{
		super(false);
	}

	@Override
	public void setXY(int x, int y)
	{
		_x = x;
		_y = y;
		setBounds(_x, _y, Card.CardC_WIDTH + 10, Card.CardC_HEIGHT + 10);
	}

	@Override
	public boolean contains(Point p)
	{
		Rectangle rect = new Rectangle(_x, _y, Card.CardC_WIDTH + 10, Card.CardC_HEIGHT + 10);
		return (rect.contains(p));
	}

	/*
	 * We draw this stack one CardC on top of the other
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		removeAll();
		if (!empty())
		{
			add(moveCardC(this.getLast(), 1, 1));
		} else
		{
			// draw back of CardC if empty
			Graphics2D g2d = (Graphics2D) g;
			RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, Card.CardC_WIDTH, Card.CardC_HEIGHT,
					Card.CORNER_ANGLE, Card.CORNER_ANGLE);
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fill(rect);
			g2d.setColor(Color.black);
			g2d.draw(rect);
		}

	}
}
