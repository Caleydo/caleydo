package org.caleydo.core.util.sound;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class SoundPlayer {

	private static boolean TURN_ON_SOUND = false;
	
	public static void playSoundByFilename(String sFilename) {
		
		if (TURN_ON_SOUND == false)
			return;
		
		try{
            AudioInputStream audioInputStream = 
            	AudioSystem.getAudioInputStream(new File(sFilename));
            AudioFormat af     = audioInputStream.getFormat();
            int size      = (int) (af.getFrameSize() * audioInputStream.getFrameLength());
            byte[] audio       = new byte[size];
            DataLine.Info info      = new DataLine.Info(Clip.class, af, size);
            audioInputStream.read(audio, 0, size);
            
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(af, audio, 0, size);
            clip.start();

		}catch(Exception e){ e.printStackTrace(); }
	}
}
