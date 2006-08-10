/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 * Creation date: 26-07-2006
 *  
 */
package cerberus.view.gui.swt.widget;

import org.eclipse.swt.widgets.Composite;

abstract public class ASWTWidget 
{
	/**
	 * Composite in which the content of the View should be placed.
	 */
	protected final Composite refComposite;
	
	/**
	 * Constructor that takes the composite in which it should 
	 * place the content.
	 * 
	 * @param refComposite Reference to the composite 
	 * that is supposed to be filled.
	 */
	public ASWTWidget(Composite refComposite)
	{
		this.refComposite = refComposite;
	}
}
