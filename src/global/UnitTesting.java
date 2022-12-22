package global;

import java.text.ParseException;
import java.util.Date;

import global.StatisticAnalysis.User;
import global.StatisticAnalysis.Record;

public class UnitTesting {

	public static void main(String[] args) throws ParseException {
		
		boolean exist = StatisticAnalysis.doesUserExist("joe");
		System.out.println("Does user 'joe' exist? "+exist);
		System.out.println();
		
		if(!exist) {
			System.out.println("Create User test with name joe and best time of 678 seconds");
			System.out.println();
		} else {
			System.out.println("User named joe exists already!");
			System.out.println();
		}
		
		User test = new User("joe", 678);
		System.out.println("Does user 'joe' exist now? "+StatisticAnalysis.doesUserExist("joe"));
		System.out.println();
		
		System.out.println("--Print Best Time for test before new record creation--");
		System.out.println("Expected: 678");
		System.out.println("Actual: "+test.getBestTime());
		System.out.println();
		
		System.out.println("Create record: 'Flea Market', '312', '452'");
		test.createRecord("Flea Market", 312, 452);
		System.out.println();
		
		//user tests
		System.out.println("--Print Username for test--");
		System.out.println("Expected: joe");
		System.out.println("Actual: "+test.getUser());
		System.out.println();
		
		System.out.println("--Print Best Time for test after record creation--");
		System.out.println("Expected: 678");
		System.out.println("Actual: "+test.getBestTime());
		System.out.println();
		
		System.out.println("--Print Records for test--");
		System.out.println("Expected printed list in this format:"
				+ " ['Game Type', 'Time', 'Score', 'Date']");
		System.out.println("Actual: "+StatisticAnalysis.getRecords(test));
		System.out.println();
		
		System.out.println("--Print test--");
		System.out.println("Expected: Username: joe, Best Time: 452, "
				+ "List of printed records");
		System.out.println("Actual: "+test);
		System.out.println();
		
		System.out.println("Print User color from XML file");
		System.out.println("Expected: LIGHT_GRAY");
		System.out.println("Actual: "+StatisticAnalysis.getUserColor(test));
		System.out.println();
		
		Date now = new Date();
		Record rTest = new Record("Klondike", 212, 515, now);
		
		//record tests
		System.out.println("--Print Game Type for rTest--");
		System.out.println("Expected: Klondike");
		System.out.println("Actual: "+rTest.getGame());
		System.out.println();
		
		System.out.println("--Print Score for rTest--");
		System.out.println("Expected: 212");
		System.out.println("Actual: "+rTest.getScore());
		System.out.println();
		
		System.out.println("--Print Time for rTest--");
		System.out.println("Expected: 515");
		System.out.println("Actual: "+rTest.getTime());
		System.out.println();
		
		System.out.println("--Print Date for rTest--");
		System.out.println("Expected: "+now);
		System.out.println("Actual: "+rTest.getDate());
		System.out.println();
		
		System.out.println("--Print rTest--");
		System.out.println("Expected: Time: 515, Score: 212, Date: "+now);
		System.out.println("Actual: "+rTest);
		
	}

}
