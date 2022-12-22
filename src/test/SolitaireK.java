package test;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.text.ParseException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import fleaMarket.SolitaireFM;
import global.SolitaireMenu;
import global.StatisticAnalysis.Record;



public class SolitaireK
{
	// CONSTANTS
	public static int TABLE_HEIGHT = Card.CardC_HEIGHT * 4;
	public static int TABLE_WIDTH = (Card.CardC_WIDTH * 7) + 100;
	public static final int NUM_FINAL_DECKS = 4;
	public static final int NUM_PLAY_DECKS = 7;
	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	static int width = (int)screenSize.getWidth();
	static int height = (int)screenSize.getHeight();
	public static Point DECK_POS = new Point(5, 5);
			//new Point(width*(5/TABLE_WIDTH), height*(5/TABLE_HEIGHT));
	public static Point SHOW_POS = new Point(DECK_POS.x + Card.CardC_WIDTH + 5, DECK_POS.y);
	public static Point FINAL_POS = new Point(SHOW_POS.x + Card.CardC_WIDTH + 150, DECK_POS.y);
	public static Point PLAY_POS = new Point(DECK_POS.x, FINAL_POS.y + Card.CardC_HEIGHT + 30);

	// GAMEPLAY STRUCTURES
	private static FinalStack[] final_CardCs;// Foundation Stacks
	private static CardStackK[] playCardStack; // Tableau stacks
	private static final Card newCardCPlace = new Card();// waste CardC spot
	private static CardStackK deck; // populated with standard 52 CardC deck

	// GUI COMPONENTS (top level)
	private static final JFrame frame = new JFrame("Klondike Solitaire");
	public static final JPanel table = new JPanel();
	// other components
	public static JEditorPane gameTitle = new JEditorPane("text/html", "");
	private static JButton showRulesButton = new JButton("Show Rules");
	private static JButton newGameButton = new JButton("New Game");
	private static JButton toggleTimerButton = new JButton("Pause Timer");
	public static JTextPane scoreBox = new JTextPane();// displays the score
	public static JTextPane timeBox = new JTextPane();// displays the time
	public static JTextPane statusBox = new JTextPane();// status messages
	private static final Card newCardCButton = new Card();// reveal waste CardC

	// TIMER UTILITIES
	private static Timer timer = new Timer();
	private static ScoreClock scoreClock = new ScoreClock();

	// MISC TRACKING VARIABLES
	private static boolean timeRunning = false;// timer running?
	private static int score = 0;// keep track of the score
	private static int time = 0;// keep track of seconds elapsed
	
	//Action Listener for buttons
	private static ActionListener ae = new setUpButtonListeners();
	private static WindowStateListener WL = new gameListener();
	
	public static class gameListener implements WindowStateListener
	{
		@Override
		public void windowStateChanged(WindowEvent e) {
			
			if (e.getNewState() == frame.MAXIMIZED_BOTH){ //this means maximized
				System.out.println("Event");

		    	TABLE_HEIGHT = frame.getHeight();
		    	TABLE_WIDTH = frame.getWidth();
		    	
		    	Card.CardC_HEIGHT = (int)Math.round(TABLE_HEIGHT*.25);
		    	Card.CardC_WIDTH = (int)Math.round(TABLE_WIDTH*.125);
		    	
		    	frame.setSize(TABLE_WIDTH, TABLE_HEIGHT);
		    	
		    	for (int x = 0; x < NUM_FINAL_DECKS; x++)
				{
					int size = (int)Math.round(TABLE_WIDTH*(.4625) + (x * Card.CardC_WIDTH));
					final_CardCs[x].setXY(size , FINAL_POS.y);
					table.add(final_CardCs[x]);
				}
				// place new CardC distribution button
				table.add(moveCardC(newCardCButton, (int)Math.round(TABLE_WIDTH*(.00625)), (int)Math.round(TABLE_WIDTH*(.00625))));
				if(CardCMovementManager.prevCardC != null) {
					table.add(SolitaireK.moveCardC(CardCMovementManager.prevCardC, (int)Math.round(TABLE_WIDTH*(.1375)), (int)Math.round(TABLE_WIDTH*(.00625))));
					//CardCMovementManager.prevCardC.repaint();
					//table.repaint();
				}
				
				for (int x = 0; x < NUM_PLAY_DECKS; x++)
				{
					int size = (int)Math.round(TABLE_WIDTH*(.00625) + (x * (Card.CardC_WIDTH + TABLE_WIDTH*(.0125))));
					playCardStack[x].setXY(size, (int)Math.round(TABLE_HEIGHT*.30834));
					table.add(playCardStack[x]);
				}

				newGameButton.setBounds(0, TABLE_HEIGHT - 50*(TABLE_HEIGHT/600), 120*(TABLE_WIDTH/800), 30*(TABLE_HEIGHT/600));

				showRulesButton.setBounds((int)Math.round(TABLE_WIDTH*0.15), TABLE_HEIGHT - 50*(TABLE_HEIGHT/600), 120*(TABLE_WIDTH/800), 30*(TABLE_HEIGHT/600));

				gameTitle.setText("<b>Shamari's Solitaire</b> <br> COP3252 <br> Spring 2012");
				gameTitle.setEditable(false);
				gameTitle.setOpaque(false);
				gameTitle.setBounds((int)Math.round(TABLE_WIDTH*0.30625), 20, 100, 100);

				scoreBox.setBounds((int)Math.round(TABLE_WIDTH*0.3), TABLE_HEIGHT - 50*(TABLE_HEIGHT/600), 120*(TABLE_WIDTH/800), 30*(TABLE_HEIGHT/600));
				scoreBox.setText("Score: 0");
				scoreBox.setEditable(false);
				scoreBox.setOpaque(false);

				timeBox.setBounds((int)Math.round(TABLE_WIDTH*0.45), TABLE_HEIGHT - 50*(TABLE_HEIGHT/600), 120*(TABLE_WIDTH/800), 30*(TABLE_HEIGHT/600));
				timeBox.setText("Seconds: 0");
				timeBox.setEditable(false);
				timeBox.setOpaque(false);

				toggleTimerButton.setBounds((int)Math.round(TABLE_WIDTH*0.60), TABLE_HEIGHT - 50*(TABLE_HEIGHT/600), 125*(TABLE_WIDTH/800), 30*(TABLE_HEIGHT/600));

				statusBox.setBounds((int)Math.round(TABLE_WIDTH*0.75625), TABLE_HEIGHT - 50*(TABLE_HEIGHT/600), 180*(TABLE_WIDTH/800), 30*(TABLE_HEIGHT/600));
				statusBox.setEditable(false);
				statusBox.setOpaque(false);

		      } else if (e.getOldState() == frame.MAXIMIZED_BOTH) { //this means minimized state
		    	  	System.out.println("Opposite Event");

		    	  	Card.CardC_HEIGHT = 150;
			    	Card.CardC_WIDTH = 100;
		    	  	
		    	  	TABLE_HEIGHT = Card.CardC_HEIGHT * 4;
			  		TABLE_WIDTH = (Card.CardC_WIDTH * 7) + 100;

			  		frame.setSize(TABLE_WIDTH, TABLE_HEIGHT);
					// initialize & place final (foundation) decks/stacks

					for (int x = 0; x < NUM_FINAL_DECKS; x++)
					{
						final_CardCs[x].setXY(FINAL_POS.x + (x * Card.CardC_WIDTH) + 10, FINAL_POS.y);
						final_CardCs[x].repaint();
						table.add(final_CardCs[x]);
					}
					// place new CardC distribution button
					table.add(moveCardC(newCardCButton, DECK_POS.x, DECK_POS.y));
					if(CardCMovementManager.prevCardC != null) {
						table.add(SolitaireK.moveCardC(CardCMovementManager.prevCardC, SHOW_POS.x, SHOW_POS.y));
					}
					
					// initialize & place play (tableau) decks/stacks
					for (int x = 0; x < NUM_PLAY_DECKS; x++)
					{
						playCardStack[x].setXY((DECK_POS.x + (x * (Card.CardC_WIDTH + 10))), PLAY_POS.y);

						table.add(playCardStack[x]);
					}
					
					newGameButton.setBounds(0, TABLE_HEIGHT - 70, 120, 30);

					showRulesButton.setBounds(120, TABLE_HEIGHT - 70, 120, 30);

					gameTitle.setText("<b>Shamari's Solitaire</b> <br> COP3252 <br> Spring 2012");
					gameTitle.setEditable(false);
					gameTitle.setOpaque(false);
					gameTitle.setBounds(245, 20, 100, 100);

					scoreBox.setBounds(240, TABLE_HEIGHT - 70, 120, 30);
					scoreBox.setText("Score: 0");
					scoreBox.setEditable(false);
					scoreBox.setOpaque(false);

					timeBox.setBounds(360, TABLE_HEIGHT - 70, 120, 30);
					timeBox.setText("Seconds: 0");
					timeBox.setEditable(false);
					timeBox.setOpaque(false);

					toggleTimerButton.setBounds(480, TABLE_HEIGHT - 70, 125, 30);

					statusBox.setBounds(605, TABLE_HEIGHT - 70, 180, 30);
					statusBox.setEditable(false);
					statusBox.setOpaque(false);
		      }
		}
	}
	
	
	// moves a CardC to abs location within a component
	protected static Card moveCardC(Card c, int x, int y)
	{
		c.setBounds(new Rectangle(new Point(x, y), new Dimension(Card.CardC_WIDTH + 10, Card.CardC_HEIGHT + 10)));
		c.setXY(new Point(x, y));
		return c;
	}

	// add/subtract points based on gameplay actions
	protected static void setScore(int deltaScore)
	{
		SolitaireK.score += deltaScore;
		String newScore = "Score: " + SolitaireK.score;
		scoreBox.setText(newScore);
		scoreBox.repaint();
	}

	// GAME TIMER UTILITIES
	protected static void updateTimer()
	{
		SolitaireK.time += 1;
		// every 10 seconds elapsed we take away 2 points
		if (SolitaireK.time % 10 == 0)
		{
			setScore(-2);
		}
		String time = "Seconds: " + SolitaireK.time;
		timeBox.setText(time);
		timeBox.repaint();
	}

	protected static void startTimer()
	{
		scoreClock = new ScoreClock();
		// set the timer to update every second
		timer.scheduleAtFixedRate(scoreClock, 1000, 1000);
		timeRunning = true;
	}
	
	protected static void resetTimer()
	{
		scoreClock.cancel();
		time = 0;
		timeRunning = false;
	}


	// the pause timer button uses this
	protected static void toggleTimer()
	{
		if (timeRunning && scoreClock != null)
		{
			scoreClock.cancel();
			timeRunning = false;
		} else
		{
			startTimer();
		}
	}

	private static class ScoreClock extends TimerTask
	{
		@Override
		public void run()
		{
			updateTimer();
		}
	}

	// BUTTON LISTENER
	private static class setUpButtonListeners implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == newGameButton) {
				frame.setState(JFrame.ICONIFIED);
				frame.setState(JFrame.NORMAL);
				playKNewGame();
			} else if(e.getSource() == toggleTimerButton) {
					toggleTimer();
					if (!timeRunning)
					{
						toggleTimerButton.setText("Start Timer");
					} else
					{
						toggleTimerButton.setText("Pause Timer");
					}
			} else if(e.getSource() == showRulesButton){
					JDialog ruleFrame = new JDialog(frame, true);
					ruleFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					ruleFrame.setSize(TABLE_HEIGHT, TABLE_WIDTH);
					JScrollPane scroll;
					JEditorPane rulesTextPane = new JEditorPane("text/html", "");
					rulesTextPane.setEditable(false);
					String rulesText = "<b>Klondike Solitaire Rules</b>"
							+ "<br><br> (From Wikipedia) Taking a shuffled standard 52-CardC deck of playing CardCs (without Jokers),"
							+ " one upturned CardC is dealt on the left of the playing area, then six downturned CardCs"
							+ " (from left to right).<p> On top of the downturned CardCs, an upturned CardC is dealt on the "
							+ "left-most downturned pile, and downturned CardCs on the rest until all piles have an "
							+ "upturned CardC. The piles should look like the figure to the right.<p>The four foundations "
							+ "(light rectangles in the upper right of the figure) are built up by suit from Ace "
							+ "(low in this game) to King, and the tableau piles can be built down by alternate colors,"
							+ " and partial or complete piles can be moved if they are built down by alternate colors also. "
							+ "Any empty piles can be filled with a King or a pile of CardCs with a King.<p> The point of "
							+ "the game is to build up a stack of CardCs starting with 2 and ending with King, all of "
							+ "the same suit. Once this is accomplished, the goal is to move this to a foundation, "
							+ "where the player has previously placed the Ace of that suit. Once the player has done this, "
							+ "they will have \"finished\" that suit- the goal being, of course, to finish all suits, "
							+ "at which time the player will have won.<br><br><b> Scoring </b><br><br>"
							+ "Moving CardCs directly from the Waste stack to a Foundation awards 10 points. However, "
							+ "if the CardC is first moved to a Tableau, and then to a Foundation, then an extra 5 points "
							+ "are received for a total of 15. Thus in order to receive a maximum score, no CardCs should be moved "
							+ "directly from the Waste to Foundation.<p>	Time can also play a factor in Windows Solitaire, if the Timed game option is selected. For every 10 seconds of play, 2 points are taken away."
							+ "<b><br><br>Notes On My Implementation</b><br><br>"
							+ "Drag CardCs to and from any stack. As long as the move is valid the CardC, or stack of "
							+ "CardCs, will be repositioned in the desired spot. The game follows the standard scoring and time"
							+ " model explained above with only one waste CardC shown at a time."
							+ "<p> The timer starts running as soon as "
							+ "the game begins, but it may be paused by pressing the pause button at the bottom of"
							+ "the screen. ";
					rulesTextPane.setText(rulesText);
					ruleFrame.add(scroll = new JScrollPane(rulesTextPane));

					ruleFrame.setVisible(true);
			}
		}
	}

	/*
	 * This class handles all of the logic of moving the CardC components as well
	 * as the game logic. This determines where CardCs can be moved according to
	 * the rules of Klondike solitiaire
	 */
	private static class CardCMovementManager extends MouseAdapter
	{
		private static Card prevCardC = null;// tracking CardC for waste stack
		private Card movedCardC = null;// CardC moved from waste stack
		private boolean sourceIsFinalDeck = false;
		private boolean putBackOnDeck = true;// used for waste CardC recycling
		private boolean checkForWin = false;// should we check if game is over?
		private boolean gameOver = true;// easier to negate this than affirm it
		private Point start = null;// where mouse was clicked
		private Point stop = null;// where mouse was released
		private Card card = null; // CardC to be moved
		// used for moving single CardCs
		private CardStackK source = null;
		private CardStackK dest = null;
		// used for moving a stack of CardCs
		private CardStackK transferStack = new CardStackK(false);

		private boolean validPlayStackMove(Card source, Card dest)
		{
			int s_val = source.getValue().ordinal();
			int d_val = dest.getValue().ordinal();
			Card.Suit s_suit = source.getSuit();
			Card.Suit d_suit = dest.getSuit();

			// destination CardC should be one higher value
			if ((s_val + 1) == d_val)
			{
				// destination CardC should be opposite color
				switch (s_suit)
				{
				case SPADES:
					if (d_suit != Card.Suit.HEARTS && d_suit != Card.Suit.DIAMONDS)
						return false;
					else
						return true;
				case CLUBS:
					if (d_suit != Card.Suit.HEARTS && d_suit != Card.Suit.DIAMONDS)
						return false;
					else
						return true;
				case HEARTS:
					if (d_suit != Card.Suit.SPADES && d_suit != Card.Suit.CLUBS)
						return false;
					else
						return true;
				case DIAMONDS:
					if (d_suit != Card.Suit.SPADES && d_suit != Card.Suit.CLUBS)
						return false;
					else
						return true;
				}
				return false; // this never gets reached
			} else
				return false;
		}

		private boolean validFinalStackMove(Card source, Card dest)
		{
			int s_val = source.getValue().ordinal();
			int d_val = dest.getValue().ordinal();
			Card.Suit s_suit = source.getSuit();
			Card.Suit d_suit = dest.getSuit();
			if (s_val == (d_val + 1)) // destination must one lower
			{
				if (s_suit == d_suit)
					return true;
				else
					return false;
			} else
				return false;
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			start = e.getPoint();
			boolean stopSearch = false;
			statusBox.setText("");
			transferStack.makeEmpty();

			/*
			 * Here we use transferStack to temporarily hold all the CardCs above
			 * the selected CardC in case player wants to move a stack rather
			 * than a single CardC
			 */
			for (int x = 0; x < NUM_PLAY_DECKS; x++)
			{
				if (stopSearch)
					break;
				source = playCardStack[x];
				// pinpointing exact CardC pressed
				for (Component ca : source.getComponents())
				{
					Card c = (Card) ca;
					if (c.getFaceStatus() && source.contains(start))
					{
						transferStack.putFirst(c);
					}
					if (c.contains(start) && source.contains(start) && c.getFaceStatus())
					{
						card = c;
						stopSearch = true;
						System.out.println("Transfer Size: " + transferStack.showSize());
						break;
					}
				}

			}
			// SHOW (WASTE) CardC OPERATIONS
			// display new show CardC
			if (newCardCButton.contains(start) && deck.showSize() > 0)
			{
				if (putBackOnDeck && prevCardC != null)
				{
					System.out.println("Putting back on show stack: ");
					prevCardC.getValue();
					prevCardC.getSuit();
					deck.putFirst(prevCardC);
				}

				System.out.print("poping deck ");
				deck.showSize();
				if (prevCardC != null)
					table.remove(prevCardC);
				Card c = deck.pop().setFaceup();
				table.add(SolitaireK.moveCardC(c, (int)Math.round(TABLE_WIDTH*(.1375)), (int)Math.round(TABLE_WIDTH*(.00625))));
				c.repaint();
				table.repaint();
				prevCardC = c;
			}

			// preparing to move show CardC
			if (newCardCPlace.contains(start) && prevCardC != null)
			{
				movedCardC = prevCardC;
			}

			// FINAL (FOUNDATION) CardC OPERATIONS
			for (int x = 0; x < NUM_FINAL_DECKS; x++)
			{

				if (final_CardCs[x].contains(start))
				{
					source = final_CardCs[x];
					card = source.getLast();
					transferStack.putFirst(card);
					sourceIsFinalDeck = true;
					break;
				}
			}
			putBackOnDeck = true;

		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			stop = e.getPoint();
			// used for status bar updates
			boolean validMoveMade = false;

			// SHOW CardC MOVEMENTS
			if (movedCardC != null)
			{
				// Moving from SHOW TO PLAY
				for (int x = 0; x < NUM_PLAY_DECKS; x++)
				{
					dest = playCardStack[x];
					// to empty play stack, only kings can go
					if (dest.empty() && movedCardC != null && dest.contains(stop)
							&& movedCardC.getValue() == Card.Value.KING)
					{
						System.out.print("moving new CardC to empty spot ");
						movedCardC.setXY(dest.getXY());
						table.remove(prevCardC);
						dest.putFirst(movedCardC);
						table.repaint();
						movedCardC = null;
						putBackOnDeck = false;
						setScore(5);
						validMoveMade = true;
						break;
					}
					// to populated play stack
					if (movedCardC != null && dest.contains(stop) && !dest.empty() && dest.getFirst().getFaceStatus()
							&& validPlayStackMove(movedCardC, dest.getFirst()))
					{
						System.out.print("moving new CardC ");
						movedCardC.setXY(dest.getFirst().getXY());
						table.remove(prevCardC);
						dest.putFirst(movedCardC);
						table.repaint();
						movedCardC = null;
						putBackOnDeck = false;
						setScore(5);
						validMoveMade = true;
						break;
					}
				}
				// Moving from SHOW TO FINAL
				for (int x = 0; x < NUM_FINAL_DECKS; x++)
				{
					dest = final_CardCs[x];
					// only aces can go first
					if (dest.empty() && dest.contains(stop))
					{
						if (movedCardC.getValue() == Card.Value.ACE)
						{
							dest.push(movedCardC);
							table.remove(prevCardC);
							dest.repaint();
							table.repaint();
							movedCardC = null;
							putBackOnDeck = false;
							setScore(10);
							validMoveMade = true;
							break;
						}
					}
					if (!dest.empty() && dest.contains(stop) && validFinalStackMove(movedCardC, dest.getLast()))
					{
						System.out.println("Destin" + dest.showSize());
						dest.push(movedCardC);
						table.remove(prevCardC);
						dest.repaint();
						table.repaint();
						movedCardC = null;
						putBackOnDeck = false;
						checkForWin = true;
						setScore(10);
						validMoveMade = true;
						break;
					}
				}
			}// END SHOW STACK OPERATIONS

			// PLAY STACK OPERATIONS
			if (card != null && source != null)
			{ // Moving from PLAY TO PLAY
				for (int x = 0; x < NUM_PLAY_DECKS; x++)
				{
					dest = playCardStack[x];
					// MOVING TO POPULATED STACK
					if (card.getFaceStatus() == true && dest.contains(stop) && source != dest && !dest.empty()
							&& validPlayStackMove(card, dest.getFirst()) && transferStack.showSize() == 1)
					{
						Card c = null;
						if (sourceIsFinalDeck)
							c = source.pop();
						else
							c = source.popFirst();

						c.repaint();
						// if playstack, turn next CardC up
						if (source.getFirst() != null)
						{
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.putFirst(c);

						dest.repaint();

						table.repaint();

						System.out.print("Destination ");
						dest.showSize();
						if (sourceIsFinalDeck)
							setScore(15);
						else
							setScore(10);
						validMoveMade = true;
						break;
					} else if (dest.empty() && card.getValue() == Card.Value.KING && transferStack.showSize() == 1)
					{// MOVING TO EMPTY STACK, ONLY KING ALLOWED
						Card c = null;
						if (sourceIsFinalDeck)
							c = source.pop();
						else
							c = source.popFirst();

						c.repaint();
						// if playstack, turn next CardC up
						if (source.getFirst() != null)
						{
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.putFirst(c);

						dest.repaint();

						table.repaint();

						System.out.print("Destination ");
						dest.showSize();
						setScore(5);
						validMoveMade = true;
						break;
					}
					// Moving STACK of CardCs from PLAY TO PLAY
					// to EMPTY STACK
					if (dest.empty() && dest.contains(stop) && !transferStack.empty()
							&& transferStack.getFirst().getValue() == Card.Value.KING)
					{
						System.out.println("King To Empty Stack Transfer");
						while (!transferStack.empty())
						{
							System.out.println("popping from transfer: " + transferStack.getFirst().getValue());
							dest.putFirst(transferStack.popFirst());
							source.popFirst();
						}
						if (source.getFirst() != null)
						{
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.repaint();

						table.repaint();
						setScore(5);
						validMoveMade = true;
						break;
					}
					// to POPULATED STACK
					if (dest.contains(stop) && !transferStack.empty() && source.contains(start)
							&& validPlayStackMove(transferStack.getFirst(), dest.getFirst()))
					{
						System.out.println("Regular Stack Transfer");
						while (!transferStack.empty())
						{
							System.out.println("popping from transfer: " + transferStack.getFirst().getValue());
							dest.putFirst(transferStack.popFirst());
							source.popFirst();
						}
						if (source.getFirst() != null)
						{
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.repaint();

						table.repaint();
						setScore(5);
						validMoveMade = true;
						break;
					}
				}
				// from PLAY TO FINAL
				for (int x = 0; x < NUM_FINAL_DECKS; x++)
				{
					dest = final_CardCs[x];

					if (card.getFaceStatus() == true && source != null && dest.contains(stop) && source != dest)
					{// TO EMPTY STACK
						if (dest.empty())// empty final should only take an ACE
						{
							if (card.getValue() == Card.Value.ACE)
							{
								Card c = source.popFirst();
								c.repaint();
								if (source.getFirst() != null)
								{

									Card temp = source.getFirst().setFaceup();
									temp.repaint();
									source.repaint();
								}

								dest.setXY(dest.getXY().x, dest.getXY().y);
								dest.push(c);

								dest.repaint();

								table.repaint();

								System.out.print("Destination ");
								dest.showSize();
								card = null;
								setScore(10);
								validMoveMade = true;
								break;
							}// TO POPULATED STACK
						} else if (validFinalStackMove(card, dest.getLast()))
						{
							Card c = source.popFirst();
							c.repaint();
							if (source.getFirst() != null)
							{

								Card temp = source.getFirst().setFaceup();
								temp.repaint();
								source.repaint();
							}

							dest.setXY(dest.getXY().x, dest.getXY().y);
							dest.push(c);

							dest.repaint();

							table.repaint();

							System.out.print("Destination ");
							dest.showSize();
							card = null;
							checkForWin = true;
							setScore(10);
							validMoveMade = true;
							break;
						}
					}

				}
			}// end cycle through play decks

			// SHOWING STATUS MESSAGE IF MOVE INVALID
			if (!validMoveMade && dest != null && card != null)
			{
				statusBox.setText("That Is Not A Valid Move");
			}
			// CHECKING FOR WIN
			if (checkForWin)
			{
				boolean gameNotOver = false;
				// cycle through final decks, if they're all full then game over
				for (int x = 0; x < NUM_FINAL_DECKS; x++)
				{
					dest = final_CardCs[x];
					if (dest.showSize() != 13)
					{
						// one deck is not full, so game is not over
						gameNotOver = true;
						break;
					}
				}
				if (!gameNotOver)
					gameOver = true;
			}

			if (checkForWin && gameOver)
			{
				JOptionPane.showMessageDialog(table, "Congratulations! You've Won!");
				statusBox.setText("Game Over!");
			}
			// RESET VARIABLES FOR NEXT EVENT
			start = null;
			stop = null;
			source = null;
			dest = null;
			card = null;
			sourceIsFinalDeck = false;
			checkForWin = false;
			gameOver = false;
		}// end mousePressed()
	}

	private static void playKNewGame()
	{
		newGameButton.removeActionListener(ae);
		
		showRulesButton.removeActionListener(ae);
		
		toggleTimerButton.removeActionListener(ae);
		
		frame.removeWindowStateListener(WL);
		frame.dispose();
		
		deck = new CardStackK(true); // deal 52 CardCs
		deck.shuffle();
		table.removeAll();
		// reset stacks if user starts a new game in the middle of one
		if (playCardStack != null && final_CardCs != null)
		{
			for (int x = 0; x < NUM_PLAY_DECKS; x++)
			{
				playCardStack[x].makeEmpty();
			}
			for (int x = 0; x < NUM_FINAL_DECKS; x++)
			{
				final_CardCs[x].makeEmpty();
			}
		}
		// initialize & place final (foundation) decks/stacks
		final_CardCs = new FinalStack[NUM_FINAL_DECKS];
		for (int x = 0; x < NUM_FINAL_DECKS; x++)
		{
			final_CardCs[x] = new FinalStack();
			int size = (int)Math.round(TABLE_WIDTH*.4625) + (x * Card.CardC_WIDTH);
					//(FINAL_POS.x + (x * Card.CardC_WIDTH)) + 10;
			System.out.println(size);
			final_CardCs[x].setXY(size, FINAL_POS.y);
			table.add(final_CardCs[x]);

		}
		// place new CardC distribution button
		table.add(moveCardC(newCardCButton, DECK_POS.x, DECK_POS.y));
		// initialize & place play (tableau) decks/stacks
		playCardStack = new CardStackK[NUM_PLAY_DECKS];
		for (int x = 0; x < NUM_PLAY_DECKS; x++)
		{
			playCardStack[x] = new CardStackK(false);
			playCardStack[x].setXY((DECK_POS.x + (x * (Card.CardC_WIDTH + 10))), PLAY_POS.y);

			table.add(playCardStack[x]);
		}

		// Dealing new game
		for (int x = 0; x < NUM_PLAY_DECKS; x++)
		{
			int hld = 0;
			Card c = deck.pop().setFaceup();
			playCardStack[x].putFirst(c);

			for (int y = x + 1; y < NUM_PLAY_DECKS; y++)
			{
				playCardStack[y].putFirst(c = deck.pop());
			}
		}

		resetTimer();
		
		newGameButton.addActionListener(ae);
		newGameButton.setBounds(0, TABLE_HEIGHT - 70, 120, 30);

		showRulesButton.addActionListener(ae);
		showRulesButton.setBounds(120, TABLE_HEIGHT - 70, 120, 30);

		gameTitle.setText("<b>Shamari's Solitaire</b> <br> COP3252 <br> Spring 2012");
		gameTitle.setEditable(false);
		gameTitle.setOpaque(false);
		gameTitle.setBounds(245, 20, 100, 100);

		scoreBox.setBounds(240, TABLE_HEIGHT - 70, 120, 30);
		scoreBox.setText("Score: 0");
		scoreBox.setEditable(false);
		scoreBox.setOpaque(false);

		timeBox.setBounds(360, TABLE_HEIGHT - 70, 120, 30);
		timeBox.setText("Seconds: 0");
		timeBox.setEditable(false);
		timeBox.setOpaque(false);

		startTimer();

		toggleTimerButton.setBounds(480, TABLE_HEIGHT - 70, 125, 30);
		toggleTimerButton.addActionListener(ae);

		statusBox.setBounds(605, TABLE_HEIGHT - 70, 180, 30);
		statusBox.setEditable(false);
		statusBox.setOpaque(false);

		table.add(statusBox);
		table.add(toggleTimerButton);
		table.add(gameTitle);
		table.add(timeBox);
		table.add(newGameButton);
		table.add(showRulesButton);
		table.add(scoreBox);
		table.repaint();
		
		frame.addWindowStateListener(WL);
		
		Container contentPane;

		frame.setSize(TABLE_WIDTH, TABLE_HEIGHT);

		table.setLayout(null);
		table.setBackground(SolitaireMenu.getColor());

		contentPane = frame.getContentPane();
		contentPane.add(table);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		table.addMouseListener(new CardCMovementManager());
		table.addMouseMotionListener(new CardCMovementManager());

		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		playKNewGame();
	}
}