package org.caleydo.core.util.midi.device;

import java.awt.FlowLayout;
import java.awt.BorderLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Hashtable;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
//import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.sound.midi.SysexMessage;

import javax.swing.JCheckBox;
//import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
//import javax.swing.JComboBox;



import org.caleydo.core.util.midi.device.IMidiCallback;
import org.caleydo.core.util.midi.device.data.MsgBuilder;

/**	<titleabbrev>MidiConnector</titleabbrev>
<title>Listens to a MIDI port of a fader and a foot pedal and store the received events.</title>

<formalpara><title>limitations</title>
<para>
For the Sun J2SDK 1.3.x or 1.4.0, MIDI IN does not work. See the <olink targetdoc="faq_midi" targetptr="faq_midi">FAQ</olink> for alternatives.
</para></formalpara>

*/
public class MidiConnectorSwingSliders 
	implements IMidiCallback {

	/**	Flag for debugging messages.
 	If true, some messages are dumped to the console
 	during operation.
 	*/
	private boolean		DEBUG = true;
	
	@SuppressWarnings("unused")
	private MidiMessage lastMidiMessage = null;
	
	private ShortMessage lastShortMidiMessage = null;  
	
	protected PrintStream outStream;
	
	private int iNumberActiveMidiDevices = 0;
	
	protected int iNumberAllocatedMidiDevices = 0;
	
	protected MidiDevice.Info	info[];
	protected MidiDevice 		inputDevice []; 	
	
	protected MidiDevice 		outMidi;
	           		
	private DumpReceiver midiReceiver;
	
	protected JFrame mainFrame;
	protected JSlider [] sliderArray;
	protected JCheckBox [] boxArray;
	
	protected JRadioButton [] presetComboBox;
	
	protected final int iNumberSliders = 16;
	
	protected final int iNumberCheckBoxes = 16;
	
	protected final int iNumberPresets = 9;
	
	protected final int iOffsetCheckBoxes = 100;
	
	private int iLastPresetNumber = 0;
	
	protected Hashtable <Integer,Integer> hashMidiDeviceIdLoopupTable;
	
	//private String [][] settingsArray;
	
	public MidiConnectorSwingSliders() {
		
		hashMidiDeviceIdLoopupTable = new Hashtable <Integer,Integer> ();

		initMidiLookupTable();
		
		mainFrame = new JFrame();
		mainFrame.setSize( 800, 400 );
		mainFrame.setLayout( new BorderLayout() );
		mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		JPanel slidersPanel = new JPanel();
		JPanel buttonsPanel = new JPanel();
		JPanel presetPanel  = new JPanel();

		
		slidersPanel.setLayout( new FlowLayout() );
		buttonsPanel.setLayout( new FlowLayout() );
		
		mainFrame.add( slidersPanel, BorderLayout.CENTER );
		mainFrame.add( buttonsPanel, BorderLayout.NORTH );
		mainFrame.add( presetPanel, BorderLayout.SOUTH );
		
		sliderArray = new JSlider [iNumberSliders];
		boxArray = new JCheckBox[ iNumberCheckBoxes ];
		presetComboBox = new JRadioButton [ iNumberPresets ];
		
		for ( int i=0; i < iNumberSliders; i++ )			
		{
			sliderArray[i] = new JSlider(JSlider.VERTICAL, 0, 127, 10);
			
			sliderArray[i].setPaintTicks( true );
			sliderArray[i].setPaintLabels( true );
			sliderArray[i].setVisible( true );			
			
			slidersPanel.add( sliderArray[i] );
		}
		

		for ( int i=0; i < iNumberCheckBoxes; i++ )			
		{
			boxArray[i] = new JCheckBox( " #" + i ,false);
								
			buttonsPanel.add( boxArray[i] );
		}
		
		for ( int i=0; i < iNumberPresets; i++ )			
		{
			presetComboBox[i] = new JRadioButton( "#" + (i+1) );
			presetComboBox[i].setToolTipText("write preset #" + (i+1) + " ti midi device");
			presetComboBox[i].addActionListener(
					new PresetActionListener(this, i)
			);
			presetPanel.add( presetComboBox[i] );
		}
		
		mainFrame.setVisible( true );
		
	}		
	
	protected void initMidiLookupTable() {
		
		hashMidiDeviceIdLoopupTable.put( 33, 0 );
		hashMidiDeviceIdLoopupTable.put( 34, 1 );
		hashMidiDeviceIdLoopupTable.put( 35, 2 );
		hashMidiDeviceIdLoopupTable.put( 36, 3 );
		hashMidiDeviceIdLoopupTable.put( 37, 4 );
		hashMidiDeviceIdLoopupTable.put( 38, 5 );
		
		hashMidiDeviceIdLoopupTable.put( 70, 7 );
		hashMidiDeviceIdLoopupTable.put( 71, 8 );
		hashMidiDeviceIdLoopupTable.put( 72, 9 );
		hashMidiDeviceIdLoopupTable.put( 76, 10 );
		hashMidiDeviceIdLoopupTable.put( 7, 11 );
		
		/** buttons ... */
		hashMidiDeviceIdLoopupTable.put( 30, 100 );
		hashMidiDeviceIdLoopupTable.put( 31, 101 );
		hashMidiDeviceIdLoopupTable.put( 1,  102 );
		hashMidiDeviceIdLoopupTable.put( 66, 103 );
		hashMidiDeviceIdLoopupTable.put( 67, 104 );
		hashMidiDeviceIdLoopupTable.put( 68, 105 );
		hashMidiDeviceIdLoopupTable.put( 69, 106 );
	}
	
	public MidiConnectorSwingSliders( final int iSizeAllocationMidiDevices) {
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
					out("no device info found for name [" + strDeviceName + "]");
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
		
		MidiConnectorSwingSliders midiConnector= new MidiConnectorSwingSliders();
		
		midiConnector.allocateNumberOfMidiDevices( 2 );
		
		//midiConnector.initMidiDeviceByName("BCF2000 [01]");
		//midiConnector.initMidiDeviceByName("USB Audio Device");
		midiConnector.initMidiDeviceByName("USB-Audioger�t");
		//midiConnector.initMidiDeviceByIndex(2);
		
		midiConnector.connect();
	
		midiConnector.read();
	
	}
	
	public void connect() {
		
		//MidiDevice outMidi = null;
		try
		{
			outMidi = MidiSystem.getMidiDevice( MidiCommon.getMidiDeviceInfo(2));
			
			for ( int i=0; i < 6; i++ ) 
			{
				writePresetToMididDevice( outMidi, i );
			}
			
		} catch (MidiUnavailableException e1)
		{
			e1.printStackTrace();
		}
		
		
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
	

	public void callbackSetValue( String value, int iValue, int control) {
		out("set: " + value );	
		
		Integer IntloopkupValue = (Integer) hashMidiDeviceIdLoopupTable.get( control );
		
		if ( IntloopkupValue != null ) {
			
			int iloopkupValue = IntloopkupValue.intValue();
			
			if ( iloopkupValue < iOffsetCheckBoxes )
			{
				sliderArray[ iloopkupValue ].setValue( 127 - iValue );
			}
			else
			{
				if ( iValue < 1 ) {
					boxArray[ iloopkupValue - iOffsetCheckBoxes ].setSelected( false );
				}
				else
				{
					boxArray[ iloopkupValue - iOffsetCheckBoxes ].setSelected( true );					
				}
			}
		}
			
	}
	
	public void writePresetToMidiDevice( final int presetnumber) {
		
		if ( presetnumber != iLastPresetNumber ) 
		{
			presetComboBox[ iLastPresetNumber ].setSelected( false );
			iLastPresetNumber = presetnumber;
		}
		
		
		writePresetToMididDevice( outMidi, presetnumber);
		
		if ( presetnumber == 8 ) {
			System.out.println(" MIDI go...");
			writeMidiOutDevice( outMidi );
		}
	}
	
	
	protected void writePresetToMididDevice( MidiDevice MidiOut, final int presetnumber) {
		
		if ( MidiOut == null ) {
			return;
		}
		
		SysexMessage sysexMessage = new SysexMessage();
		
		 try
         {
			 if ( ! MidiOut.isOpen() ) {
				 MidiOut.open();
			 }
			 
			 int id = 0;
			 int model = 20;
			 
			 //int presetnumber = 3;
			 
             Receiver rx = MidiOut.getReceiver();
             byte mmsg[] = MsgBuilder.selectPreset(id, model, presetnumber);
             
             sysexMessage.setMessage(mmsg, mmsg.length);
             
             rx.send(sysexMessage, -1L);
         }
         catch(MidiUnavailableException e)
         {
             System.out.println("Error: " + e.getMessage());
         }
         catch(IllegalStateException llegalStateEx)
         {
             System.out.println("Error: " + llegalStateEx.getMessage());
         }
         catch(InvalidMidiDataException invDataEx)
         {
             System.out.println("Error: " + invDataEx.getMessage());
         }
         
	}
	
	protected void writeMidiOutDevice( MidiDevice MidiOut ) {
		
		System.out.println("PLAYBACK!: " + lastShortMidiMessage.toString() );
		
		if ( MidiOut == null ) {
			return;
		}
		
		if ( lastShortMidiMessage == null ) {
			return;
			
		}
		
		 try
         {
			 if ( ! MidiOut.isOpen() ) {
				 MidiOut.open();
			 }
			 
             Receiver rx = MidiOut.getReceiver();
             
             rx.send( lastShortMidiMessage , -1L);
         }
         catch(MidiUnavailableException e)
         {
             System.out.println("Error: " + e.getMessage());
         }
         catch(IllegalStateException llegalStateEx)
         {
             System.out.println("Error: " + llegalStateEx.getMessage());
         }
        
         
	}
	
	
	public void callbackSetMidiMessage( MidiMessage message ) {
		lastMidiMessage = message;
		lastShortMidiMessage = (ShortMessage) message;
	}


}
