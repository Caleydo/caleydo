package org.caleydo.core.view.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * Class takes a composite in the constructor and provides this composite over
 * the getSWTWidget() method.
 * 
 * @author Marc Streit
 */
public class SWTNativeWidget
	extends ASWTWidget
{

	protected Composite composite;

	/**
	 * Constructor
	 * 
	 * @param parent Reference to the composite that is supposed to be filled.
	 */
	public SWTNativeWidget(Composite parentComposite)
	{

		super(parentComposite);

		composite = new Composite(parentComposite, SWT.NONE);

		GridData gridData = new GridData();

		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;

		composite.setLayoutData(gridData);
	}

	/**
	 * Get the SWT container in which the View can place the content.
	 * 
	 * @return SWT container
	 */
	final public Composite getSWTWidget()
	{

		return composite;
	}
}
