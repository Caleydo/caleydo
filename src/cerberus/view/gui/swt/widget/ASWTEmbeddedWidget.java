/**
 * 
 */
package cerberus.view.gui.swt.widget;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ASWTEmbeddedWidget 
extends ASWTWidget {

	/**
	 * Embedded AWT Frame.
	 */
	protected Frame refEmbeddedFrame;
	
	protected Composite refEmbeddedFrameComposite;
	
	protected final Composite refParentComposite;
	
	protected final int iWidth;
	
	protected final int iHeight;
	
	/**
	 * @param refParentComposite
	 */
	public ASWTEmbeddedWidget(Composite refParentComposite,
			final int iWidth, 
			final int iHeight ) {
		
		super(refParentComposite);
		
		this.refParentComposite = refParentComposite;
		this.iWidth = iWidth;
		this.iHeight = iHeight;
		
		refEmbeddedFrame = null;
		refEmbeddedFrameComposite = null;
	}

	public void createEmbeddedComposite() {
		
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
	
	/**
	 * Get the embedded frame composite.
	 * 
	 * @return The embedded composite.
	 */
	public final Composite getEmbeddedFrameComposite() {
		
		return refEmbeddedFrameComposite;
	}	
	
	/**
	 * Get parent composite.
	 * 
	 * @return The parent composite.
	 */
	public final Composite getParentComposite() {
		
		return refParentComposite;
	}	
	
}
