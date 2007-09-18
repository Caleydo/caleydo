/**
 * 
 */
package org.geneview.core.util.midi.device.data;

/**
 * Enumeration for different short MIDI messages.
 * 
 * @author Michael Kalkusch
 *
 * @see org.geneview.core.util.midi.device.data.MidiData
 */
public enum MidiShortMessageType {

	NOTE_OFF("note off","velocity:", 0x80),
	NOTE_ON("note on","velocity:", 0x90),
	POLY_PRESS("polyphonic key pressure","pressure:", 0xa0),
	CONTROL_CHANGE("control change","value:", 0xb0),
	PROGRAM_CHANGE("program change","value:", 0xb0),
	KEY_PRESS("key pressure","pressure",0xd0),
	PITCH_CHANGE("pitch wheel change","value",0xe0),
	SYSTEM_MSG("system message","value",0xe0),
	NO_MSG("no message","value",0xe0),
	ERROR_MSG("error","value",0xe0);
	
	
	private String msgTypeDescription;
	
	private String msgParameterDescription;
	
	private int msgByteCode;
	
	
	private MidiShortMessageType( String typeDescription, 
			String parameterDescription, 
			int msgByteCode){
		this.msgTypeDescription = typeDescription;
		this.msgParameterDescription = parameterDescription;
		this.msgByteCode = msgByteCode;
	}
	
	public int getByteCode() {
		return this.msgByteCode;
	}
	
	public String getTypeDescription() {
		return this.msgTypeDescription;
	}
	
	public String getParameterDescription() {
		return this.msgParameterDescription;
	}
}
