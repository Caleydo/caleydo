/**
 * 
 */
package cerberus.view.gui;

import java.util.Vector;
import java.util.Iterator;

import cerberus.data.AUniqueManagedObject;
import cerberus.manager.IGeneralManager;

/**
 * @author kalkusch
 *
 */
public abstract class AViewManagedRep 
extends AUniqueManagedObject
implements IView
{

	protected Vector <String> vecAttributes = null;
	
	/**
	 * 
	 */
	public AViewManagedRep( final int iSetCollectionId, 
			final IGeneralManager setGeneralManager)
	{
		super( iSetCollectionId, setGeneralManager );
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
		
	}

}
