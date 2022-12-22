package global;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class StatisticAnalysis {

	/*
	 * Create Directory based on users system--Directory path would be 'C:\Users\"NameofUser"' on windows
	 * XML files to store record for individual users
	 */

	private static final File savedir = new File(System.getProperty("user.home"), ".solitairegame");
	private static boolean dircreate = savedir.mkdir();
	
	//used for unit testing purposes
	//private static final File savefile = new File(savedir, "test.xml");
	//private static File filePP = new File(savedir, "testPP.xml");
	
	private static final File savefile = new File(savedir, "users.xml");
	private static File filePP = new File(savedir, "usersPP.xml");
	private static SimpleDateFormat sDF = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
	
	
	/*
	 * Create directory on users home User directory (windows) named .solitairegame
	 * then create the initial XML structure
	 */
	public static void createFiles()
	{
		try {
			final boolean save = savefile.createNewFile();
			if(save)
			{
			
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder;
	
			    documentBuilder = documentBuilderFactory.newDocumentBuilder();
			    Document document;
			    document = documentBuilder.newDocument();
			    
			    Element newUsers = document.createElement("users");
	
			    document.appendChild(newUsers);
			    
			    DOMSource source = new DOMSource(document);
			    TransformerFactory transformerFactory = TransformerFactory.newInstance();
			    Transformer transformer = transformerFactory.newTransformer();
			    StreamResult result = new StreamResult(savefile);
			    transformer.transform(source, result);
			}
		    	 
		} catch (IOException | ParserConfigurationException | NullPointerException | TransformerException e) 
		{
				System.out.println("Error occurred - Printing stack trace");
				e.printStackTrace();
		}
	}
	
	/*
	 * Loop through users, setting the favorite color selected for the current user
	 */
	public static void setUserColor(User user) 
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		String c = SolitaireMenu.getColorS();
	    try 
	    {
	    	 documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    	 Document document;
	    	 document = documentBuilder.parse(savefile);

	    	 NodeList users = document.getElementsByTagName("user");
	    	 
	    	 for(int i = 0; i < users.getLength(); i++)
	    	 {
	    		 Node node = users.item(i);
	    		 Element ele = (Element) node;
	    		 if(user.getUser().equals(ele.getElementsByTagName("name").item(0).getTextContent()) ) {
	    			 System.out.println(c);
	    			 ele.getElementsByTagName("bgcolor").item(0).setTextContent(c);
	    		 }
	    	 }
	    	 
	    	 DOMSource source = new DOMSource(document);

	    	 TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    	 Transformer transformer = transformerFactory.newTransformer();
	    	 //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    	 StreamResult result = new StreamResult(savefile);
	    	 transformer.transform(source, result);
	    	 
		} catch (SAXException | IOException | ParserConfigurationException | NullPointerException | TransformerException e) 
	    {
			System.out.println("Error occurred - Printing stack trace");
			e.printStackTrace();
		}
	    prettyPrint();
	}
	
	/*
	 * Loop through users, getting the favorite color for the current user
	 */
	public static String getUserColor(User user) 
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		String color = "";
	    try 
	    {
	    	 documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    	 Document document;
	    	 document = documentBuilder.parse(savefile);

	    	 NodeList users = document.getElementsByTagName("user");
	    	 
	    	 for(int i = 0; i < users.getLength(); i++)
	    	 {
	    		 Node node = users.item(i);
	    		 Element ele = (Element) node;
	    		 if(user.getUser().equals(ele.getElementsByTagName("name").item(0).getTextContent())) {
	    			 color = ele.getElementsByTagName("bgcolor").item(0).getTextContent();
	    		 }
	    	 }
		} catch (SAXException | IOException | ParserConfigurationException | NullPointerException e) 
	    {
			System.out.println("Error occurred - Printing stack trace");
			e.printStackTrace();
		}
	    return color;
	}
	
	/*
	 * Loop through users to determine if a user with that name already exists
	 * Creates file if not already created for the case that the new user is the only user
	 * 
	 * Used for newUser() to verify that username is not taken
	 */
	public static boolean doesUserExist(String name) 
	{
		createFiles();
		ArrayList<User> allUsers = getAllUsers();
		//System.out.println(allUsers);
		boolean doesExist = false;
		for( User u : allUsers) {
			
			if(u.getUser().equals(name))
			{
				doesExist = true;
			}
		}
		return doesExist;
	}
	
	/*
	 * Loop through users, creating an array list of all the user objects 
	 * including all saved records for each user
	 * 
	 * @return		ArrayList<User> object
	 */
	private static ArrayList<User> getAllUsers() 
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		ArrayList<User> allUsers = new ArrayList<User>();
		Record rec = new Record();
		User user = new User();
	    try 
	    {
	    	 documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    	 Document document;
	    	 document = documentBuilder.parse(savefile);

	    	 NodeList users = document.getElementsByTagName("user");
	    	 
	    	 String game = "";
	    	 int time = 0;
	    	 int score = 0;
	    	 Date date = null;
	    	 for(int i = 0; i < users.getLength(); i++)
	    	 {
	    		 Node node = users.item(i);
	    		 Element ele = (Element) node;
	    		 user = new User(ele.getElementsByTagName("name").item(0).getTextContent());
	    		 //System.out.println(user);
	    		 user.setBestTime(Integer.parseInt(ele.getElementsByTagName("best_time").item(0).getTextContent()));
	    			 if(node.getNodeType() == Node.ELEMENT_NODE) 
	    			 {
	    				 NodeList recs = ele.getElementsByTagName("record");
	    				 
	    				 for(int y = 0; y < recs.getLength(); y++)
	    				 {
	    					 game = ele.getElementsByTagName("game").item(y).getTextContent();
		    				 time = Integer.parseInt(ele.getElementsByTagName("score").item(y).getTextContent());
		    				 score = Integer.parseInt(ele.getElementsByTagName("time").item(y).getTextContent());
		    				 date = sDF.parse(ele.getElementsByTagName("date").item(y).getTextContent());
		    				 rec = new Record(game, time, score, date);
		    				 user.addRecord(rec);
		    				//System.out.println(rec);
	    				 }
	    			 }
	    			 allUsers.add(user);
	    	 }
		} catch (SAXException | IOException | ParserConfigurationException | DOMException | ParseException e) 
	    {
			System.out.println("Error occurred - Printing stack trace");
			e.printStackTrace();
		}
		return allUsers;
	}	
	
	/*
	 * Returns a User object based on the input string of the username
	 * 
	 * @param  name  a string of user input used for username during login
	 * @return		User object
	 */
	public static User setUser(String name) 
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;

		Record rec = new Record();
		User user = new User();
	    try 
	    {
	    	 documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    	 Document document;
	    	 document = documentBuilder.parse(savefile);

	    	 NodeList users = document.getElementsByTagName("user");
	    	 
	    	 String game = "";
	    	 int time = 0;
	    	 int score = 0;
	    	 Date date = null;
	    	 
	    	 for(int i = 0; i < users.getLength(); i++)
	    	 {
	    		 Node node = users.item(i);
	    		 Element ele = (Element) node;
	    		 if(name.equals(ele.getElementsByTagName("name").item(0).getTextContent())) 
	    		 {
	    			 user = new User(ele.getElementsByTagName("name").item(0).getTextContent());
	    			 user.setBestTime(Integer.parseInt(ele.getElementsByTagName("best_time").item(0).getTextContent()));
	    			 if(node.getNodeType() == Node.ELEMENT_NODE) 
	    			 {
	    				 NodeList recs = ele.getElementsByTagName("record");
	    				 
	    				 for(int y = 0; y < recs.getLength(); y++)
	    				 {
	    					 game = ele.getElementsByTagName("game").item(y).getTextContent();
		    				 time = Integer.parseInt(ele.getElementsByTagName("score").item(y).getTextContent());
		    				 score = Integer.parseInt(ele.getElementsByTagName("time").item(y).getTextContent());
		    				 date = sDF.parse(ele.getElementsByTagName("date").item(y).getTextContent());
		    				 rec = new Record(game, time, score, date);
		    				 user.addRecord(rec);
	    				 }
	    			 }
	    		 }
	    	 }
		} catch (SAXException | IOException | ParserConfigurationException | DOMException | ParseException e) 
	    {
			System.out.println("Error occurred - Printing stack trace");
			e.printStackTrace();
		}
	    return user;
	}	
	
	/*
	 * Loop through a users record, creating an array list of all the record objects 
	 *
	 * @param  user  User object of the current user
	 * @return		ArrayList<Record> object
	 */
	public static ArrayList<Record> getRecords(User user) 
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		Record rec = new Record();
		user.resetRecords();
	    try 
	    {
	    	 documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    	 Document document;
	    	 document = documentBuilder.parse(savefile);

	    	 NodeList users = document.getElementsByTagName("user");
	    	 
	    	 String game = "";
	    	 int best_time = 0;
	    	 int time = 0;
	    	 int score = 0;
	    	 Date date = null;
	    	 
	    	 for(int i = 0; i < users.getLength(); i++)
	    	 {
	    		 Node node = users.item(i);
	    		 Element ele = (Element) node;
	    		 //System.out.println(ele.getElementsByTagName("name").item(0).getTextContent());
	    		 //System.out.println(user.getUser());
	    		 if(user.getUser().equals(ele.getElementsByTagName("name").item(0).getTextContent())) 
	    		 {
	    			 if(node.getNodeType() == Node.ELEMENT_NODE) 
	    			 {
	    				 best_time = Integer.parseInt(ele.getElementsByTagName("best_time").item(0).getTextContent());
	    				 
	    				 NodeList recs = ele.getElementsByTagName("record");
	    				 
	    				 for(int y = 0; y < recs.getLength(); y++)
	    				 {
	    					 game = ele.getElementsByTagName("game").item(y).getTextContent();
		    				 time = Integer.parseInt(ele.getElementsByTagName("score").item(y).getTextContent());
		    				 score = Integer.parseInt(ele.getElementsByTagName("time").item(y).getTextContent());
		    				 date = sDF.parse(ele.getElementsByTagName("date").item(y).getTextContent());
		    				 rec = new Record(game, time, score, date);
		    				 user.addRecord(rec);
		    				//System.out.println("y: "+rec);
	    				 }
	    			 } 
	    		 }
	    	 }
		} catch (SAXException | IOException | ParserConfigurationException | DOMException | ParseException e) 
	    {
			System.out.println("Error occurred - Printing stack trace");
			e.printStackTrace();
		}
		return user.getRecords();
	}	
	
	/*
	 * Save a record on to the XML file under the specified user
	 * 
	 * @param	user	User object of current user
	 * @param	r	Record object that is to be added to XML file
	 */
	private static void setRecord(User user, Record r) 
	{
		 DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	     DocumentBuilder documentBuilder;
	     try 
	     {
	    	 documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    	 Document document;
	    	 document = documentBuilder.parse(savefile);
	    	 
	    	// user elements
	    	 NodeList users = document.getElementsByTagName("user");
	    	 
	    	 for(int i = 0; i < users.getLength(); i++)
	    	 {
	    		 Node node = users.item(i);
	    		 Element ele = (Element) node;
	    		 //System.out.println(ele.getElementsByTagName("name").item(0).getTextContent());
	    		 //System.out.println(user.getUser());
	    		 if(user.getUser().equals(ele.getElementsByTagName("name").item(0).getTextContent())) 
	    		 {
	    			 ele.getElementsByTagName("best_time").item(0).setTextContent(Integer.toString(user.getBestTime()));
	    			 
	    			 Element record = document.createElement("record");

	    			 Element game = document.createElement("game");
	    			 game.appendChild(document.createTextNode(SolitaireMenu.gameType));
	    			 record.appendChild(game);
	    			 
	    			 Element score = document.createElement("score");
	    			 score.appendChild(document.createTextNode(Integer.toString(r.getScore())));
	    			 record.appendChild(score);
	    	        
	    			 Element time = document.createElement("time");
	    			 time.appendChild(document.createTextNode(Integer.toString(r.getTime())));
	    			 record.appendChild(time);
	    			 
	    			 Element date = document.createElement("date");
	    			 date.appendChild(document.createTextNode(sDF.format(r.getDate())));
	    			 record.appendChild(date);
	    	        
	    			 ele.appendChild(record);
	    		 }
	    	 }
	    	 
	    	 DOMSource source = new DOMSource(document);

	    	 TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    	 Transformer transformer = transformerFactory.newTransformer();
	    	 StreamResult result = new StreamResult(savefile);
	    	 transformer.transform(source, result);
	        
	        
	     } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) // 
	     {
				System.out.println("Error occurred - Printing stack trace");
				e.printStackTrace();
	     }
	     prettyPrint();
	}	
	
	/*
	 * Create a XML files and structure for a newly created user
	 * 
	 * @param	user	User object of the user that has just been created
	 */
	private static void newUser(User user) 
	{
		 DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder documentBuilder;
		 createFiles();
		 try 
		 {
			 documentBuilder = documentBuilderFactory.newDocumentBuilder();
			 Document document;
			 document = documentBuilder.parse(savefile);
			
			 Element root = document.getDocumentElement(); 
	    	 
	    	 if(!doesUserExist(user.getUser())) {
		    	 Element newUser = document.createElement("user");
	
				 Element name = document.createElement("name");
				 name.appendChild(document.createTextNode(user.getUser()));
				 newUser.appendChild(name);
		        
				 Element bestTime = document.createElement("best_time");
				 bestTime.appendChild(document.createTextNode(Integer.toString(user.getBestTime())));
				 newUser.appendChild(bestTime);
				 
				 Element color = document.createElement("bgcolor");
				 color.appendChild(document.createTextNode("LIGHT_GRAY"));
				 newUser.appendChild(color);
				 
				 root.appendChild(newUser);
	    	 }

			 DOMSource source = new DOMSource(document);

			 TransformerFactory transformerFactory = TransformerFactory.newInstance();
			 Transformer transformer = transformerFactory.newTransformer();
			 //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			 StreamResult result = new StreamResult(savefile);
			 transformer.transform(source, result);
	        
		 } catch (SAXException | IOException | ParserConfigurationException | TransformerException e)
		 {
				System.out.println("Error occurred - Printing stack trace");
				e.printStackTrace();
		 }
		 prettyPrint();
	}
	
	/*
	 * Create a pretty print version of the XML so it is readable 
	 */
	public static void prettyPrint(){
	    try{
	    	boolean deleted = filePP.delete();
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	        
	        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse(savefile);
	        
	        DOMSource source = new DOMSource(doc);
	        filePP = new File(savedir, "usersPP.xml");
	        StreamResult result = new StreamResult(filePP);

	        transformer.transform(source, result);
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }
	    }
	
	//Nested User class
	public static class User 
	{
		
		private String username;
		private int best_time;
		
		private ArrayList<Record> records = new ArrayList<Record>();
		
		//helper constructors for XML methods
		public User(){}
		
		public User(String user)
		{
			username = user;
			//best_time = 0;
		}
		
		public User(String user, int bt)
		{
			username = user;
			best_time = bt;
			newUser(this);
		}
		
		public String getUser() 
		{
			return username;
		}
		
		public int getBestTime() 
		{
			return best_time;
		}
		
		public void setBestTime()
		{
			if(records.size() >= 1)
			{
				for(int x = 0; x < records.size(); x++)
				{
					if(records.get(x).getScore() == 416) {
						if(records.get(x).getTime() < best_time)
						{
							best_time = records.get(x).getTime();
						}
					}
				}
			}
		}
		
		public void setBestTime(int i)
		{
			best_time = i;
		}
		
		public boolean isBestTime(Record r)
		{
			if(r.getTime() >= getBestTime())
			{
				return true;
			} else 
			{
				return false;
			}
		}
		public void resetRecords() {
			records = new ArrayList<Record>();
		}
		
		public void addRecord(Record rec) 
		{
			records.add(rec);
		}

		public void createRecord(String game, int score, int time) throws ParseException 
		{
			Record newR = new Record(game, score, time, new Date());
			records.add(newR);
			setBestTime();
			setRecord(this, newR);
		}
		
		public ArrayList<Record> getRecords()
		{
			return records;
		}
		public String toString()
		{
			String userStr = "Username: "+this.getUser()+" Best Time: "+this.getBestTime()+"\n";
			for(Record r : this.getRecords()) 
			{
				userStr += "	"+r.toString()+"\n";
			}
			return userStr;
		}
	}
	
	//Nested Record class
	public static class Record 
	{
		private String game;
		private int time;
		private int score;
		private Date date;
			
		public Record(){}
		
		public Record(String g, int s, int t, Date d){
			game = g;
			score = s;
			time = t;
			date = d;
		}
			
		public String getGame()
		{
			return game;
		}
		
		public int getScore()
		{
			return score;
		}
			
		public int getTime()
		{
			return time;
		}
		
		public Date getDate()
		{
			return date;
		}
		
		public String toString()
		{
			return "Game: "+game+", Time: "+time+", Score: "+score+", Date: "+date;
		}
	}

	
		
	
	public static void main(String[] args) {
		
		//User test1 = setUser("test");
		//System.out.println(getUserColor(test1));
		System.out.println(System.getProperty("user.home"));
		System.out.println(savefile.getPath());
		//createFiles();
		//User test2 = setUser("testing");
		
		//setUserColor(test2);
		//test2.createRecord(416, 780);
		
		
		//User test2 = new User("bobby", 0);
		//User test3 = new User("tommy", 0);
		
		//test3.createRecord(416, 999);
		
		//test1.createRecord(416, 1021);
		
		//System.out.println((getRecords(test1)));
		//System.out.println((getAllUsers()));
		//System.out.println(test1.getBestTime());
	}
	
}
