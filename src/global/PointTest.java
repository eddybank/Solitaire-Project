package global;

import java.awt.Point;
import java.util.ArrayList;

public class PointTest {
	public static void main(String args[]) {
		ArrayList<Points> ap = new ArrayList<Points>();
		
		Points p1 = new Points(4, 2);
		ap.add(p1);
		System.out.println(ap.contains(p1));
		
		Points p2 = new Points(4, 2);
		//ap.add(p2);
		System.out.println(ap.contains(new Points(4, 2)));
		
		System.out.println(p1.equals(p2));
		System.out.println(p1.hashCode());
		System.out.println(p2.hashCode());
		
	}
}
