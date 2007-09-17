/**
 * 
 */
package org.studierstube.net.protocol.muddleware;

/**
 * Enumeration for different types of operations.
 *  Also used to generate selections.
 *  
 * See Muddleware::common\Operation.h
 * 
 * @author Michael Kalkusch
 *
 */
public enum OperationEnum {

	/* 
	 * Copy of enum in Muddleware::common\Operation.h
	 * 
	 * Note: Maybe use Java-Enum?
	 */
	
	OP_GET_ELEMENT( 0 ,"Get Element" ),

	/**
	 *  Message ID for MUDDLEWARE::addElementAsString()
	 */
	OP_ADD_ELEMENT( 1, "Add Element" ),

	/**
	 *  Message ID for MUDDLEWARE::addElementsAsStrings()
	 */
	OP_ADD_ELEMENTS( 2, "Add Elements" ),

	// watch out !! in file include\Muddleware\Client\Operation.h Remove and Update are switched !!!
	/**
	 * Message ID for MUDDLEWARE::removeElements()
	 */
	OP_REMOVE_ELEMENT( 3, "Remove Element" ),

	/**
	 * Message ID for MUDDLEWARE::updateElement()
	 */
	OP_UPDATE_ELEMENT( 4, "Update Element" ),

	
	/**
	 * Message ID for MUDDLEWARE::getElementAsString()
	 */
	OP_ELEMENT_EXISTS( 5, "Element Exists" ),

	/**
	 *  Message ID for MUDDLEWARE::getAttributeAsString()
	 */
	OP_GET_ATTRIBUTE( 6, "Get Attribute" ),

	/**
	 * Message ID for MUDDLEWARE::updateAttribute()
	 */
	OP_UPDATE_ATTRIBUTE( 7, "Update Attribute" ),

	/**
	 * Adds a new attribute
	 */
	OP_ADD_ATTRIBUTE( 8, "Add Attribute" ),

	/**
	 * Removes an attribute
	 */
	OP_REMOVE_ATTRIBUTE( 9, "Remove Attribute" ),

	/**
	 * Message ID for requesting the connections client ID
	 */
	OP_REQUEST_CLIENTID( 10, "request ClientId" ),

	/**
	 * Message ID for registering an (non-callback) update-messages on modified nodes
	 */
	OP_REGISTER_WATCHDOG( 11, "register Watchdog" ),

	/**
	 * Message ID for unregistering a watchdog
	 */
	OP_UNREGISTER_WATCHDOG( 12, "unregister Watchdog" ),

	/**
	 * Message ID for watchdog reply - only set from server to client
	 */
	OP_WATCHDOG( 13, "Watchdog" ),
	
	/**
	 * Message ID for doing nothing (can be used for getting only update-replies)
	 */
	OP_EMPTY( 14, "no operation" ),

	/**
	 * Message ID for doing nothing (can be used for getting only update-replies)
	 */
	OP_REQUEST_GROUPID ( 15, "request group id" ),

	/**
	 * Message ID for doing nothing (can be used for getting only update-replies)
	 */
	OP_SET_GROUPID ( 16, "set group id" ),

	/**
	 * Message ID for registering a callback channel
	 */
	OP_REGISTER_CALLBACK(17, "register callback"),

//	/**
//	 * Title of the operation type.
//	 */
//	private final String sTitle;
//	
//	/**
//	 * Detail on the operation type.
//	 */
//	private String sDetail;
	
	/**
	  * Message ID for registering callback update-messages on modified nodes
	 */
	OP_REGISTER_WATCHDOG_ASYNC(18, "register async watchdog");
	
	/**
	 * Index of enum.
	 */
	private int iIndex;
	/**
	 * Text to appear inside ComboBox
	 */
	private String sComboBoxTitle;

	/**
	 * Default constructor 
	 * 
	 * @param iIndex index to address this operation
	 * @param sComboBoxName short name for this operation to apprear inside ComboBox
	 */
	private OperationEnum( int iIndex, String sComboBoxName ) {
		this.iIndex = iIndex;
		this.sComboBoxTitle = sComboBoxName;
	}
	
//	private OperationEnum( String sTitle, String sDetail ) {
//		this.sTitle = sTitle;
//		this.sDetail = sDetail;
//	}
//	
//	public String getDetail() {
//		return this.sDetail;
//	}
	
	/**
	 * Get the title for this operation.
	 * 
	 * @return title
	 */
	public String getTitle() {
		return this.name();
	}
	
	/**
	 * Title for this Operation to appear in JComboBox
	 * Note: used by getAllTitleComboBox()
	 * 
	 * @return title for combo box for this enumeration
	 */
	public String getTitleComboBox() {
		return this.sComboBoxTitle;
	}
	
	/**
	 * Get the index of this operation.
	 * @return index
	 */
	public int getIndex() {
//		return this.ordinal();
		return iIndex;
	}
	
	public boolean isExecutableOperation() {
		if ( this.iIndex < OperationEnum.values().length) return true;
		
		return false;
	}
	
	public boolean equals( final OperationEnum test) {
		if ( test.getIndex() == this.iIndex ) return true;
		
		return false;
	}
	
	/**
	 * Get title of OperationEnum by index.
	 * 
	 * @param iIndex index to convert to type
	 * @return naem of OperationEnum, if iIndex was valid else  "__unknown_type__" is returned
	 */
	public static String getNameFromIndex( int iIndex ) {
		OperationEnum[] array = OperationEnum.values(); 
		for( int i=0; i < array.length; i++ ) {
			if ( array[i].ordinal() == iIndex ) {
				return array[i].getTitle();
			}
		}
		
		return "__unknown_type__";		
	}
	
	
	/**
	 * Get number of operations.
	 * 
	 * @return number of operations
	 */
	public static int getNumOperations() {
		return OperationEnum.values().length;
	}
	
	/**
	 * List of all operations used for combo-box.
	 * 
	 * @return list of all names to be palced inside combo box
	 */
	public static String[] getAllTitlesForComboBox() {
		OperationEnum[] array = OperationEnum.values();
		String names[] = new String [array.length];
		
		for ( int i=0; i< array.length; i++ ) {
			names[i] = array[i].getTitleComboBox();
		}
		return names;
	}
	
	public static OperationEnum valueOfInt( int iIndex ) {
		return OperationEnum.valueOf( getNameFromIndex(iIndex) );
	}
	
	public static boolean equal( final OperationEnum first, final OperationEnum second) {
		if ( first.getIndex() == first.iIndex ) return true;
		
		return false;
	}
}
