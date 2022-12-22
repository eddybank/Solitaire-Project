package global;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.JCheckBox;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import Klondike.SolitaireK;
import global.StatisticAnalysis.User;
import fleaMarket.SolitaireFM;
import global.StatisticAnalysis.Record;

public class SolitaireMenu {
	
	public int TABLE_HEIGHT = 400;
	public int TABLE_WIDTH = 500;
	private User user;
	
	// GUI COMPONENTS (top level)
	private final JFrame frame = new JFrame("Solitaire Menu");
	private final JPanel menu = new JPanel();
	
	// other components
	private JButton klondikeStart = new JButton("Klondike");
	private JButton fleaMarketStart = new JButton("Flea Market");
	private JButton backgroundColorButton = new JButton("Change Background Color");
	private JButton enable = new JButton("Enable Sound");
	private JButton disable = new JButton("Disable Sound");
	private JButton records = new JButton("Look At Records");
	private JButton changeUser = new JButton("Change User");
	private JTextPane gameTypes = new JTextPane();// displays the score
	private JTextField userInput = new JTextField();
	private JTextPane statusBox = new JTextPane();// status messages
	private JButton guestButton;
	
	private boolean soundO = true;
	private Record record;
	private String gameType = "";
	
	// FM Instance
	private SolitaireFM gameFM;
	private SolitaireK gameK;
	
	// Styled docs to allow for text color changes
	private StyledDocument game = gameTypes.getStyledDocument();
	private StyledDocument status = statusBox.getStyledDocument();
	//StyledDocument scoreK = SolitaireK.scoreBox.getStyledDocument();
	//StyledDocument timeK = SolitaireK.timeBox.getStyledDocument();
	//StyledDocument gameStatusK = SolitaireK.statusBox.getStyledDocument();
	
	SimpleAttributeSet center = new SimpleAttributeSet();
	
	Style style = gameTypes.addStyle("I'm a Style", null);
	
	// Action Listener
	private ActionListener buttonListener = new ButtonListeners();
	private ActionListener newGameListener = new NewGameListener();
	
	// Color switcher
	private Color c;
	private String col;
	private final Color N_GREEN = new Color(0, 180, 0);
	
	private SimpleDateFormat sDF = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
	
	// GETTERS
	
	// sound boolean getter to check is sound is on
	public boolean getSoundState() {
		return soundO;
	}
	
	// getter method to return color as Color
	public Color getColor() {
		 return c;
	}
	
	// helper method to return color as a string
	public String getColorS() {
		 return col;
	}
	
	// Get the user or use "Guest"
	public User getUser() {
		if(this.user != null) {
			return this.user;
		} else {
			return StatisticAnalysis.setUser("Guest");
		}
	}
	
	// SETTERS
	
	public void setGameType(String game) {
		gameType = game;
	}
	
	// Method to facilitate background color changing
	private void setUpColorChange() {
		Container cP;
		
		JFrame colorFrame = new JFrame("Color Change Menu");
		JPanel colorMenu = new JPanel();
		colorFrame.setSize(300, 269);
		colorMenu.setLayout(null);
		
		cP = colorFrame.getContentPane();
		cP.add(colorMenu);
		
		JCheckBox favColor = new JCheckBox("Allow Favorite Color Change");
		
		JButton original = new JButton("Default");
		JButton red = new JButton("Red");
		JButton yellow = new JButton("Yellow");
		JButton cyan = new JButton("Cyan");
		JButton blue = new JButton("Blue");
		JButton green = new JButton("Green");
		JButton gray = new JButton("Dark Gray");
		JButton orange = new JButton("Orange");
		JButton pink = new JButton("Pink");
		JButton white = new JButton("White");
		
		JButton originalC = new JButton("Default");
		JButton redC = new JButton("Red");
		JButton yellowC = new JButton("Yellow");
		JButton cyanC = new JButton("Cyan");
		JButton blueC = new JButton("Blue");
		JButton greenC = new JButton("Green");
		JButton grayC = new JButton("Dark Gray");
		JButton orangeC = new JButton("Orange");
		JButton pinkC = new JButton("Pink");
		JButton whiteC = new JButton("White");
		/*
		 * Nested ActionListener class to facilitate action events for background color changing
		 */
		class SetUpColorListeners implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == original) {
					menu.setBackground(Color.GRAY);
					c = Color.GRAY;
					col = "GRAY";
					
				} else if(e.getSource() == red) {
					menu.setBackground(Color.RED);
					c = Color.RED;
					col = "RED";

				} else if(e.getSource() == yellow) {
					menu.setBackground(Color.YELLOW);
					c = Color.YELLOW;
					col = "YELLOW";
					
				} else if(e.getSource() == cyan) {
					menu.setBackground(Color.CYAN);
					c = Color.CYAN;
					col = "CYAN";
					
				} else if(e.getSource() == blue) {
					menu.setBackground(Color.BLUE);
					c = Color.BLUE;
					col = "BLUE";
					
				} else if(e.getSource() == green) {
					menu.setBackground(new Color(0, 180, 0));
					c = N_GREEN;
					col = "N_GREEN";
					
				} else if(e.getSource() == gray) {
					menu.setBackground(Color.DARK_GRAY);
					c = Color.DARK_GRAY;
					col = "DARK_GRAY";
					
				} else if(e.getSource() == orange) {
					menu.setBackground(Color.ORANGE);
					c = Color.ORANGE;
					col = "ORANGE";
					
				} else if(e.getSource() == pink) {
					menu.setBackground(Color.PINK);
					c = Color.PINK;
					col = "PINK";
					
				} else if(e.getSource() == white) {
					menu.setBackground(Color.WHITE);
					c = Color.WHITE;
					col = "WHITE";
			        	
				}
				
				try {
					if(c == Color.GRAY || c == Color.DARK_GRAY || c == new Color(0, 180, 0) || c == Color.BLUE) {
						gameTypes.setText("");
						statusBox.setText("");
						
						StyleConstants.setForeground(style, Color.WHITE);
						
						game.insertString(game.getLength(), "Available Game Modes", style);
						status.insertString(status.getLength(), "Welcome "+user.getUser(), style);
				        
					} else if(c == Color.WHITE || c == Color.PINK || c == Color.ORANGE || 
							c == Color.CYAN || c == Color.YELLOW || c == Color.RED) {
						gameTypes.setText("");
						statusBox.setText("");
						
						StyleConstants.setForeground(style, Color.BLACK);
						
						game.insertString(game.getLength(), "Available Game Modes", style);
						status.insertString(status.getLength(), "Welcome "+user.getUser(), style);

					}
					menu.setBackground(c);
				} catch (BadLocationException err) {
					System.out.println("Failure to change menu text color!");
				}
				
				if(favColor.isSelected()) {
					StatisticAnalysis.setUserColor(user, col);
				}
				if(gameFM != null) {
					gameFM.setColor(c);
				}
			}
		}
		
		ActionListener aL = new SetUpColorListeners();
		
		colorFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		favColor.setBounds(50, TABLE_HEIGHT - 400, 285, 20);
		
		original.addActionListener(aL);
		original.setBounds(0, TABLE_HEIGHT - 370, 285, 20);
		red.addActionListener(aL);
		red.setBounds(0, TABLE_HEIGHT - 350, 285, 20);
		yellow.addActionListener(aL);
		yellow.setBounds(0, TABLE_HEIGHT - 330, 285, 20);
		cyan.addActionListener(aL);
		cyan.setBounds(0, TABLE_HEIGHT - 310, 285, 20);
		blue.addActionListener(aL);
		blue.setBounds(0, TABLE_HEIGHT - 290, 285, 20);
		green.addActionListener(aL);
		green.setBounds(0, TABLE_HEIGHT - 270, 285, 20);
		gray.addActionListener(aL);
		gray.setBounds(0, TABLE_HEIGHT - 250, 285, 20);
		orange.addActionListener(aL);
		orange.setBounds(0, TABLE_HEIGHT - 230, 285, 20);
		pink.addActionListener(aL);
		pink.setBounds(0, TABLE_HEIGHT - 210, 285, 20);
		white.addActionListener(aL);
		white.setBounds(0, TABLE_HEIGHT - 190, 285, 20);
		
		originalC.addActionListener(aL);
		originalC.setBounds(0, TABLE_HEIGHT - 370, 285, 20);
		redC.addActionListener(aL);
		redC.setBounds(0, TABLE_HEIGHT - 350, 285, 20);
		yellowC.addActionListener(aL);
		yellowC.setBounds(0, TABLE_HEIGHT - 330, 285, 20);
		cyanC.addActionListener(aL);
		cyanC.setBounds(0, TABLE_HEIGHT - 310, 285, 20);
		blue.addActionListener(aL);
		blueC.setBounds(0, TABLE_HEIGHT - 290, 285, 20);
		greenC.addActionListener(aL);
		greenC.setBounds(0, TABLE_HEIGHT - 270, 285, 20);
		grayC.addActionListener(aL);
		grayC.setBounds(0, TABLE_HEIGHT - 250, 285, 20);
		orangeC.addActionListener(aL);
		orangeC.setBounds(0, TABLE_HEIGHT - 230, 285, 20);
		pinkC.addActionListener(aL);
		pinkC.setBounds(0, TABLE_HEIGHT - 210, 285, 20);
		whiteC.addActionListener(aL);
		whiteC.setBounds(0, TABLE_HEIGHT - 190, 285, 20);
		
		colorMenu.add(favColor);
		
		colorMenu.add(original);
		colorMenu.add(red);
		colorMenu.add(yellow);
		colorMenu.add(cyan);
		colorMenu.add(blue);
		colorMenu.add(green);
		colorMenu.add(gray);
		colorMenu.add(orange);
		colorMenu.add(pink);
		colorMenu.add(white);
		
		colorMenu.add(originalC);
		originalC.setVisible(false);
		colorMenu.add(redC);
		redC.setVisible(false);
		colorMenu.add(yellowC);
		yellowC.setVisible(false);
		colorMenu.add(cyanC);
		cyanC.setVisible(false);
		colorMenu.add(blueC);
		blueC.setVisible(false);
		colorMenu.add(greenC);
		greenC.setVisible(false);
		colorMenu.add(grayC);
		grayC.setVisible(false);
		colorMenu.add(orangeC);
		orangeC.setVisible(false);
		colorMenu.add(pinkC);
		pinkC.setVisible(false);
		colorMenu.add(whiteC);
		whiteC.setVisible(false);
		
		colorFrame.setLocationRelativeTo(frame);
		colorFrame.setVisible(true);
	}
	
	private void userCheckAndSet(JDialog jD, String username) throws BadLocationException {
		if(StatisticAnalysis.doesUserExist(username)) {
			user = StatisticAnalysis.setUser(username);
			String n = StatisticAnalysis.getUserColor(user);
			
			if(n.equals("GRAY")) {
				c = Color.GRAY;
		        
			} else if(n.equals("RED")) {
				c = Color.RED;

			} else if(n.equals("YELLOW")) {
				c = Color.BLACK;
		        
			} else if(n.equals("CYAN")) {
				c = Color.CYAN;
		        
			} else if(n.equals("BLUE")) {
				c = Color.BLUE;

			} else if(n.equals("N_GREEN")) {
				c = new Color(0, 180, 0);
		        
			} else if(n.equals("DARK_GRAY")) {
				c = Color.DARK_GRAY;
		        
			} else if(n.equals("ORANGE")) {
				c = Color.ORANGE;
		        
			} else if(n.equals("PINK")) {
				c = Color.PINK;
		        
			} else if(n.equals("WHITE")) {
				c = Color.WHITE;

			} else {
				c = Color.GRAY;

			}
			
			if(n.equals("RED") || n.equals("YELLOW") || n.equals("CYAN") || n.equals("DARK_GRAY") 
					|| n.equals("ORANGE") || n.equals("PINK") || n.equals("WHITE")) {
				gameTypes.setText("");
				statusBox.setText("");
				
				StyleConstants.setForeground(style, Color.BLACK);
				
		        game.insertString(game.getLength(), "Available Game Modes", style);
				status.insertString(status.getLength(), "Welcome "+user.getUser(), style);
			} else if (n.equals("GRAY") || n.equals("BLUE") || n.equals("N_GREEN")) {
				
				gameTypes.setText("");
				statusBox.setText("");
				
				StyleConstants.setForeground(style, Color.WHITE);
		        
		        game.insertString(game.getLength(), "Available Game Modes", style);
		        status.insertString(status.getLength(), "Welcome "+user.getUser(), style);
				
			} 
			statusBox.setText("Welcome "+user.getUser());
			menu.setBackground(c);
			jD.dispose();
		} else {
			if(!username.equals("Guest")) {
				statusBox.setText("New User Created--Welcome: "+username);
			} else {
				statusBox.setText("Welcome: "+username);
			}
			user = new User(username, 0);
			System.out.println(user);
			c = Color.GRAY;
			menu.setBackground(c);
			jD.dispose();
		}
	}
	
	/*
	 * Creates dialog window to enter user so that menu options cannot
	 * be clicked until a user logs in either with a specified user name or as a guest
	 * 
	 * Panel will dispose once user is logged in
	 */
	private void enterUser() {
		/*
		 * JDialogs stop any other actions from occurring whilst the window is still open.
		 * For this reason, we use a Dialog box for user name 
		 * input so that a game cannot be started prior to entering.
		 */
		JDialog userFrame = new JDialog(frame, true);
		JPanel userTable = new JPanel();
		userFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		userFrame.setSize(450, 100);
		JLabel userName = new JLabel("Type Desired Username then Press Enter Or Click on 'Guest' to Continue");
		guestButton = new JButton("Guest");
		
		//nested action listener to log in as Guest and change background color accordingly
		class guestListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == guestButton) {
					String username = guestButton.getText();

					try {
						userCheckAndSet(userFrame, username);
							
					} catch (BadLocationException err) {
						System.out.println("Failure to set guest user!");
					}
				}
			}
		}
		
		// nested Key listener for user input and change background color accordingly
		class userEnterListener implements KeyListener {
			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					String username = userInput.getText();
					//System.out.println("dUE: "+StatisticAnalysis.doesUserExist(username));
					userCheckAndSet(userFrame, username);
					
					}
				} catch (BadLocationException err){
					System.out.println("Failure to set user!");
				}
			}
		}
	
		KeyListener kL = new userEnterListener();
		ActionListener gL = new guestListener();
		
		userName.setBounds(0,0, 120, 30);
		
		userInput.setBounds(0,0, 120, 30);
		userInput.setColumns(5);
		userInput.addKeyListener(kL);
		
		guestButton.setBounds(130, 0, 60, 30);
		guestButton.addActionListener(gL);
		
		userTable.add(userName);
		userTable.add(userInput);
		
		userTable.add(guestButton);
		
		userFrame.add(userTable);
		
		userFrame.setBackground(c);
		userFrame.setLocationRelativeTo(frame);
		userFrame.setVisible(true);
	}
	
	
	/*
	 * Creates a frame to hold hold records retrieved from the XML file
	 * Shows all records for current user in frame
	 */
	private void openRecords() {
		
		JFrame recordFrame = new JFrame("Records Menu");
		JPanel recordMenu = new JPanel();
		
		ArrayList<Record> rec = StatisticAnalysis.getRecords(user);
		
		JScrollPane scroll;
		recordFrame.setSize(750, (rec.size()*25)+150);
		recordMenu.setSize(750, (rec.size()*25)+150);
		recordMenu.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		recordFrame.add(scroll = new JScrollPane(recordMenu, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		JEditorPane recordBox;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		
		float x = 0;
		float y = rec.size();
		for(int w = 0; w < rec.size(); w++) {
			if(rec.get(w).getScore() == 416) {
				x++;
			}	
		}
		float winPer = ((x)/y)*100; 

		JEditorPane box = new JEditorPane("text/html", "<pre><b>Solitaire</b><br>"+user.getUser()+"'s Records <br>Score for Win = 416<br>Win Percentage: "+winPer+"%</pre>");

		box.setEditable(false);
		box.setOpaque(false);
		
		recordMenu.add(box, c);
		//System.out.println(rec.size());
		
		for(int r = 0; r < rec.size() ; r++) {
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = r+1;
			recordBox = new JEditorPane("text/html", "");
			recordBox.setText("<pre><b>'"+user.getUser()+"', "+rec.get(r).getGame()+", Record "+(r+1)+"</b>"
							+ ", Time: "+rec.get(r).getTime()+" seconds, Score: "+rec.get(r).getScore()+""
							+ ", Date: "+sDF.format(rec.get(r).getDate())+"</pre>");
			recordBox.setEditable(false);
			recordBox.setOpaque(false);
			
			recordMenu.add(recordBox, c);
		}
		SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                scroll.getVerticalScrollBar().setValue(0);
                scroll.getVerticalScrollBar().setUnitIncrement(16);
            }
        });
		recordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		recordFrame.setLocationRelativeTo(frame);
		recordFrame.setVisible(true);
	}
	
	/*
	 * Action listener for starting games, enabling/diabling sound, changing user,
	 * looking at records, and reseting FM and K games
	 * 
	 */
	private class ButtonListeners implements ActionListener {	
		@Override
		public void actionPerformed(ActionEvent e) {

			if(e.getSource() == klondikeStart) {
				setGameType("Klondike");
				gameK = new SolitaireK();
				gameK.setColor(c);
				gameK.setMenuNewGameListener(newGameListener);
				//gameK.setSoundState(soundO);
				gameK.playK();
				
			} else if(e.getSource() == fleaMarketStart) {
				setGameType("Flea Market");
				gameFM = new SolitaireFM();
				
				gameFM.setColor(c);
				gameFM.setMenuNewGameListener(newGameListener);
				gameFM.setSoundState(soundO);
				gameFM.playFM();
				
			} else if(e.getSource() == backgroundColorButton) {
				setUpColorChange();
			} else if(e.getSource() == disable) {
				enable.setVisible(true);
				soundO = false;
				gameFM.setSoundState(soundO);
				disable.setVisible(false);
			} else if(e.getSource() == enable) {
				enable.setVisible(false);
				soundO = true;
				gameFM.setSoundState(soundO);
				disable.setVisible(true);
			} else if (e.getSource() == records) {
				openRecords();
			} else if(e.getSource() == changeUser) {
				enterUser();
			}
		}
	}
	
	public class NewGameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == gameFM.getNewGameButton()) {
				try {
					saveGame();
					System.out.println("Game Saved");
				} catch (ParseException e1) {
					System.out.println("Failed to FM save game!");
				} finally {
					gameFM.resetFM();
					statusBox.setText("Flea Market Reset");
				}
				
			} else if(e.getSource() == gameK.getNewGameButton()) {
				try {
					saveGame();
					System.out.println("Game Saved");
				} catch (ParseException e2) {
					System.out.println("Failed to K save game!");
				} finally {
					gameK.playK();
					statusBox.setText("Klondike Reset");
				}
			}
		}
	}
	
	// Sets record to current game stats then creates record within User object
	public void saveGame() throws ParseException {
		if(gameType.equals("Flea Market")) {
			record = new Record(gameType, Integer.parseInt(gameFM.getScoreBox().getText().substring(7)), 
			Integer.parseInt(gameFM.getTimeBox().getText().substring(9)), new Date());
			System.out.println(record);
			
		} else if (gameType.equals("Klondike")) {
			record = new Record(gameType, Integer.parseInt(gameK.getScoreBox().getText().substring(7)), 
			Integer.parseInt(gameK.getTimeBox().getText().substring(9)), new Date());
			System.out.println(record);
		}
		if(record != null) {
			getUser().createRecord(gameType, record.getScore(), record.getTime());
		}
	}
	
	// Window Listener to initiate user login
	private class windowListener implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
			enterUser();
		}
		@Override
		public void windowClosing(WindowEvent e) {}
		@Override
		public void windowClosed(WindowEvent e) {}
		@Override
		public void windowIconified(WindowEvent e) {}
		@Override
		public void windowDeiconified(WindowEvent e) {}
		@Override
		public void windowActivated(WindowEvent e) {}
		@Override
		public void windowDeactivated(WindowEvent e) {}

	}
	
	// Implements game saving by linking it to the deactivation of the game windows
	public class gameListener implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {}
		@Override
		public void windowClosing(WindowEvent e) {
			// maybe save here
		}
		@Override
		public void windowClosed(WindowEvent e) {}
		@Override
		public void windowIconified(WindowEvent e) {}
		@Override
		public void windowDeiconified(WindowEvent e) {}
		@Override
		public void windowActivated(WindowEvent e) {}
		@Override
		public void windowDeactivated(WindowEvent e) {}

	}
	
	// placing all buttons and adding their listeners
	private void openMenu() throws BadLocationException {
		
		Container contentPane;

		frame.setSize(TABLE_WIDTH, TABLE_HEIGHT);
		frame.addWindowListener(new windowListener());
		menu.setLayout(null);
		
		contentPane = frame.getContentPane();
		contentPane.add(menu);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		statusBox.setBounds(220, TABLE_HEIGHT - 120, 200, 50);
		statusBox.setEditable(false);
		statusBox.setOpaque(false);
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		status.setParagraphAttributes(0, status.getLength(), center, false);
		
		gameTypes.setText("Available Game Modes");
		gameTypes.setEditable(false);
		gameTypes.setOpaque(false);
		gameTypes.setBounds(50, TABLE_HEIGHT - 375, 150, 30);
		
		klondikeStart.addActionListener(buttonListener);
		klondikeStart.setBounds(55, TABLE_HEIGHT - 330, 120, 30);
		
		fleaMarketStart.addActionListener(buttonListener);
		fleaMarketStart.setBounds(55, TABLE_HEIGHT - 285, 120, 30);
		
		backgroundColorButton.addActionListener(buttonListener);
		backgroundColorButton.setBounds(20, TABLE_HEIGHT - 70, 200, 30);
		
		enable.addActionListener(buttonListener);
		enable.setBounds(220, TABLE_HEIGHT - 70, 200, 30);
		enable.setVisible(false);
		
		disable.addActionListener(buttonListener);
		disable.setBounds(220, TABLE_HEIGHT - 70, 200, 30);
		
		records.setBounds(240, TABLE_HEIGHT - 200, 150, 30);
		records.addActionListener(buttonListener);
		
		changeUser.setBounds(240, TABLE_HEIGHT - 230, 150, 30);
		changeUser.addActionListener(buttonListener);
		
		menu.add(changeUser);
		menu.add(records);
		menu.add(enable);
		menu.add(disable);
		menu.add(gameTypes);
		menu.add(klondikeStart);
		menu.add(fleaMarketStart);
		menu.add(backgroundColorButton);
		menu.add(statusBox);
		
		frame.setVisible(true);
	}
	
	// Create the menu, set visible, and set default close operation
	public static void main(String[] args) throws BadLocationException {

		SolitaireMenu sm = new SolitaireMenu();

		sm.openMenu();

	}

}
