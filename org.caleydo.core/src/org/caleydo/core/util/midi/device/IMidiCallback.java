/**
 * 
 */
package org.caleydo.core.util.midi.device;

import javax.sound.midi.MidiMessage;

/**
 * @author Michael Kalkusch
 *
 */
public interface IMidiCallback {

	public void callbackSetValue( String value, int control, int iValue );
	
	public void callbackSetMidiMessage( MidiMessage message );
}
