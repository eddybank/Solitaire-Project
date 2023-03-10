package fleaMarket;

import global.Card;
import global.CardStack;
import global.FinalStack;
import global.SimpleAudioPlayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SolitaireFM
{
	// CONSTANTS
	public int table_height = (Card.CARD_HEIGHT * (8)); //1200
	public int table_width = (Card.CARD_WIDTH * 8); //800
	public final int NUM_FINAL_DECKS = 8;
	public final int NUM_PLAY_DECKS = 13;
	public final Point DECK_POS = new Point(5, 5);
	public final Point SHOW_POS = new Point(DECK_POS.x + Card.CARD_WIDTH + 5, DECK_POS.y);
	public final Point FINAL_POS = new Point(SHOW_POS.x + Card.CARD_WIDTH + 150, DECK_POS.y);
	public final Point PLAY_POS = new Point(DECK_POS.x, FINAL_POS.y + Card.CARD_HEIGHT + 30);

	// GAMEPLAY STRUCTURES
	private FinalStack[] final_cards;// Foundation Stacks
	private CardStack[] playCardStack; // Tableau stacks
	private CardStack deck; // populated with 2 standard 52 card decks
	private FinalStack[] deal_deck;// Foundation Stacks

	// GUI COMPONENTS (top level)
	private final JFrame frame = new JFrame("Flea Market Solitaire");
	private final JPanel table = new JPanel();

	// other components
	private JEditorPane gameTitle = new JEditorPane("text/html", "");
	private JButton showRulesButton = new JButton("Show Rules");
	private JButton newGameButton = new JButton("New Game");
	private JButton toggleTimerButton = new JButton("Pause Timer");
	private JButton autoPlayButton = new JButton("Auto Play");
	private JTextPane scoreBox = new JTextPane();// displays the score
	private JTextPane timeBox = new JTextPane();// displays the time
	private JTextPane statusBox = new JTextPane();// status messages
	private JScrollPane scroll;
	
	//ACTION LISTENERS
	private ActionListener ae = new setUpButtonListeners();
	private ActionListener menuNewGameListener;
	private CardMovementManager cm = new CardMovementManager();
	private WindowStateListener wSL = new resizeListener();

	// TIMER UTILITIES
	private Timer timer = new Timer();
	private ScoreClock scoreClock = new ScoreClock();

	// MISC TRACKING VARIABLES
	private boolean timeRunning = false;// timer running?
	protected int score = -64;// keep track of the score, start of $-64
	protected int time = 0;// keep track of seconds elapsed
	private int deal_deck_pos = 0;
	protected boolean gameOver = true;
	
	// COLOR
	private Color bgColor = null;
	private Style style = this.statusBox.addStyle("bgColor", null);
	private StyledDocument scoreDoc = this.scoreBox.getStyledDocument();
	private StyledDocument timeDoc = this.timeBox.getStyledDocument();
	private StyledDocument statusDoc = this.statusBox.getStyledDocument();
	
	//SOUND PLAYERS
	private boolean soundState = false;
	private URL filePath = SolitaireFM.class.getClassLoader().getResource("dealing_card.wav");
	private URL filePath2 = SolitaireFM.class.getClassLoader().getResource("card_contact.wav");
	private URL filePath3 = SolitaireFM.class.getClassLoader().getResource("deal_cards_f.wav");
	private SimpleAudioPlayer card_contact_ap;
	private SimpleAudioPlayer deal_card_ap;
	
	private SimpleAudioPlayer ap(URL fp, int loops) {
		try {
			SimpleAudioPlayer audioPlayer = new SimpleAudioPlayer(fp, loops);
			return audioPlayer;
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
			System.out.println("Error occurred--Printing stack trace");
			e1.printStackTrace();
		}
		return null;
	}
	
	// SETTERS
	
	// add/subtract points based on gameplay actions
	protected void setScore(int deltaScore) {
		score += deltaScore;
		String newScore = "Score: " + score;
		scoreBox.setText(newScore);
		scoreBox.repaint();
	}
	public void setColor(Color c) {
		bgColor = c;
		updateForColorChange(c);
	}
	
	public void setSoundState(boolean b) {
		soundState = b;
	}
	
	public void setMenuNewGameListener(ActionListener ae) {
		menuNewGameListener = ae;
	}
	
	// GETTERS
	public JPanel getTable() {
		return table;
	}
	
	public JTextPane getScoreBox() {
		return scoreBox;
	}
	
	public JTextPane getTimeBox() {
		return timeBox;
	}
	
	public JTextPane getStatusBox() {
		return statusBox;
	}
	
	public JEditorPane getGameTitle() {
		return gameTitle;
	}
	
	public JButton getNewGameButton() {
		return newGameButton;
	}
	
	public int getTime() {
		return time;
	}
	
	// COLOR UPDATER
	private void updateForColorChange(Color c) {
		String scoreText = this.scoreBox.getText();
		String timeText = this.timeBox.getText();
		String statusText = this.statusBox.getText();
		
		toggleTimer();
		
		try {
			if(c == Color.GRAY || c == Color.DARK_GRAY || c == new Color(0, 180, 0) || c == Color.BLUE) {
				this.scoreBox.setText("");
				this.timeBox.setText("");
				this.statusBox.setText("");
				this.gameTitle.setText("");
				
				StyleConstants.setForeground(style, Color.WHITE);

		        scoreDoc.insertString(scoreDoc.getLength(), scoreText, style);
		        timeDoc.insertString(timeDoc.getLength(), timeText, style);
		        statusDoc.insertString(statusDoc.getLength(), statusText, style);
		        gameTitle.setText("<span style =\"color:white\"><b>Team 1<br>Flea Market Solitaire</b> <br> CPSC 4900 <br> Fall 2021</span>");
		        
			} else if(c == Color.WHITE || c == Color.PINK || c == Color.ORANGE || 
					c == Color.CYAN || c == Color.YELLOW || c == Color.RED) {
				this.scoreBox.setText("");
				this.timeBox.setText("");
				this.statusBox.setText("");
				this.gameTitle.setText("");
				
				StyleConstants.setForeground(style, Color.BLACK);
				
				scoreDoc.insertString(scoreDoc.getLength(), scoreText, style);
		        timeDoc.insertString(timeDoc.getLength(), timeText, style);
		        statusDoc.insertString(statusDoc.getLength(), statusText, style);
		        gameTitle.setText("<span style =\"color:black\"><b>Team 1<br>Flea Market Solitaire</b> <br> CPSC 4900 <br> Fall 2021</span>");

			}
			table.setBackground(c);
		} catch (BadLocationException e1) {
			System.out.println("Error occurred - Printing stack trace");
			e1.printStackTrace();
		}
		toggleTimer();
	}
	
	// GAME TIMER UTILITIES
	
	protected void updateTimer() {
		time += 1;
		// every 10 seconds elapsed we take away 2 points
		/*if (SolitaireFM.time % 10 == 0)
		{
			setScore(-2);
		}*/
		String time = "Seconds: " + getTime();
		timeBox.setText(time);
		timeBox.repaint();
	}
	
	//start timer
	protected void startTimer() {
		scoreClock = new ScoreClock();
		// set the timer to update every second
		timer.scheduleAtFixedRate(scoreClock, 1000, 1000);
		timeRunning = true;
	}
	
	
	//reset timer
	protected void resetTimer() {
		scoreClock.cancel();
		time = 0;
		timeRunning = false;
	}
	

	// the pause timer button uses this
	public void toggleTimer() {
		if (timeRunning && scoreClock != null) {
			scoreClock.cancel();
			timeRunning = false;
		} else {
			startTimer();
		}
	}
	

	private class ScoreClock extends TimerTask {
		@Override
		public void run() {
			updateTimer();
		}
	}
	
	/*
	 * Window state listener class to facilitate window resizing
	 */
	public class resizeListener implements WindowStateListener {
		@Override
		public void windowStateChanged(WindowEvent e) {
			
			if (e.getNewState() == Frame.MAXIMIZED_BOTH){ //this means maximized
				
				table_height = frame.getHeight();
		    	table_width = frame.getWidth();
		    	
		    	Card.CARD_HEIGHT = (int)Math.round(table_height*.125);
		    	Card.CARD_WIDTH = (int)Math.round(table_width*.125);
		    	
		    	frame.setSize(table_width, table_height);
		    	
				for (int x = 0; x < NUM_FINAL_DECKS; x++) {
	    			//Bottom Up: A to K
	    			if(x >= 0 && x <= 3) {
	    				final_cards[x].setXY((int)Math.round(table_width*0.25) + (x * (Card.CARD_WIDTH)), (int)Math.round(table_height*0.0041667));
	    			}
	    			else if(x >= 4 && x <= 7) {
	    				final_cards[x].setXY((int)Math.round(table_width*0.25) + ((x-4) * (Card.CARD_WIDTH)), (int)Math.round((table_height*0.1354167)));
	    			}
	    		}
	    		
	    		for (int x = 0; x < NUM_PLAY_DECKS; x++) {
	    			playCardStack[x].setXY((int)Math.round(table_width*0.4375), (2*(int)Math.round(table_height*0.154167)));
	    			
	    			if(x >= 1 && x <= 4) {
	    				playCardStack[x].setXY((int)Math.round(table_width*0.078125) + (x * (Card.CARD_WIDTH + (int)Math.round(table_width*0.01875))), (3*(int)Math.round(table_height*0.154167)));
	    			} 
	    			if(x >= 5 && x <= 8) {
	    				playCardStack[x].setXY((int)Math.round(table_width*0.078125) + ((x-4) * (Card.CARD_WIDTH + (int)Math.round(table_width*0.01875))), (4*(int)Math.round(table_height*0.154167)));
	    			} if(x >= 9 && x <= 13) {
	    				playCardStack[x].setXY((int)Math.round(table_width*0.078125) + ((x-8) * (Card.CARD_WIDTH + (int)Math.round(table_width*0.01875))), (5*(int)Math.round(table_height*0.154167)));
	    			}
	    		}
	    		
	    		
	    		for (int x = 0; x < deal_deck.length; x++) {
	    			deal_deck[x].setXY((int)Math.round(table_width*0.0625) - ((int)Math.round(table_width*0.00625) + ((int)Math.round(table_width*0.00375)*x)) + (int)Math.round(table_width*0.00982143), (2*(int)Math.round(table_height*0.154167)));
	    		}
	    		
	    		autoPlayButton.setBounds(0, table_height - 160*(table_height/1200), 120*(table_width/800), 60*(table_height/1200));

				newGameButton.setBounds(0, table_height - 100*(table_height/1200), 120*(table_width/800), 60*(table_height/1200));

				showRulesButton.setBounds((int)Math.round(table_width*0.15), table_height - 100*(table_height/1200), 120*(table_width/800), 60*(table_height/1200));

				gameTitle.setText("<b>Team 1<br>Flea Market Solitaire</b> <br> CPSC 4900 <br> Fall 2021");
				gameTitle.setEditable(false);
				gameTitle.setOpaque(false);
				gameTitle.setBounds((int)Math.round(table_width*0.04), 20, 100, 100);

				scoreBox.setBounds((int)Math.round(table_width*0.3), table_height - 100*(table_height/1200), 120*(table_width/800), 60*(table_height/1200));
				scoreBox.setEditable(false);
				scoreBox.setOpaque(false);

				timeBox.setBounds((int)Math.round(table_width*0.45), table_height - 100*(table_height/1200), 120*(table_width/800), 60*(table_height/1200));
				timeBox.setEditable(false);
				timeBox.setOpaque(false);

				toggleTimerButton.setBounds((int)Math.round(table_width*0.60), table_height - 100*(table_height/1200), 125*(table_width/800), 60*(table_height/1200));

				statusBox.setBounds((int)Math.round(table_width*0.75625), table_height - 100*(table_height/1200), 180*(table_width/800), 60*(table_height/1200));
				statusBox.setEditable(false);
				statusBox.setOpaque(false);
	
				
		      } else if (e.getOldState() == Frame.MAXIMIZED_BOTH) { //this means minimized state
		    	  	
		    	  	Card.CARD_HEIGHT = 150;
			    	Card.CARD_WIDTH = 100;
		    	  	
					table_height = Card.CARD_HEIGHT * 8;
			  		table_width = (Card.CARD_WIDTH * 8);
			    	
			  		frame.setSize(table_width-50, table_height+10);
			  		
		    	  	for (int x = 0; x < NUM_FINAL_DECKS; x++) {
		    			//Bottom Up: A to K
		    			if(x >= 0 && x <= 3) {
		    				final_cards[x].setXY(200 + (x * (Card.CARD_WIDTH)), (FINAL_POS.y));
		    			}
		    			else if(x >= 4 && x <= 7) {
		    				final_cards[x].setXY(200 + ((x-4) * (Card.CARD_WIDTH)), ((65/2)*FINAL_POS.y));
		    			}
		    		}
		    		
		    		for (int x = 0; x < NUM_PLAY_DECKS; x++) {
		    			playCardStack[x].setXY(350, (2*PLAY_POS.y));
		    			
		    			if(x >= 1 && x <= 4) {
		    				playCardStack[x].setXY((125/2) + (x * (Card.CARD_WIDTH + 15)), (3*PLAY_POS.y));
		    			} 
		    			if(x >= 5 && x <= 8) {
		    				playCardStack[x].setXY((125/2) + ((x-4) * (Card.CARD_WIDTH + 15)), (4*PLAY_POS.y));
		    			} if(x >= 9 && x <= 13) {
		    				playCardStack[x].setXY((125/2) + ((x-8) * (Card.CARD_WIDTH + 15)), (5*PLAY_POS.y));
		    			}
		    		}
		    		
		    		
		    		for (int x = 0; x < deal_deck.length; x++) {
		    			deal_deck[x].setXY(50 - (5 + (3*x)) + ((110/2)/7), (2*PLAY_POS.y));
		    		}
		    		
		    		autoPlayButton.setBounds(0, table_height - 100, 120, 30);
		    		
		    		newGameButton.setBounds(0, table_height - 70, 120, 30);

		    		showRulesButton.setBounds(120, table_height - 70, 120, 30);

		    		gameTitle.setText("<b>Team 1<br>Flea Market Solitaire</b> <br> CPSC 4900 <br> Fall 2021");
		    		gameTitle.setEditable(false);
		    		gameTitle.setOpaque(false);
		    		gameTitle.setBounds(32, 20, 100, 100);

		    		scoreBox.setBounds(240, table_height - 70, 120, 30);
		    		scoreBox.setEditable(false);
		    		scoreBox.setOpaque(false);

		    		timeBox.setBounds(360, table_height - 70, 120, 30);
		    		timeBox.setEditable(false);
		    		timeBox.setOpaque(false);

		    		toggleTimerButton.setBounds(480, table_height - 70, 125, 30);

		    		statusBox.setBounds(605, table_height - 70, 180, 30);
		    		statusBox.setEditable(false);
		    		statusBox.setOpaque(false);
		      }
		}
	}
	

	// BUTTON LISTENER
	private class setUpButtonListeners implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == toggleTimerButton) {
					toggleTimer();
					if (!timeRunning) {
						toggleTimerButton.setText("Start Timer");
					} else {
						toggleTimerButton.setText("Pause Timer");
					}
			} else if(e.getSource() == showRulesButton) {
					JDialog ruleFrame = new JDialog(frame, true);
					ruleFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					ruleFrame.setSize(table_height - (table_height/3), table_width - (table_width/3));
					JEditorPane rulesTextPane = new JEditorPane("text/html", "");
					rulesTextPane.setEditable(false);
					String rulesText = "<b>Flea Market Solitaire</b>"
							+ "<b><br><br>Notes On My Implementation</b><br>"
							+ "Drag cards to and from any stack. As long as the move is valid the card, or stack of "
							+ "cards, will be repositioned in the desired spot. The game starts with a score of $-64 and each move will result in 5 points."
							+ "The timer starts running as soon as "
							+ "the game begins, but it may be paused by pressing the pause button at the bottom of "
							+ "the screen. "
							+ "<b><br><br>Technical Aspects</b><br>"
							+ "This solitaire uses 104 cards (2 decks). You have 12 main tableau piles with one card per pile, one additional tableau piles with one card and 8 foundations."
							+ "At the start of the game an Ace and King of each suit is dealt on the foundations."
							+ "<b><br><br>The object of the game</b><br>"
							+ "To build the foundation Aces up in suit to Kings, to build the foundation Kings down in suit to Aces."
							+ "<b><br><br>The rules</b><br>"
							+ "The top cards of tableau piles are available for play on foundations. You may build tableau piles up or down by suit. Only one card at a time can be moved from pile to pile. Spaces are filled automatically from the stock pile. "
							+ "When you have made all the moves initially available, click on the stock to deal one card on each main tableau pile. "
							+ "When the stock pile is empty the spaces may be filled with the top cards of tableaus. "
							+ "There is no redeal. ";
					rulesTextPane.setText(rulesText);
					ruleFrame.add(new JScrollPane(rulesTextPane));

					ruleFrame.setVisible(true);
			} else if(e.getSource() == autoPlayButton) {
				autoPlay();
			}
		}
	} 
	
	// moves a card to abs location within a component
	protected Card moveCard(Card c, int x, int y) {
		c.setBounds(new Rectangle(new Point(x, y), new Dimension(Card.CARD_WIDTH + 10, Card.CARD_HEIGHT + 10)));
		c.setXY(new Point(x, y));
		return c;
	}
	
	/*
	 * 
	 * This class handles all of the logic of moving the Card components as well
	 * as the game logic. This determines where Cards can be moved according to
	 * the rules of Flea Market solitaire.
	 * 
	 */
	private class CardMovementManager extends MouseAdapter {
		private boolean checkForWin = false;// should we check if game is over?
		private Point start = null;// where mouse was clicked
		private Point stop = null;// where mouse was released
		private Card card = null; // card to be moved
		
		// used for moving single cards
		private CardStack source = null;
		private CardStack dest = null;
		
		// used for moving a stack of cards
		private CardStack transferStack = new CardStack(false);
		
		public boolean validStackMove(Card source, Card dest) {
			if(dest != null) {
				int s_val = source.getValue().ordinal();
				int d_val = dest.getValue().ordinal();
				Card.Suit s_suit = source.getSuit();
				Card.Suit d_suit = dest.getSuit();
	
				// destination card should be one higher value
				if (s_val  == (d_val + 1)) {
					if (s_suit == d_suit)
						return true;
					else
						return false;
				} else if((s_val + 1) == d_val) {
					if (s_suit == d_suit)
						return true;
					else
						return false;
					}
				} else
					return true;
			return false; //this never gets reached
		}
		
		
		//Deal a card to an empty tableau whilst there still is cards on the deck
		public void dealSingleCard() {
			if (deck.showSize() > 0) {
				for (int x = 0; x < NUM_PLAY_DECKS; x++) {
					if(playCardStack[x].empty()) {
						System.out.print("poping deck ");
						deck.showSize();
						Card c = deck.pop().setFaceup();
						System.out.println("sound: "+soundState);
						if(soundState) {
							deal_card_ap = ap(filePath, 0);
							deal_card_ap.play();
						}
						playCardStack[x].putFirst(c);
						c.repaint();
						statusBox.setText("Card dealt to tableau "+(x+1));
						table.repaint();
					}
				}
			}
		}
		
		
		@Override
		public void mousePressed(MouseEvent e) {
			start = e.getPoint();
			boolean stopSearch = false;
			statusBox.setText("");
			transferStack.makeEmpty();
			
			/*
			 * Here we use transferStack to temporarily hold the one card that
			 * was clicked on by the user for transfer
			 */
			for (int x = 0; x < NUM_PLAY_DECKS; x++) {
				if (stopSearch)
					break;
				source = playCardStack[x];
				// pinpointing exact card pressed

				if(!source.empty()) {
					Card c = (Card) source.getFirst();
					if (c.contains(start) && source.contains(start)) {
						transferStack.putFirst(c);
					}
					if (c.contains(start) && source.contains(start) && c.getFaceStatus()) {
						card = c;
						stopSearch = true;
						System.out.println("Transfer Size: " + transferStack.showSize());
						break;
					}
				}
			}
		}

		
		@Override
		public void mouseReleased(MouseEvent e) {
			stop = e.getPoint();
			// used for status bar updates
			boolean validMoveMade = false;

			// PLAY STACK OPERATIONS
			if (card != null && source != null) { // Moving from PLAY TO PLAY
				for (int x = 0; x < NUM_PLAY_DECKS; x++) {
					dest = playCardStack[x];
					// MOVING TO POPULATED STACK
					if (card.getFaceStatus() == true && dest.contains(stop) && source != dest &&
							validStackMove(card, dest.getFirst()) && transferStack.showSize() == 1 && card == source.getFirst()) {
						Card c = null;
						c = source.popFirst();
						c.repaint();
							
						table.repaint();
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
					} else if (dest.empty() && dest.contains(stop) &&
							validStackMove(card, dest.getFirst()) && transferStack.showSize() == 1) {
						Card c = null;
						c = source.popFirst();
						c.repaint();
							
						// if playstack, turn next card up
						if (source.getFirst() != null)
						{
							Card temp = source.getFirst();//.setFaceup();
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
				}
				// from PLAY TO FINAL
				for (int x = 0; x < NUM_FINAL_DECKS; x++) {
					dest = final_cards[x];

					if (card.getFaceStatus() == true && source != null && dest.contains(stop) && source != dest) {
						if (validStackMove(card, dest.getLast())) {
							if(dest.getFirst().getValue() == Card.Value.ACE && card.getValue().ordinal() == (dest.getLast().getValue().ordinal() + 1) ||
									dest.getFirst().getValue() == Card.Value.KING && card.getValue().ordinal() == (dest.getLast().getValue().ordinal() - 1)) {
								Card c = source.popFirst();
									c.repaint();
	
								if (source.getFirst() != null) {
	
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
								setScore(5);
								validMoveMade = true;
								break;
							}
						}
					}

				}
			}// end cycle through play decks

			try {
				
				if (deal_deck[deal_deck_pos].contains(start) && deck.showSize() > 0) {
					if(soundState) toggleTimer();
					for (int x = 1; x < NUM_PLAY_DECKS; x++) {
						if(soundState) {
							deal_card_ap = ap(filePath3, 0);
							deal_card_ap.play();
							Thread.sleep((deal_card_ap.getClip().getMicrosecondLength())/6000);
						}
						System.out.print("poping deck ");
						deck.showSize();
						Card c = deck.pop().setFaceup();
						playCardStack[x].putFirst(c);
						table.repaint();
					}
					table.remove(deal_deck[deal_deck_pos]);
					table.repaint();
					deal_deck_pos++;
					if(soundState) toggleTimer();
				} else if(deck.showSize() == 0) {
					statusBox.setText("No more cards to deal!");
				}
			} catch (Exception ex) {
				statusBox.setText("No more cards to deal!");
			}
			
			dealSingleCard();
			
			// SHOWING STATUS MESSAGE IF MOVE INVALID
			if (!validMoveMade && dest != null && card != null) {
				statusBox.setText("That Is Not A Valid Move");
			}
			
			// CHECKING FOR WIN
			if (checkForWin) {
				boolean gameNotOver = false;
				// cycle through final decks, if they're all full then game over
				for (int x = 0; x < NUM_FINAL_DECKS; x++) {
					dest = final_cards[x];
					System.out.println("Final deck size "+dest.showSize());
					if (dest.showSize() < 13) {
						// one deck is not full, so game is not over
						gameNotOver = true;
						gameOver = false;
						break;
					} else { 
						gameNotOver = false;
					}
					if (!gameNotOver)
						gameOver = true;
				}
			}
			
			if (checkForWin && gameOver) {
				JOptionPane.showMessageDialog(table, "Congratulations! You've Won!");
				statusBox.setText("Game Over!");
			}
			// RESET VARIABLES FOR NEXT EVENT
			start = null;
			stop = null;
			source = null;
			dest = null;
			card = null;
			checkForWin = false;
			gameOver = false;

			if(validMoveMade && soundState) {
				card_contact_ap = ap(filePath2, 0);
				card_contact_ap.play();
			}
		}// end mousePressed()
	}
	
	/*
	 * Automatically makes valid moves from play stacks to final stacks
	 * 
	 * This method doesn't play how the game should, only places applicable moves onto final stacks.
	 */
	private void autoPlay() {
		for(int z = 0; z < final_cards.length; z++) {
			for (int x = 0; x < NUM_PLAY_DECKS; x++) {
				for(int y = 0; y < playCardStack[x].showSize(); y++) {
					Card source1 = playCardStack[x].getFirst();
					Card dest = final_cards[z].getLast();
					CardStack source_stack = playCardStack[x];
					FinalStack dest_stack = final_cards[z];
					
					if(dest_stack.getFirst().getValue() == Card.Value.ACE && source1.getValue().ordinal() == (dest.getValue().ordinal() + 1) ||
							dest_stack.getFirst().getValue() == Card.Value.KING && source1.getValue().ordinal() == (dest.getValue().ordinal() - 1)) {
						if(cm.validStackMove(source1, dest)) {
							System.out.println("MOVE MADE");
							Card source = source_stack.popFirst();
							dest_stack.setXY(dest_stack.getXY().x, dest_stack.getXY().y);
							dest_stack.push(source);
								
							source.repaint();
							dest_stack.repaint();
							dest.repaint();
							table.repaint();
		
							System.out.print("Destination ");
							dest_stack.showSize();
							cm.dealSingleCard();
							setScore(5);
							System.out.println("Sound "+soundState);
							if(soundState) {
								card_contact_ap = ap(filePath2, 0);
								card_contact_ap.play();
							}
						}
						playCardStack[x].repaint();
						table.repaint();
					}
				}
			}
		}
	}
	
	private void setTableaus() {
		deck = new CardStack(true); // deal 104 cards
		deck.shuffle();
		
		// initialize & place final (foundation) decks/stacks
		final_cards = new FinalStack[NUM_FINAL_DECKS];
		
		for (int x = 0; x < NUM_FINAL_DECKS; x++) {
			for(int y = 0; y < deck.showSize(); y++) {
			final_cards[x] = new FinalStack();
			Card c = deck.pop().setFaceup();
			//Bottom Up: A to K
			if(x >= 0 && x <= 3) {
				final_cards[x].setXY(200 + (x * (Card.CARD_WIDTH)), (FINAL_POS.y));
					
				
					
				if(c.getValue().equals(Card.Value.ACE) && c.getSuit().equals(Card.Suit.SPADES) 
						&& x == 0) {
					final_cards[x].putFirst(c);
					break;	
				}
				else if(c.getValue().equals(Card.Value.ACE) && c.getSuit().equals(Card.Suit.HEARTS) 
						&& x == 1) {	
					final_cards[x].putFirst(c);
					break;
				} 
				else if(c.getValue().equals(Card.Value.ACE) && c.getSuit().equals(Card.Suit.DIAMONDS) 
						&& x == 2) {
					final_cards[x].putFirst(c);
					break;
				}
				else if(c.getValue().equals(Card.Value.ACE) && c.getSuit().equals(Card.Suit.CLUBS) 
						&& x == 3) {
					final_cards[x].putFirst(c);
					break;
				} else {
					deck.putFirst(c);
				}
			}
				//Top Down: K to A
			else if(x >= 4 && x <= 7) {
				final_cards[x].setXY(200 + ((x-4) * (Card.CARD_WIDTH)), ((65/2)*FINAL_POS.y));

					//Card c = deck.pop().setFaceup();
						
					if(c.getValue().equals(Card.Value.KING) && c.getSuit().equals(Card.Suit.SPADES)
							&& x == 4) {
						final_cards[x].putFirst(c);
						break;
					}
					else if(c.getValue().equals(Card.Value.KING) && c.getSuit().equals(Card.Suit.HEARTS)
							&& x == 5) {
						final_cards[x].putFirst(c);
						break;
					} 
					else if(c.getValue().equals(Card.Value.KING) && c.getSuit().equals(Card.Suit.DIAMONDS)
							&& x == 6) {
						final_cards[x].putFirst(c);
						break;
							
					}
					else if(c.getValue().equals(Card.Value.KING) && c.getSuit().equals(Card.Suit.CLUBS)
							&& x == 7) {
						final_cards[x].putFirst(c);
						break;
					} else {
						deck.putFirst(c);
					}
				}
			}
			table.add(final_cards[x]);
		}
		
		// initialize & place play (tableau) decks/stacks
		playCardStack = new CardStack[NUM_PLAY_DECKS];
		for (int x = 0; x < NUM_PLAY_DECKS; x++) {
			playCardStack[x] = new CardStack(false);
			playCardStack[x].setXY(350, (2*PLAY_POS.y));
			
			table.add(playCardStack[x]);
			
			if(x >= 1 && x <= 4) {
				playCardStack[x] = new CardStack(false);
				playCardStack[x].setXY((125/2) + (x * (Card.CARD_WIDTH + 15)), (3*PLAY_POS.y));

				table.add(playCardStack[x]);
			} 
			if(x >= 5 && x <= 8) {
				playCardStack[x] = new CardStack(false);
				playCardStack[x].setXY((125/2) + ((x-4) * (Card.CARD_WIDTH + 15)), (4*PLAY_POS.y));

				table.add(playCardStack[x]);
			} if(x >= 9 && x <= 13) {
				playCardStack[x] = new CardStack(false);
				playCardStack[x].setXY((125/2) + ((x-8) * (Card.CARD_WIDTH + 15)), (5*PLAY_POS.y));

				table.add(playCardStack[x]);
			}
		}
		
		
		deal_deck = new FinalStack[7];
		for (int x = 0; x < deal_deck.length; x++) {
			deal_deck[x] = new FinalStack();
			deal_deck[x].setXY(50 - (5 + (3*x)) + ((110/2)/7), (2*PLAY_POS.y));
			table.add(deal_deck[x]);
		}
		
		// Dealing new game
		for (int x = 0; x < NUM_PLAY_DECKS; x++) {
			Card c = deck.pop().setFaceup();
			playCardStack[x].putFirst(c);

		}
	}
	// Remove all listeners as to not duplicate
	private void resetGame() {
		// reset stacks if user starts a new game in the middle of one
		if (playCardStack != null && final_cards != null) {
			for (int x = 0; x < NUM_PLAY_DECKS; x++) {
				playCardStack[x].makeEmpty();
			}
			for (int x = 0; x < NUM_FINAL_DECKS; x++) {
				final_cards[x].makeEmpty();
			}
		}
		
		newGameButton.removeActionListener(menuNewGameListener);
		
		toggleTimerButton.removeActionListener(ae);
		
		showRulesButton.removeActionListener(ae);
		
		autoPlayButton.removeActionListener(ae);
		
		frame.removeWindowStateListener(wSL);
		//frame.removeWindowListener(gl);

		table.removeMouseListener(cm);
		table.removeMouseMotionListener(cm);
		
		resetTimer();
		
		deal_deck_pos = 0;
		
		score = -64;
		
		frame.remove(scroll);
		
		scroll.remove(table);
		
		table.removeAll();
		
		setTableaus();
	}
	
	private void setUpFrame() {
		autoPlayButton.addActionListener(ae);
		autoPlayButton.setBounds(0, table_height - 100, 120, 30);
		
		newGameButton.addActionListener(menuNewGameListener);
		newGameButton.setBounds(0, table_height - 70, 120, 30);

		showRulesButton.addActionListener(ae);
		showRulesButton.setBounds(120, table_height - 70, 120, 30);

		gameTitle.setText("<b>Team 1<br>Flea Market Solitaire</b> <br> CPSC 4900 <br> Fall 2021");
		gameTitle.setEditable(false);
		gameTitle.setOpaque(false);
		gameTitle.setBounds(32, 20, 100, 100);

		scoreBox.setBounds(240, table_height - 70, 120, 30);
		scoreBox.setText("Score: 0");
		scoreBox.setEditable(false);
		scoreBox.setOpaque(false);

		timeBox.setBounds(360, table_height - 70, 120, 30);
		timeBox.setText("Seconds: 0");
		timeBox.setEditable(false);
		timeBox.setOpaque(false);

		startTimer();

		toggleTimerButton.setBounds(480, table_height - 70, 125, 30);
		toggleTimerButton.addActionListener(ae);

		statusBox.setBounds(605, table_height - 70, 180, 30);
		statusBox.setEditable(false);
		statusBox.setOpaque(false);
		
		frame.setSize(table_width, table_height+10);

		table.setLayout(null);
		table.setBackground(bgColor);
		
		scoreBox.setText("Score: "+score);
		
		table.add(statusBox);
		table.add(toggleTimerButton);
		table.add(gameTitle);
		table.add(timeBox);
		table.add(newGameButton);
		table.add(showRulesButton);
		table.add(scoreBox);
		table.add(autoPlayButton);
		
		table.repaint();
		
		
		frame.add(scroll = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		table.setPreferredSize(new Dimension(750, 1160));
		scroll.setPreferredSize(new Dimension(750, 1160));
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
		scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
		SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                scroll.getVerticalScrollBar().setValue(0);
                scroll.getVerticalScrollBar().setUnitIncrement(Card.CARD_HEIGHT/4);
            }
        });
		
		frame.add(scroll);
		
		//frame.addWindowListener(gl);
		frame.addWindowStateListener(wSL);
		table.addMouseListener(cm);
		table.addMouseMotionListener(cm);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);	
	}
	
	public void resetFM() {
		resetGame();
		setUpFrame();
	}
	
	/*
	 * Set Initial Card for tableaus and locations within the frame, then set up 
	 */
	public void playFM() {
		setTableaus();
		setUpFrame();
	}


	/*public void main(String[] args)
	{
		
		playFM();
		
	}*/
}