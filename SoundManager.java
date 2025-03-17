// for playing sound clips
import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;				// for storing sound clips

public class SoundManager {				// a Singleton class
	HashMap<String, Clip> clips;

	private static SoundManager instance = null;	// keeps track of Singleton instance

	private SoundManager () {

		loadAllClips();


		// clip = loadClip("Sounds/background.wav");
		// clips.put("background", clip);

		// clip = loadClip("Sounds/background.wav");
		// clips.put("background", clip);
	}


	public void loadAllClips () 
	{
		Clip clip;

		clips = new HashMap<String, Clip>();
		

		String names[] = {
			// "background.wav",
			// "lose.wav",
			// "win.wav",
			"hurt.wav",
			"laser.wav",
			"portal.wav"
		};


		for (int i = 0; i < names.length; i++) {
			clip = loadClip("Sounds/" + names[i]);
			clips.put(names[i].substring(0, names[i].lastIndexOf('.')), clip);
		}

		
		clip = loadClip("Sounds/background" + random(1, 3) + ".wav");
		clips.put("background", clip);

		clip = loadClip("Sounds/lose" + random(1, 3) + ".wav");
		clips.put("lose", clip);

		clip = loadClip("Sounds/win" + random(1, 2) + ".wav");
		clips.put("win", clip);
	}


	private int random (int min, int max) {
		return (int) (Math.random() * (max - min + 1) + min);
	}


	public static SoundManager getInstance() {	// class method to retrieve instance of Singleton
		if (instance == null)
			instance = new SoundManager();
		
		return instance;
	}		


    	public Clip loadClip (String fileName) {	// gets clip from the specified file
 		AudioInputStream audioIn;
		Clip clip = null;

		try {
    			File file = new File(fileName);
    			audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL()); 
    			clip = AudioSystem.getClip();
    			clip.open(audioIn);
		}
		catch (Exception e) {
 			System.out.println ("Error opening sound files " + fileName + ": " + e);
		}
    		return clip;
    	}


	public Clip getClip (String title) {

		return clips.get(title);
	}


	private void startClip(String title, boolean looping) {

		Clip clip = getClip(title);
		if (clip != null && !clip.isRunning()) {
			clip.setFramePosition(0);
			if (looping)
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			else
				clip.start();
		}
	}


	//plays clip using default volume
	public void playClip(String title, boolean looping) {
		setVolume(title, 0.7f); //default volume value
		startClip(title, looping);
	}

	//same as above but also looping is false by default
	public void playClip(String title) {
		playClip(title, false);
	}

	
	//allow clip to be played and volume to be set
	public void playClip(String title, boolean looping, float volume) {
		setVolume(title, volume);
		startClip(title, looping);
	}


	//The above but looping set to false if it is not set
	public void playClip(String title, float volume) {
		playClip(title, false, volume);
	}
	public void stopClip(String title) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.stop();
		}
	}


	public void stopAllClips() {
		for (Clip clip : clips.values()) {
			clip.stop();
		}
	}


	public void setVolume (String title, float volume) {
		Clip clip = getClip(title);

		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	
		float range = gainControl.getMaximum() - gainControl.getMinimum();
		float gain = (range * volume) + gainControl.getMinimum();

		gainControl.setValue(gain);
	}
}