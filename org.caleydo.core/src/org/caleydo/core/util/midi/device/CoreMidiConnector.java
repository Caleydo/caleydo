package org.caleydo.core.util.midi.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
//import javax.sound.midi.Receiver;
//import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import org.caleydo.core.util.midi.device.IMidiCallback;

/**	<titleabbrev>MidiConnector</titleabbrev>
<title>Listens to a MIDI port of a fader and a foot pedal and store the received events.</title>

<formalpara><title>limitations</title>
<para>
For the Sun J2SDK 1.3.x or 1.4.0, MIDI IN does not work. See the <olink targetdoc="faq_midi" targetptr="faq_midi">FAQ</olink> for alternatives.
</para></formalpara>

*/
public class CoreMidiConnector 
	implements IMidiCallback {

	/**	Flag for debugging messages.
 	If true, some messages are dumped to the console
 	during operation.
 	*/
	private boolean		DEBUG = true;
	
	protected PrintStream outStream;
	
	private int iNumberActiveMidiDevices = 0;
	
	private int iNumberAllocatedMidiDevices = 0;
	
	protected MidiDevice.Info	info[];
	protected MidiDevice 		inputDevice []; 	
	           		
	private DumpReceiver midiReceiver;
	
	public CoreMidiConnector() {
		
	}
	
	public CoreMidiConnector( final int iSizeAllocationMidiDevices) {
		this.allocateNumberOfMidiDevices( iSizeAllocationMidiDevices );
	}


	protected boolean aquireMidiInfo( MidiDevice.Info[] midiInfoArray, 
		final int iPositionInArray,
		final String strDeviceName,
		final int iDeviceIndex ) {
	
		/**
		 * Begin consistency check
		 */
		if ( iPositionInArray < 0 ) {
			return false;
		}
		
		if ( iPositionInArray >= midiInfoArray.length ) {
			return false;
		}
		
		if ((strDeviceName == null) && (iDeviceIndex < 0))
		{
			out("device name/index not specified!");
			//printUsageAndExit();
		}
		/**
		 * End consistency check
		 */
		
		
		if (strDeviceName != null) 
		{
			midiInfoArray[iPositionInArray] = MidiCommon.getMidiDeviceInfo(strDeviceName, false);
		}
		else
		{
			midiInfoArray[iPositionInArray] = MidiCommon.getMidiDeviceInfo(iDeviceIndex);
		}
		
		if (midiInfoArray[iPositionInArray] == null)
		{
			if (strDeviceName != null) 
				{
					out("no device info found for name " + strDeviceName);
				}
			else
				{
					out("no device info found for index " + iDeviceIndex);
				}
			//System.exit(1);
			return false;
		}
	
		out( "read from device: '" +  midiInfoArray[iPositionInArray].getName() + 
				"' vendor=" + midiInfoArray[iPositionInArray].getVendor() + 
				" version=" + midiInfoArray[iPositionInArray].getVersion() + 
				" details=" + midiInfoArray[iPositionInArray].getDescription()  );
	
		return true;
	}

	public static void main(String[] args)
		throws Exception {
	
		/*
		 *	The device name/index to listen to.
		 */
		
		// TODO: synchronize options with MidiPlayer
	
	
		MidiCommon.listDevicesAndExit(true, false, false);
		MidiCommon.listDevicesAndExit(false, true, false);
		
		CoreMidiConnector midiConnector= new CoreMidiConnector();
		
		midiConnector.allocateNumberOfMidiDevices( 2 );
		
		midiConnector.initMidiDeviceByName("BCF2000 [01]");
		midiConnector.initMidiDeviceByName("USB Audio Device");
		
		midiConnector.connect();
	
		midiConnector.read();
	
	}
	
	public void connect() {
		try
		{
			for ( int i=0; i < iNumberActiveMidiDevices; i++) 
			{
				inputDevice[i] = MidiSystem.getMidiDevice(info[i]);
				inputDevice[i].open();
			}
		}
		catch (MidiUnavailableException e)
			{
				out(e);
			}
		if (inputDevice == null)
			{
				out("wasn't able to retrieve MidiDevice");
				System.exit(1);
			}
		
		midiReceiver =  new DumpReceiver(System.out, this );
		
		try
		{
			for ( int i=0; i < iNumberActiveMidiDevices; i++) 
			{
				Transmitter	t = ((MidiDevice) inputDevice[i]).getTransmitter();
				t.setReceiver( midiReceiver );
			}
		}
		catch (MidiUnavailableException e)
		{
			out("wasn't able to connect the device's Transmitter to the Receiver:");
			out(e); 
			
			for ( int i=0; i < iNumberActiveMidiDevices; i++) 
			{
				inputDevice[i].close();
			}
			
			throw new RuntimeException("MIDI: failed to connect the device's Transmitter to the Receiver");
		}
	}
	
	public void read() {
		out("now running; interupt the program with [ENTER] when finished");
		
		try
			{
				InputStream in = System.in;
				in.read();
			}
		catch (IOException ioe)
			{
			}
		
		for ( int i=0; i < iNumberActiveMidiDevices; i++) 
		{
			inputDevice[i].close();
		}
		
		out("Received "+DumpReceiver.seCount+" sysex messages with a total of "+DumpReceiver.seByteCount+" bytes");
		out("Received "+DumpReceiver.smCount+" short messages with a total of "+DumpReceiver.smByteCount+" bytes");
		out("Received a total of "+(DumpReceiver.smByteCount + DumpReceiver.seByteCount)+" bytes");
		
		
		try
			{
				Thread.sleep(1000);
			}
		catch (InterruptedException e)
			{
				if (DEBUG) { out(e); }
			}
	}
	
	
	public boolean initMidiDeviceByName( final String sDeviceName ) {		
		if ( aquireMidiInfo(info, iNumberActiveMidiDevices , sDeviceName, -1) ) {
			iNumberActiveMidiDevices++;
			return true;
		}
		
		return false;
	}
	
	public boolean initMidiDeviceByIndex( final int iDeviceIndes ) {
		if ( aquireMidiInfo(info, iNumberActiveMidiDevices , "", iDeviceIndes) ) {
			iNumberActiveMidiDevices++;
			return true;
		}
		
		return false;
	}
	
	public synchronized void allocateNumberOfMidiDevices( final int iMidiDevices ) {
		if ( iMidiDevices < 0 )
		{
			return;
		}
		
		
				
		if ( info == null ) 
		{
			info = new MidiDevice.Info [iMidiDevices];
		}
		else
		{
			if ( info.length != iMidiDevices ) {
				info = new MidiDevice.Info [iMidiDevices];
			}
		}
		
		if ( inputDevice == null ) 
		{
			inputDevice = new MidiDevice[iMidiDevices];
		}
		else
		{
			if ( inputDevice.length != iMidiDevices ) {
				inputDevice = new MidiDevice[iMidiDevices];
			}
		}
		
		
		iNumberActiveMidiDevices = 0;
		iNumberAllocatedMidiDevices = iMidiDevices;
	}
	
	
	public void printUsage() {
		out("MidiInDump: usage:");
		out("  java MidiInDump -h");
		out("    gives help information");
		out("  java MidiInDump -l");
		out("    lists available MIDI devices");
		out("  java MidiInDump [-D] [-d <input device name>] [-n <device index>]");
		out("    -d <input device name>\treads from named device (see '-l')");
		out("    -n <input device index>\treads from device with given index(see '-l')");
		out("    -D\tenables debugging output");
	}
	
	public int getNumberAllocatedMidiDevices() {
		return iNumberAllocatedMidiDevices;
	}
	
	private void out(String strMessage) {
		System.out.println(strMessage);
	}
	
	
	
	private void out(Throwable t)
	{
		if (DEBUG) {
			t.printStackTrace();
		} else {
			out(t.toString());
		}
	}
	

	public void callbackSetValue( String value, int iValue, int control ) {
		out("set: " + value );		
	}
	
	public void callbackSetMidiMessage( MidiMessage message ) {
		// out("msg: " + message.toString() );	
	}

}
