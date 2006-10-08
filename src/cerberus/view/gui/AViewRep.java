package cerberus.view.gui;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import cerberus.data.AUniqueManagedObject;
import cerberus.manager.IGeneralManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.system.StringConversionTool;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Abstract class that is the base of all view representations.
 * It holds the the own view ID, the parent ID and the attributes that
 * needs to be processed.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AViewRep 
extends AUniqueManagedObject
implements IView, IMediatorSender, IMediatorReceiver
{
	
	protected int iParentContainerId;
	
	protected String sLabel;
	
	protected Vector <String> vecAttributes;

	protected IParameterHandler refParameterHandler;
	
	/**
	 * Width of the widget.
	 */
	protected int iWidth;
	
	/**
	 * Height of the widget;
	 */
	protected int iHeight;

	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param iViewId
	 * @param iParentContainerId
	 * @param sLabel
	 */
	public AViewRep(
			final IGeneralManager refGeneralManager, 
			final int iViewId, 
			final int iParentContainerId, 
			final String sLabel)
	{	
		super ( iViewId, refGeneralManager );
		
		this.iParentContainerId = iParentContainerId;
		this.sLabel = sLabel;
	}
	
	/**
	 * Get one attribute by its index.
	 * If the index in invalid "" is returned. 
	 *  
	 * @return attribute bound to index or "" if index is invalid
	 */
	protected final String getAttributeByIndex( final int iIndex )
	{
		try {
			return vecAttributes.get( iIndex );
		}
		catch (ArrayIndexOutOfBoundsException ae) 
		{
			return "";
		}
	}
	
//	/**
//	 * Get one attribute by its index assuning that it is a integer.
//	 * If the index in invalid -1 is returned. 
//	 *  
//	 * @return attribute bound to index as (int) or -1 if index is invalid
//	 */
//	protected final int getAttributeByIndexToInteger( final int iIndex )
//	{
//		try {
//			return Integer.valueOf( vecAttributes.get( iIndex ) );
//		}
//		catch ( NumberFormatException nfe ) 
//		{
//			/**
//			 * From String to Int conversion
//			 */
//			return -1;
//		}
//		catch ( ArrayIndexOutOfBoundsException ae ) 
//		{
//			return -1;
//		}
//		
//	}
	
	/**
	 * Get one attribute by its index assuning that it is a integer.
	 * If the index in invalid -1 is returned. 
	 *  
	 * @return attribute bound to index as (int) or -1 if index is invalid
	 */
	protected final int getAttributeByIndexToInteger( final int iIndex )
	{
		try {
			return Integer.valueOf( vecAttributes.get( iIndex ) );
		}
		catch ( NumberFormatException nfe ) 
		{
			/**
			 * From String to Int conversion
			 */
			return -1;
		}
		catch ( ArrayIndexOutOfBoundsException ae ) 
		{
			return -1;
		}
		
		//throw new RuntimeException("AViewRep.setAttributes(Vector <String> attributes ) must not be called any more!");
	}
	

/**
	 * Set attributes for this view.
	 * Overwrite previous attributes.
	 * 
	 * @see cerberus.view.gui.IView#setAttributes(java.util.Vector)
	 */
	public void setAttributes( final Vector<String> attributes)
	{ 
		vecAttributes = attributes;
	}

	/**
	 * Extracts the height and the width of the widget from the attributes.
	 *
	 * @deprecated
	 */
	public void extractAttributes()
	{
		int [] iParseResult = 
			StringConversionTool.convertStringToIntArray( vecAttributes.get(2), 2 );
		
		iWidth = iParseResult[0];
		iHeight = iParseResult[1];
	}

	/**
	 * Set attributes for this view.
	 * Extracts the height and the width of the widget from the attributes.
	 * 
	 * @see cerberus.view.gui.IView#setAttributes(java.util.Vector)
	 */
	public void readInAttributes( final IParameterHandler refParameterHandler )
	{ 
		this.refParameterHandler = refParameterHandler;
		
		iWidth = 
			refParameterHandler.getValueInt( CommandQueueSaxType.TAG_POS_WIDTH_X.getXmlKey() );
		iHeight = 
			refParameterHandler.getValueInt( CommandQueueSaxType.TAG_POS_HEIGHT_Y.getXmlKey() );
	}
	
	
	public final ManagerObjectType getBaseType() {
		return null;
	}
	

/**
	 * Get a copy of the current attributes.
	 *  
	 * @return copy of the current attributes
	 */
	protected final Vector<String> getAttributes()
	{
		Vector <String> cloneVecAttributes = 
			new Vector <String> ( vecAttributes.size() );
				
		Iterator <String> iter = vecAttributes.iterator();
		
		while ( iter.hasNext() ) {
			cloneVecAttributes.addElement( iter.next() );
		}
		
		return cloneVecAttributes;
	}
	
	/**
	 * Get a copy of the current attributes.
	 *  
	 * @return copy of the current attributes
	 */
	protected final IParameterHandler getAttributesParameterHandler()
	{
		return refParameterHandler;
	}
	
	/**
	 * Sets the unique ID of the parent container.
	 * Normally it is already set in the constructor.
	 * Use this method only if you want to change the parent during runtime.
	 * 
	 * @param iParentContainerId
	 */
	public void setParentContainerId(int iParentContainerId)
	{
		this.iParentContainerId = iParentContainerId;
	}
	
	/**
	 * TODO: implement in subclasses
	 */
	public void update( Object eventTrigger )
	{
		
	}

}
