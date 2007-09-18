package org.geneview.core.util.slerp;

import org.geneview.core.util.sound.SoundPlayer;

import gleem.linalg.open.Slerp;

/**
 * Slerp implementation that can handle 
 * rotation, scaling and transformation.
 * 
 * @author Marc Streit
 */
public class SlerpMod extends Slerp {
	
	private static final String SLERP_SOUND = "resources/sounds/slerp.wav";
	


	public SlerpMod() {
		
		super();
	}
    
    public void playSlerpSound() {
    	
    	SoundPlayer.playSoundByFilename(SLERP_SOUND);
    }
}
