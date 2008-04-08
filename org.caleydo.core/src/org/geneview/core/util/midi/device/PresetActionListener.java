package org.geneview.core.util.midi.device;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PresetActionListener implements ActionListener {

	private MidiConnectorSwingSliders parent;
	
	private int iPresetId;
	
	public PresetActionListener( MidiConnectorSwingSliders parent, int iPresetId ) {
		super();
		
		this.iPresetId = iPresetId;		
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent arg0) {

		parent.writePresetToMidiDevice( iPresetId );

	}

}
