/**
 * 
 */
package cerberus.util.midi.device;

import javax.sound.midi.MidiMessage;

/**
 * @author kalkusch
 *
 */
public interface IMidiCallback {

	public void callbackSetValue( String value, int control, int iValue );
	
	public void callbackSetMidiMessage( MidiMessage message );
}
