package cerberus.view.gui;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;

import cerberus.manager.IGeneralManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.util.system.StringConversionTool;

/**
 * Abstract class that is the base of all view representations.
 * It holds the the own view ID, the parent ID and the attributes that
 * needs to be processed.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AViewRep implements IView
{
	protected IGeneralManager refGeneralManager;
	
	protected final int iViewId;
	
	protected int iParentContainerId;
	
	protected String sLabel;
	
	protected Vector <String> vecAttributes;
	
	/**
	 * Width of the widget.
	 */
	protected int iWidth;
	
	/**
	 * Height of the widget;
	 */
	protected int iHeight;

	public AViewRep(
			final IGeneralManager refGeneralManager, 
			final int iViewId, 
			final int iParentContainerId, 
			final String sLabel)
	{	
		this.refGeneralManager = refGeneralManager;
		this.iViewId = iViewId;
		this.iParentContainerId = iParentContainerId;
		this.sLabel = sLabel;
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
	 * Sets the unique ID of the parent container.
	 * Normally it is already set in the constructor.
	 * Use this method only if you want to change the parent during runtime.
	 */
	public void setParentContainerId(int iParentContainerId)
	{
		this.iParentContainerId = iParentContainerId;
	}
	
	/**
	 * Extracts the height and the width of the widget from the attributes.
	 *
	 */
	public final void extractAttributes()
	{
		StringTokenizer token = new StringTokenizer(vecAttributes.get(0),
				CommandFactory.sDelimiter_CreateView_Size);

		iWidth = (StringConversionTool.convertStringToInt(
				token.nextToken(), -1));
		iHeight = (StringConversionTool.convertStringToInt(token
				.nextToken(), -1));

	}
}
