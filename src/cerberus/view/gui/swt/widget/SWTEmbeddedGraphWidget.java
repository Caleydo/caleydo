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
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * Class takes a composite in the constructor and
 * embedds an AWT Frame in it.
 * The Frame can be retrieved over the getEmbeddedFrame()
 * method.
 * 
 * @author Marc Streit
 */
public class SWTEmbeddedGraphWidget extends ASWTWidget 
{
	/**
	 * Embedded AWT Frame.
	 */
	protected final java.awt.Frame refEmbeddedFrame;
	
	/**
	 * Constructor that takes the composite in which it should 
	 * place the content and creates an embedded AWT frame.
	 * 
	 * @param Composite Reference to the composite 
	 * that is supposed to be filled.
	 */
	public SWTEmbeddedGraphWidget(Composite refParentComposite, int iWidth, int iHeight)
	{
		super(refParentComposite);
		
		Composite composite = new Composite(refParentComposite, SWT.EMBEDDED);
		
		GridData gridData = new GridData();
		
		if (iWidth != -1)
		{
			gridData.widthHint = iWidth;
			gridData.horizontalAlignment = GridData.CENTER;
		}
		else
		{
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
		}
		
		if (iHeight != -1)
		{
			gridData.heightHint = iHeight;
			gridData.verticalAlignment = GridData.CENTER;
		}
		else
		{
			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessVerticalSpace = true;
		}
		
		composite.setLayoutData(gridData);
		
		refEmbeddedFrame = SWT_AWT.new_Frame(composite);
	}

	/**
	 * Get the embedded frame.
	 * 
	 * @return The embedded AWT Frame.
	 */
	public final java.awt.Frame getEmbeddedFrame()
	{
		return refEmbeddedFrame;
	}
}
