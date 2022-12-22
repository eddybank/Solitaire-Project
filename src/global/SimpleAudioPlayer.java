package global;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class SimpleAudioPlayer {
	private AudioInputStream aIS;
	private Clip clip;
		
	public SimpleAudioPlayer() throws UnsupportedAudioFileException, IOException, LineUnavailableException 
	{
		URL filePath = getClass().getResource("Sounds.dealing_card.wav");
		// create AudioInputStream object
		aIS = AudioSystem.getAudioInputStream(filePath);
			              
		// create clip reference
		clip = AudioSystem.getClip();
		// open audioInputStream to the clip
		clip.open(aIS);
		clip.loop(0);
	 }
	
	public SimpleAudioPlayer(URL fp) throws UnsupportedAudioFileException, IOException, LineUnavailableException 
	{
		// create AudioInputStream object
		aIS = AudioSystem.getAudioInputStream(fp);
			              
		// create clip reference
		clip = AudioSystem.getClip();
		// open audioInputStream to the clip
		clip.open(aIS);
		clip.loop(0);
	 }
	
	public SimpleAudioPlayer(URL fp, int loops) throws UnsupportedAudioFileException, IOException, LineUnavailableException 
	{
		// create AudioInputStream object
		aIS = AudioSystem.getAudioInputStream(fp);
			              
		// create clip reference
		clip = AudioSystem.getClip();
		// open audioInputStream to the clip
		clip.open(aIS);
		clip.loop(loops);
	 }
	
	public void play() 
    {
        //start the clip
        clip.start();
    }
	
	public void stop() throws UnsupportedAudioFileException, IOException, LineUnavailableException 
    {
        clip.stop();
        clip.close();
    }
	
	public Clip getClip() 
    {
        //start the clip
        return clip;
    }
	
	public void resetAudioStream(URL fp) throws UnsupportedAudioFileException, IOException, LineUnavailableException 
	{
		aIS = AudioSystem.getAudioInputStream(fp);
		clip.open(aIS);
		clip.loop(0);
	}
}
