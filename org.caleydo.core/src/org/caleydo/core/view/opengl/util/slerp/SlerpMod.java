package org.caleydo.core.view.opengl.util.slerp;

import gleem.linalg.open.Slerp;
import org.caleydo.core.util.sound.SoundPlayer;

/**
 * Slerp implementation that can handle rotation, scaling and transformation.
 * 
 * @author Marc Streit
 */
public class SlerpMod
	extends Slerp
{

	private static final String SLERP_SOUND = "resources/sounds/slerp.wav";

	public SlerpMod()
	{

		super();
	}

	public void playSlerpSound()
	{

		SoundPlayer.playSoundByFilename(SLERP_SOUND);
	}
}
