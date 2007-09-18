/**
 * 
 */
package org.geneview.core.view.swt.widget;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
//import org.eclipse.swt.layout.FillLayout;
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
	
//	protected final int iWidth;
//	
//	protected final int iHeight;
	
	/**
	 * @param refParentComposite
	 */
	public ASWTEmbeddedWidget(Composite refParentComposite) {
		
		super(refParentComposite);
		
		this.refParentComposite = refParentComposite;
		
		refEmbeddedFrame = null;
		refEmbeddedFrameComposite = null;
	}

	public void createEmbeddedComposite() {
		
		refEmbeddedFrameComposite = 
			new Composite(refParentComposite, SWT.EMBEDDED);
			
		GridData gridData = new GridData();
		
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		
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
