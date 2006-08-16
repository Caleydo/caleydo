/**
 * 
 */
package cerberus.view.gui;

import java.util.Vector;
import java.util.Iterator;

/**
 * @author kalkusch
 *
 */
public abstract class AViewRep implements IView
{
	protected int iParentContainerId;
	
	protected Vector <String> vecAttributes = null;

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
	
	public void setParentContainerId(int iParentContainerId)
	{
		this.iParentContainerId = iParentContainerId;
	}
}
