/**
 * 
 */
package cerberus.view.gui.swt.widget;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author kalkusch
 *
 */
public abstract class ASWTEmbeddedWidget extends ASWTWidget
{

	/**
	 * Embedded AWT Frame.
	 */
	protected final Frame refEmbeddedFrame;
	
	protected final Composite refEmbeddedFrameComposite;
	
	/**
	 * @param refParentComposite
	 */
	public ASWTEmbeddedWidget(Composite refParentComposite,
			final int iWidth, 
			final int iHeight )
	{
		super(refParentComposite);
		
		refEmbeddedFrameComposite = 
			new Composite(refParentComposite, SWT.EMBEDDED);
		
		refEmbeddedFrameComposite.setBounds(0, 0, iWidth, iHeight);
		
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
		
		refEmbeddedFrameComposite.setLayoutData(gridData);
	
		refEmbeddedFrame = SWT_AWT.new_Frame(refEmbeddedFrameComposite);
	}

}
