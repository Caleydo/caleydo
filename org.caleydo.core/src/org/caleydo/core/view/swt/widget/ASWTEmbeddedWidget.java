package org.caleydo.core.view.swt.widget;

import java.awt.Frame;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ASWTEmbeddedWidget
	extends ASWTWidget
{

	/**
	 * Embedded AWT Frame.
	 */
	protected Frame embeddedFrame;

	protected Composite embeddedFrameComposite;

	protected final Composite parentComposite;

	/**
	 * @param parentComposite
	 */
	public ASWTEmbeddedWidget(Composite parentComposite)
	{

		super(parentComposite);

		this.parentComposite = parentComposite;

		embeddedFrame = null;
		embeddedFrameComposite = null;
	}

	public void createEmbeddedComposite()
	{

		embeddedFrameComposite = new Composite(parentComposite, SWT.EMBEDDED);

		GridData gridData = new GridData();

		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;

		embeddedFrameComposite.setLayoutData(gridData);

		embeddedFrame = SWT_AWT.new_Frame(embeddedFrameComposite);
	}

	/**
	 * Get the embedded frame composite.
	 * 
	 * @return The embedded composite.
	 */
	public final Composite getEmbeddedFrameComposite()
	{

		return embeddedFrameComposite;
	}

	/**
	 * Get parent composite.
	 * 
	 * @return The parent composite.
	 */
	public final Composite getParentComposite()
	{

		return parentComposite;
	}

}
