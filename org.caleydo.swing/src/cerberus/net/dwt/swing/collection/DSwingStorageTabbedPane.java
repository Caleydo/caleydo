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
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.StringTokenizer;
import java.lang.StringBuffer;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import cerberus.manager.type.ManagerType;
import cerberus.manager.type.ManagerObjectType;

import cerberus.command.ICommand;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.VirtualArrayType;
import cerberus.data.collection.virtualarray.VirtualArraySingleBlock;
import cerberus.net.dwt.swing.collection.DSwingStorageCanvas;

import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class DSwingStorageTabbedPane extends JPanel {

	private JTabbedPane gui_tabbedPane;
	
	private JPanel[]		gui_Panels;
	
	private final static int iSizePanels = 3;
	
	private final static int iSizeTF = 2;
	
	private final static int INDEX_ID = 0;
	private final static int INDEX_LABEL = 1;
	
	//private JPanel refJPanel_Parent;
	
	private boolean bUpdateSelectionFromGui = false;
	
	private Box j_Box;
	
	private DSwingStorageCanvas refCanvas;
	
	private JTextField[] j_tf;
	
	private JTextArea[]  j_ta;
	
	private JLabel		 j_label_length;
	
	private JComboBox    j_cb_selectionTypes;
	
	private ManagerObjectType currentType = ManagerObjectType.VIRTUAL_ARRAY;
	
	private final static String[] sTabbenPaneNames = {
		"INT",
		"FLOAT",
		"STRING" };
	
	private final static String[] sTabbenPaneToolTipNames = {
		"INT",
		"FLOAT",
		"STRING" };
	
	private final static String[] tooltipText = {"Id",
		"label" };
	
	private final static String[] sTypeNames = {"Flat IStorage",
		"none"};
	
	private final static int[] j_array_length = { 5, 10};
	
	private final static int TAB_INT = 0;
	private final static int TAB_FLOAT = 1;
	private final static int TAB_STRING = 2;
	
	private final static int STORE_FLAT = 0;
	private final static int STORE_NONE = 1;
	

	/*
	 * Parapeters from GUI
	 */
	private int iDataFromGui_Id;
	private String sDataFromGui_label;
	private String[] iDataFromGui_String;
	private int[] iDataFromGui_int;
	private float[] iDataFromGui_float;
	
	private boolean bDataFromGui_Int_HasChanged 	= true;
	private boolean bDataFromGui_Float_HasChanged 	= true;
	private boolean bDataFromGui_String_HasChanged 	= true;
	
	private IStorage refStorage;
	
	/**
	 * 
	 */
	public DSwingStorageTabbedPane(DSwingStorageCanvas setCanvas ) {
			//JPanel setRefParentPanel) {
		
		super();
		
		assert setCanvas != null : "Error in constructor. setCanvas==null";
		//assert setRefParentPanel != null : "Error in constructor. refList==null";
		
		refCanvas = setCanvas;
		//refJPanel_Parent = setRefParentPanel;
		
		/*
		 * init Jcomponents...
		 */
		
		Box gui_IdLabelBox = Box.createVerticalBox();
		
		gui_tabbedPane 	= new JTabbedPane();
		gui_Panels 		= new JPanel[ iSizePanels ];
		j_ta 			= new JTextArea[iSizePanels];
		j_tf 			= new JTextField[ iSizeTF ];
		j_label_length 	= new JLabel();
		
		j_Box = Box.createHorizontalBox();
		
		gui_tabbedPane.setVisible( true );
		gui_tabbedPane.setBackground( Color.RED );
		
		BorderLayout defaultPanelBoderLayout = new BorderLayout();
		
		for ( int i=0; i<iSizePanels ; i++) {
			gui_Panels[i] = new JPanel();
			
			j_ta[i] = new JTextArea();
			j_ta[i].setToolTipText( sTabbenPaneToolTipNames[i] );
			j_ta[i].setAutoscrolls( true );
			j_ta[i].setText("  ");
			j_ta[i].setColumns( 30 );
			j_ta[i].setRows( 2 );
			j_ta[i].setVisible( true );
			j_ta[i].setBackground( Color.CYAN );
			
			gui_Panels[i].setLayout( defaultPanelBoderLayout );
			gui_Panels[i].add( new JLabel( sTabbenPaneNames[i]),
					BorderLayout.SOUTH );
			gui_Panels[i].add(j_ta[i], BorderLayout.CENTER );
			
			gui_Panels[i].setBackground( Color.GREEN );
			
			gui_tabbedPane.addTab( 
					sTabbenPaneNames[i],
					null,
					gui_Panels[i],
					sTabbenPaneToolTipNames[i] );
		}
		
		j_cb_selectionTypes = new JComboBox( sTypeNames );
		
		DSwingSelectionRowTextFieldHandler textFieldHandler =
			new DSwingSelectionRowTextFieldHandler();
		
		for ( int i=0; i<iSizeTF; i++) {
			j_tf[i] = new JTextField();
			j_tf[i].setToolTipText( tooltipText[i] );
			j_tf[i].setColumns( j_array_length[i] );
			
			//southJPanel.add( j_tf[i] );
			j_tf[i].addActionListener( textFieldHandler );
		}
		j_tf[0].setEditable( false );
			
		setGuiByBaseType( ManagerObjectType.VIRTUAL_ARRAY );
		
		/*
		 * General Layout
		 */
		j_Box.add( j_tf[INDEX_ID] );
		j_Box.add( j_tf[INDEX_LABEL] );
		j_Box.add( j_label_length );
		
		for ( int i=0; i< iSizePanels ; i++ ) {
			j_Box.add( new JScrollPane( j_ta[i] )  );
		}
		
//		generalJPanel.setLayout( new BorderLayout() );
//		generalJPanel.add( southJPanel, BorderLayout.SOUTH );
//		generalJPanel.add( gui_tabbedPane, BorderLayout.CENTER );
//		
//		j_Box.add( generalJPanel );
		
		//j_Box.add( gui_IdLabelBox );
		//j_Box.add( gui_tabbedPane );
		
		j_cb_selectionTypes.addItemListener( 
				//new  DSwingSelectionRowItemListener implements 
				new ItemListener() {
					
					public void itemStateChanged( ItemEvent event ) 
					{
						
						if ( event.getStateChange() == ItemEvent.SELECTED ) {
							
							switch ( j_cb_selectionTypes.getSelectedIndex() ) {
							
							case STORE_FLAT:
								setGuiByBaseType( ManagerObjectType.STORAGE_FLAT );
								break;
							case STORE_NONE:
								setGuiByBaseType( ManagerObjectType.STORAGE );
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
	
	
	/**
	 * Enables and siables par of the Gui based on the type.
	 * 
	 * @param setType Type to switch gui
	 * @return true on success
	 */
	private boolean setGuiByBaseType(final ManagerObjectType setType){
		
		if ( setType.getGroupType() != ManagerType.DATA_STORAGE ) {
			return false;
		}
		
		/*
		 * early exit...
		 */
		if ( this.currentType == setType ) {
			return true;
		}
		
		switch ( setType ) {
		
			case STORAGE: {
				for ( int i=0; i<iSizeTF; i++) {
					j_tf[i].setEditable( false );
					j_tf[i].setEnabled( false );
				}
				
				break;
			}
			
			case STORAGE_FLAT: {
				for ( int i=0; i<iSizeTF; i++) {
					j_tf[i].setEditable( true );
					j_tf[i].setEnabled( true );
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
	public void updateFromStorageToGui( final IStorage useStorage ) {

		if ( useStorage == null ) {
			assert useStorage!= null :"updateFromStorageToGui() can not handle null-pointer";
			return;
		}
		
		final ManagerObjectType testType = useStorage.getBaseType();
		
		if ( currentType != testType ) {
			setGuiByBaseType( testType );
		}
		
		switch (currentType) {
		
		case STORAGE_FLAT: {
			assert useStorage.getBaseType() == ManagerObjectType.STORAGE_FLAT :
				"Wrong type for storage STORAGE_FLAT!";
			
			j_tf[INDEX_ID].setText( 
					new Integer( useStorage.getId()).toString() );
			
			j_tf[INDEX_LABEL].setText( useStorage.getLabel() );
			
			StringBuffer buffer = new StringBuffer();
			
			{
				final int[] iIntArray = useStorage.getArrayInt();
				final int iReducedSizeIntArray = iIntArray.length -1;
				
				for ( int i=0; i < iReducedSizeIntArray; i++ ){
					buffer.append( Integer.toString(iIntArray[i]) );
					buffer.append( " " );
				}
				if ( iReducedSizeIntArray > 0) {
					buffer.append( Integer.toString(iIntArray[iReducedSizeIntArray]) );
				}
				
				j_ta[TAB_INT].setText( new String(buffer) );
				
			}
			
			{
				/* wipe buffer */
				buffer.setLength( 0 );
				final float[] iFloatArray = useStorage.getArrayFloat();
				final int iReducedSizeFloatArray = iFloatArray.length -1;
				
				for ( int i=0; i < iReducedSizeFloatArray; i++ ){
					buffer.append( Float.toString(iFloatArray[i]) );
					buffer.append( " " );
				}
				if ( iReducedSizeFloatArray > 0) {
					buffer.append( Float.toString(iFloatArray[iReducedSizeFloatArray]) );
				}
				
				j_ta[TAB_FLOAT].setText( new String(buffer) );
			}
			
			{
				/* wipe buffer */
				buffer.setLength( 0 );
				final String[] iStringArray = useStorage.getArrayString();
				final int iReducedSizeStringArray = iStringArray.length -1;
				
				for ( int i=0; i < iReducedSizeStringArray; i++ ){
					buffer.append( iStringArray[i] );
					buffer.append( " " );
				}
				if ( iReducedSizeStringArray > 0) {
					buffer.append( iStringArray[iReducedSizeStringArray] );
				}
				
				j_ta[TAB_STRING].setText( new String(buffer) );
			}
			          
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
	public IStorage getCurrentSelection() {
		return refStorage;
	}
	
	/**
	 * Read back all values from Gui to private variabels.
	 */
	private void readBackGui() {
		
		final int iTabbedIndex = gui_tabbedPane.getSelectedIndex();
		
		StringTokenizer tokenizer = new StringTokenizer( j_ta[TAB_STRING].getText() );
		final int iCountTokens = tokenizer.countTokens();
		
		/*
		 * read back Id
		 */
		try {
			iDataFromGui_Id = Integer.valueOf( j_tf[INDEX_ID].getText() );
		}
		catch ( NumberFormatException ne ) {
			//
		}
		
		/*
		 * Read back label
		 */
		sDataFromGui_label = j_tf[INDEX_LABEL].getText();
		
		/*
		 * Read back test area's
		 */
		switch ( iTabbedIndex ) {
		
		case TAB_INT:
			
			int[] bufferIntArray = new int[ iCountTokens ];
			
			for (int i=0; tokenizer.hasMoreTokens() ;i++ ) {
				
				try {
					bufferIntArray[i] = Integer.valueOf( 
							tokenizer.nextToken() );
				}
				catch ( NumberFormatException ne ) {
					bufferIntArray[i] = 0;
					
//					JOptionPane.showMessageDialog( 
//							this.j_Box, 
//							String.format( "ignore value [" 
//									+ buffer + "]") );
				} // end try catch
			} // end for	
			iDataFromGui_int = bufferIntArray;	
			j_ta[TAB_INT].setToolTipText( "Integer  [" + Integer.toString( iCountTokens ) + "]");
			return;
			
		case TAB_FLOAT:
			
			float[] bufferFloatArray = new float[ iCountTokens ];
			
			for (int i=0; tokenizer.hasMoreTokens() ;i++ ) {
				
				try {
					bufferFloatArray[i] = Float.valueOf( 
							tokenizer.nextToken() );
				}
				catch ( NumberFormatException ne ) {
					bufferFloatArray[i] = 0;
					
//					JOptionPane.showMessageDialog( 
//							this.j_Box, 
//							String.format( "ignore value [" 
//									+ buffer + "]") );
				} // end try catch
			
			} // end for
			iDataFromGui_float = bufferFloatArray;		
			j_ta[TAB_FLOAT].setToolTipText( "Float  [" + Integer.toString( iCountTokens ) + "]");
			return;
			
		case TAB_STRING:
			
			String[] bufferStringArray = new String[ iCountTokens ];
			
			for (int i=0; tokenizer.hasMoreTokens() ;i++ ) {
				bufferStringArray[i] = tokenizer.nextToken();
			}
			iDataFromGui_String = bufferStringArray;
			j_ta[TAB_STRING].setToolTipText( "String  [" + Integer.toString( iCountTokens ) + "]");
			return;
			
			default:
				throw new CerberusRuntimeException("ERROR in readBackGui() unknown type");
			
		} // end   switch

	}
	
	/**
	 * updates parameters from the GUI to the IVirtualArray.
	 * 
	 * @param updateSelection
	 */
	public void updateStorageFromGui( IStorage updateStorage ) {
	
		assert updateStorage != null:"updateStorageFromGui() can not handle null-pointer";
		
		readBackGui();
		
		ManagerObjectType selType = updateStorage.getBaseType();
		
		if ( selType == ManagerObjectType.STORAGE_FLAT ) {
			
			updateStorage.setId( iDataFromGui_Id );
			updateStorage.setLabel( this.sDataFromGui_label );
			
			if ( bDataFromGui_Int_HasChanged )
				updateStorage.setArrayInt( this.iDataFromGui_int );
			if ( bDataFromGui_Float_HasChanged)
				updateStorage.setArrayFloat( this.iDataFromGui_float ); 
			if ( bDataFromGui_String_HasChanged )
				updateStorage.setArrayString( this.iDataFromGui_String );
			
			return;
		}
		
		throw new CerberusRuntimeException("ERROR in updateSelectionFromGui() unknown type");
		
	}
	
	/**
	 * Get type that is set current.
	 * 
	 * @return current type used for the Gui.
	 */
	public ManagerObjectType getBaseType() {
		return currentType;
	}

	protected DSwingStorageCanvas getCanvas() {
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
				refStorage = null;
				currentType = ManagerObjectType.STORAGE;
				
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
