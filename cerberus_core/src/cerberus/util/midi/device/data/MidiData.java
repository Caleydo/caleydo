package cerberus.util.midi.device.data;

import javax.sound.midi.ShortMessage;

import cerberus.util.midi.device.data.MidiShortMessageType;

/**
 * Container to store data from MIDI event.
 * 
 * @author Michael Kalkusch
 *
 */
public class MidiData {

	protected MidiShortMessageType msgType = MidiShortMessageType.NO_MSG;
	
	protected int iChannel = -1;
	
	protected int iControlId = -1;
	
	protected int iValue = -1;
	
	
	public MidiData() {
		
	}

	public MidiShortMessageType getMidiType() {
		return this.msgType;
	}
	
	public void setMidiType( MidiShortMessageType type) {
		this.msgType = type;
	}
	
	public void setChannel( final int channel ) {
		this.iChannel = channel;
	}
	
	public int getChannel() {
		return this.iChannel;
	}
	
	public String toString() {
		String resultMsg = "[" + 
			iChannel + "# " +
			iControlId + " -> " +
			iValue + " " +
			msgType + "]";
		
		return resultMsg;
	}

	
	public int getControlId() {
	
		return iControlId;
	}

	
	public void setControlId(int controlId) {
	
		iControlId = controlId;
	}

	
	public int getValue() {
	
		return iValue;
	}

	
	public void setValue(int value) {
	
		iValue = value;
	}
	
	public void setControlIdAndValue( final ShortMessage message ) {
		iControlId = message.getData1();
		iValue = message.getData2();
	}
	
}
