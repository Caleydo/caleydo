/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.collection;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.StringTokenizer;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import cerberus.manager.type.ManagerType;
import cerberus.manager.type.ManagerObjectType;

import cerberus.command.ICommand;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.VirtualArrayType;
import cerberus.data.collection.virtualarray.VirtualArraySingleBlock;

import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class DSwingSelectionRow {

	private final static int iSizeTF = 6;
	
	private final static int INDEX_ID = 0;
	private final static int INDEX_LABEL = 1;
	private final static int INDEX_OFFSET = 2;
	private final static int INDEX_LENGTH = 3;
	private final static int INDEX_OFFSET_INC = 4;
	private final static int INDEX_REPEAT = 5;
	
	private JPanel refJPanel_Parent;
	
	private boolean bUpdateSelectionFromGui = false;
	
	private Box j_Box;
	
	private IDSwingSelectionCanvas refCanvas;
	
	private JTextField[] j_tf;
	
	private JTextArea    j_ta;
	
	private JComboBox    j_cb_selectionTypes;
	
	private ManagerObjectType currentType = ManagerObjectType.VIRTUAL_ARRAY;
	
	private final static String[] tooltipText = {"Id",
		"label",
		"offset",
		"length",
		"repeat",
		"inc" };
	
	private final static String sRandomLookupToolTipText=
		"random & multi RLE lookup values";
	
	private final static String[] sTypeNames = {"Single",
		"Multi", 
		"Multi RLE",
		"Random",
		"none"};
	
	private final static int SELECT_SINGEL = 0;
	private final static int SELECT_MULTI = 1;
	private final static int SELECT_MULTI_RLE = 2;
	private final static int SELECT_RANDOM = 3;
	private final static int SELECT_NONE = 4;
	
	private final int[] j_array_length = { 6, 15, 4, 4, 4, 4, 4, 10};
	
	/*
	 * Parapeters from GUI
	 */
	private int iDataFromGui_Id;
	private int iDataFromGui_offset;
	private int iDataFromGui_length;
	private int iDataFromGui_MultiOffset;
	private int iDataFromGui_MultiRepeat;
	private int[] iDataFromGui_MultiRLE_Random_Array = null;
	private String sDataFromGui_label;
	
	private IVirtualArray refSelection;
	
	/**
	 * 
	 */
	public DSwingSelectionRow(IDSwingSelectionCanvas setCanvas,
			JPanel setRefParentPanel) {
		
		assert setCanvas != null : "Error in constructor. setCanvas==null";
		
		//assert setRefParentPanel != null : "Error in constructor. refList==null";
		
		refCanvas = setCanvas;
		refJPanel_Parent = setRefParentPanel;
		
		/*
		 * init Jcomponents...
		 */
		
		j_ta = new JTextArea(" ");
		j_tf = new JTextField[iSizeTF];
		j_cb_selectionTypes = new JComboBox( sTypeNames );
		j_Box = Box.createHorizontalBox();
		
		j_Box.add( j_cb_selectionTypes );
		
		DSwingSelectionRowTextFieldHandler textFieldHandler =
			new DSwingSelectionRowTextFieldHandler();
		
		for ( int i=0; i<iSizeTF; i++) {
			j_tf[i] = new JTextField();
			j_tf[i].setToolTipText( tooltipText[i] );
			j_tf[i].setColumns( j_array_length[i] );
			
			j_Box.add( j_tf[i] );
			j_tf[i].addActionListener( textFieldHandler );
		}
		
		j_tf[0].setEditable( false );
		
		j_ta.setColumns(1);
		j_ta.setColumns(15);
		j_ta.setDisabledTextColor( Color.LIGHT_GRAY );
		j_ta.setAutoscrolls( true );
		j_ta.setToolTipText( sRandomLookupToolTipText );
		
		j_Box.add( new JScrollPane( j_ta ) );
		
		setGuiByBaseType( ManagerObjectType.VIRTUAL_ARRAY );
		
		j_cb_selectionTypes.addItemListener( 
				//new  DSwingSelectionRowItemListener implements 
				new ItemListener() {
					
					public void itemStateChanged( ItemEvent event ) 
					{
						if ( event.getStateChange() == ItemEvent.SELECTED ) {
							
							switch ( j_cb_selectionTypes.getSelectedIndex() ) {
							
							case SELECT_SINGEL:
								setGuiByBaseType( ManagerObjectType.VIRTUAL_ARRAY_SINGLE_BLOCK );
								break;
							case SELECT_MULTI:
								setGuiByBaseType( ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK );
								break;
							case SELECT_MULTI_RLE:
								setGuiByBaseType( ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK_RLE );
								break;
							case SELECT_RANDOM:
								setGuiByBaseType( ManagerObjectType.VIRTUAL_ARRAY_RANDOM_BLOCK );
								break;
							case SELECT_NONE:
								setGuiByBaseType( ManagerObjectType.VIRTUAL_ARRAY );
								break;
							default:
								throw new CerberusRuntimeException("unkown type in GUI!");
							}
						}
						
						refCanvas.notifySelectionHasChangedInGui();
		
					}
				} // end DSwingSelectionRowItemListener()
		); // end addItemListener
	}
	
	private void enableSingleSelection() {
		for ( int i=0; i<iSizeTF; i++) {
			if ( i<4 )
				j_tf[i].setEditable( true );
			else
				j_tf[i].setEditable( false );
		}
	}
	
	/**
	 * Enables and siables par of the Gui based on the type.
	 * 
	 * @param setType Type to switch gui
	 * @return true on success
	 */
	private boolean setGuiByBaseType(final ManagerObjectType setType){
		
		if ( setType.getGroupType() != ManagerType.DATA_VIRTUAL_ARRAY ) {
			return false;
		}
		
		/*
		 * early exit...
		 */
		if ( this.currentType == setType ) {
			return true;
		}
		
		switch ( setType ) {
		
			case VIRTUAL_ARRAY: {
				for ( int i=0; i<iSizeTF; i++) {
					j_tf[i].setEditable( false );
				}
								
				if ( j_ta.isEnabled() ) {
					j_ta.setEnabled( false );
				}
				//j_cb_selectionTypes.setEnabled( false );
				
				return true;
			}
			
			case VIRTUAL_ARRAY_SINGLE_BLOCK: {
				
				enableSingleSelection();
				
				//j_ta.setBackground( Color.LIGHT_GRAY );
				j_ta.setEnabled(false);
				j_ta.setEditable(false);
				
				if ( j_ta.isEnabled() ) {
					j_ta.setEnabled( false );
				}
				break;
			}

			case VIRTUAL_ARRAY_MULTI_BLOCK_RLE: {
				
				enableSingleSelection();
				
				if ( !j_ta.isEnabled() ) {
					j_ta.setEnabled( true );
				}
				break;
			}

			case VIRTUAL_ARRAY_RANDOM_BLOCK: {
				
				enableSingleSelection();
				
				j_ta.setBackground( Color.WHITE );
				
				j_ta.setEnabled(true);
				j_ta.setEditable(true);
				
				j_ta.setColumns(2);
				j_ta.setVisible( true );
				
				if ( !j_ta.isEnabled() ) {
					j_ta.setEnabled( true );
				}
				break;
			}
			
			case VIRTUAL_ARRAY_MULTI_BLOCK: {
				
				for ( int i=0; i<iSizeTF; i++) {
					j_tf[i].setEditable( true );
				}
				if ( j_ta.isEnabled() ) {
					j_ta.setEnabled( false );
				}
				break;
			}
			
			default:
				
		} // end switch
		
		if ( ! j_cb_selectionTypes.isEnabled() ) {
			j_cb_selectionTypes.setEnabled( true );
		}
		
		currentType = setType;
		
		return true;
	}
	
	/**
	 * Immedeatly updates the gui based on the data from the IVirtualArray.
	 * 
	 * @param useSelection data used to update the Gui
	 */
	public void updateFromSelectionToGui( final IVirtualArray useSelection ) {

		if ( useSelection == null ) {
			assert useSelection!= null :"updateFromSelection() can not handle null-pointer";
			return;
		}
		
		final ManagerObjectType testType = useSelection.getBaseType();
		
		if ( currentType != testType ) {
			this.setGuiByBaseType( testType );
			currentType = testType;
		}
		
		switch (currentType) {
		
//		case VIRTUAL_ARRAY: {
//			return;
//		}
		
		case VIRTUAL_ARRAY_SINGLE_BLOCK: {
			assert useSelection.getSelectionType() == VirtualArrayType.VIRTUAL_ARRAY_SINGLE_BLOCK :
				"Wrong type for selection VIRTUAL_ARRAY_SINGLE_BLOCK!";
			
			j_tf[INDEX_ID].setText( 
					new Integer( useSelection.getId()).toString() );
			
			j_tf[INDEX_OFFSET].setText( 
					new Integer( useSelection.getOffset()).toString() );
			
			j_tf[INDEX_LENGTH].setText( 
					new Integer( useSelection.length()).toString() );
			
			j_tf[INDEX_LABEL].setText( useSelection.getLabel() );
			return;
		}
		
		case VIRTUAL_ARRAY_MULTI_BLOCK: {
			assert useSelection.getSelectionType() == VirtualArrayType.VIRTUAL_ARRAY_MULTI_BLOCK :
				"Wrong type for selection VIRTUAL_ARRAY_MULTI_BLOCK!";
			
			j_tf[INDEX_ID].setText( 
					new Integer( useSelection.getId()).toString() );
			
			j_tf[INDEX_OFFSET].setText( 
					new Integer( useSelection.getOffset()).toString() );
			
			j_tf[INDEX_LENGTH].setText( 
					new Integer( useSelection.length()).toString() );
			
			j_tf[INDEX_LABEL].setText( useSelection.getLabel() );
			
			j_tf[INDEX_REPEAT].setText( 
					new Integer( useSelection.getMultiRepeat()).toString() );
			
			j_tf[INDEX_OFFSET_INC].setText( 
					new Integer( useSelection.getMultiOffset()).toString() );
		
			return;
		}
		
		default:
			throw new CerberusRuntimeException("updateFromSelection() unsupported type!");
		
		} // end switch
	}
	
	/**
	 * Get the current IVirtualArray shown in this element.
	 * 
	 * @return IVirtualArray the data was read from
	 */
	public IVirtualArray getCurrentSelection() {
		return refSelection;
	}
	
	/**
	 * Read back all values from Gui to private variabels.
	 */
	private void readBackGui() {
		
		try {
			iDataFromGui_Id = Integer.valueOf( j_tf[ INDEX_ID ].getText() );
		} 
		catch ( NumberFormatException ne ) {
			//reset GUI
			j_tf[ INDEX_ID ].setText( Integer.toString(iDataFromGui_Id) );
		}
		
		try {
			iDataFromGui_offset = 
				Integer.valueOf( j_tf[ INDEX_OFFSET ].getText() );
		} 
		catch ( NumberFormatException ne ) {
			//reset GUI
			j_tf[ INDEX_OFFSET ].setText( 
					Integer.toString(iDataFromGui_offset) );
		}
		
		try {
			iDataFromGui_length = 
				Integer.valueOf( j_tf[ INDEX_LENGTH ].getText() );
		} 
		catch ( NumberFormatException ne ) {
			//reset GUI
			j_tf[ INDEX_LENGTH ].setText( 
					Integer.toString(iDataFromGui_length) );
		}
		
		try {
			iDataFromGui_MultiOffset = 
				Integer.valueOf( j_tf[ INDEX_OFFSET_INC ].getText() );
		} 
		catch ( NumberFormatException ne ) {
			//reset GUI
			j_tf[ INDEX_OFFSET_INC ].setText( Integer.toString(iDataFromGui_MultiOffset) );
		}
		
		try {
			this.iDataFromGui_MultiRepeat = 
				Integer.valueOf( j_tf[ INDEX_REPEAT ].getText() );
		} 
		catch ( NumberFormatException ne ) {
			//reset GUI
			j_tf[ INDEX_REPEAT ].setText( Integer.toString(iDataFromGui_MultiRepeat) );
		}
		
		sDataFromGui_label = j_tf[ INDEX_LABEL ].getText();
		
		if (( currentType == ManagerObjectType.VIRTUAL_ARRAY_RANDOM_BLOCK ) ||
				(currentType == ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK_RLE)) {
			/*
			 * Use Tokinizer for parsing textarea...
			 */
			
			StringTokenizer tokenizer = new StringTokenizer( j_ta.getText() );
			
			int[] bufferArray = new int[ tokenizer.countTokens() ];
			
			String buffer="";
			
			for (int i=0; tokenizer.hasMoreTokens() ;i++ ) {
				
				try {
					buffer = tokenizer.nextToken();
					bufferArray[i] = Integer.valueOf( 
							buffer );
				}
				catch ( NumberFormatException ne ) {
					bufferArray[i] = 0;
					
					JOptionPane.showMessageDialog( 
							this.j_Box, 
							String.format( "ignore value [" 
									+ buffer + "]") );
				}
			}
			
			iDataFromGui_MultiRLE_Random_Array = bufferArray;
		}
	}
	
	/**
	 * updates parameters from the GUI to the IVirtualArray.
	 * 
	 * @param updateSelection
	 */
	public void updateSelectionFromGui( IVirtualArray updateSelection ) {
	
//		if ( updateSelection.getId() != iDataFromGui_Id ) {
//			updateSelection.setId( null, iDataFromGui_Id );
//		}
		
		readBackGui();
		
		updateSelection.setLength( iDataFromGui_length );
		updateSelection.setOffset( iDataFromGui_offset );
		updateSelection.setMultiOffset( iDataFromGui_MultiOffset );
		updateSelection.setMultiRepeat( iDataFromGui_MultiRepeat);
		
		ManagerObjectType selType = updateSelection.getBaseType();
		
		if ( selType == ManagerObjectType.VIRTUAL_ARRAY_RANDOM_BLOCK ) {
			updateSelection.setIndexArray( iDataFromGui_MultiRLE_Random_Array ); 
			return;
		}
		if ( selType == ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK_RLE ) {
			updateSelection.setIndexArray( iDataFromGui_MultiRLE_Random_Array ); 
			return;
		}
		
	}
	
	/**
	 * Get type that is set current.
	 * 
	 * @return current type used for the Gui.
	 */
	public ManagerObjectType getBaseType() {
		return currentType;
	}

	protected IDSwingSelectionCanvas getCanvas() {
		return refCanvas;
	}
	
	/**
	 * Do we need to update the IVirtualArray because the Gui requires it
	 * 
	 * @return True, if the IVirtualArray needs to be update.
	 */
	public boolean hasSelectionChanged() {
		return bUpdateSelectionFromGui;
	}
	
	/**
	 * Get the JComponent with is a JPanel containing all the data.
	 * 
	 * @return JPanel with all Panels.
	 */
	public JComponent getJRow() {
		return j_Box;
	}
	
	public void setVisibel( final boolean set ) {
		if ( set ) {
			if ( ! j_Box.isVisible() ) {
				j_Box.setVisible( set );
			}
		} 
		else {
			if ( j_Box.isVisible() ) {
				refSelection = null;
				currentType = ManagerObjectType.VIRTUAL_ARRAY;
				
				j_Box.setVisible(set);
			}
		}
	}
	
	public void triggerCommand( ICommand triggerCmd ) {
//		this.refCanvas.
	}
	
	private class DSwingSelectionRowTextFieldHandler implements ActionListener
	{
		
		public void actionPerformed( ActionEvent event ) {
			bUpdateSelectionFromGui = true;
			refCanvas.notifySelectionHasChangedInGui();
		}
		
	} // end class DSwingSelectionRowTextFieldHandler
}
