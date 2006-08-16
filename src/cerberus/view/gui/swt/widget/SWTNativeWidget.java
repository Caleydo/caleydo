/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 * Creation date: 26-07-2006
 *  
 */

package cerberus.view.gui.swt.widget;

import cerberus.view.gui.swt.widget.ASWTWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


/**
 * Class takes a composite in the constructor
 * and provides this composite over the getSWTWidget()
 * method.
 * 
 * @author Marc Streit
 */
public class SWTNativeWidget extends ASWTWidget 
{
	protected Composite refComposite;
	
	/**
	 * Constructor 
	 * 
	 * @param refComposite Reference to the composite 
	 * that is supposed to be filled.
	 */
	public SWTNativeWidget(Composite refParentComposite)
	{
		super(refParentComposite);

		refComposite = new Composite(refParentComposite, SWT.NONE);
	}

	/**
	 * Get the SWT container in which the View can place the content.
	 * 
	 * @return SWT container
	 */
	final public Composite getSWTWidget()
	{
		return refComposite;
	}
}
