package global;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

public class AnimationP {

	private ArrayList scenes;
	private int sceneIndex;
	private long movieTime;
	private	long totalTime;
	
	//constructor
	public AnimationP() {
		scenes = new ArrayList();
		totalTime = 0;
		start();
	}
	
	//add scene to array list and set time for each scene
	public synchronized void addScene(Image i, long t) {
		totalTime += t;
		scenes.add(new OneScene(i, totalTime));
		
	}
	
	//start animation from ebinng
	public synchronized void start() {
		movieTime = 0;
		sceneIndex = 0;
	}
	
	//switch scenes
	public synchronized void update(long timePassed) { //, ArrayList<Point> p
		if(scenes.size() > 1) {
			movieTime += timePassed;
			if(movieTime >= totalTime) {
				movieTime = 0;
				sceneIndex = 0;
			}
			while(movieTime > getScene(sceneIndex).endTime) {
				sceneIndex++;
			}
		}
	}
	
	//get animations current scene
	public synchronized Image getImage() {
		if(scenes.size() == 0) return null;
		else return getScene(sceneIndex).pic;
	}
	
	//get scene
	private OneScene getScene(int x) {
		return (OneScene) scenes.get(x);
	}
	
	//////// Private Inner Class ////////
	private class OneScene {
		
		Image pic;
		long endTime;
		
		public OneScene(Image pic, long endTime) {
			this.pic = pic;
			this.endTime = endTime;
		}
	}
}


















