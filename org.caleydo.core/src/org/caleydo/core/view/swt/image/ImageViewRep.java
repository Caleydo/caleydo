/**
 * 
 */
package org.geneview.core.view.swt.image;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.view.AViewRep;
import org.geneview.core.view.IView;
import org.geneview.core.view.ViewType;

/**
 * Image view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class ImageViewRep 
extends AViewRep 
implements IView {
	
	protected String sImagePath;
	
	public ImageViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_IMAGE_VIEWER);	
	}

	/**
	 * 
	 * @see org.geneview.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer) {
		
		Image image = new Image(refSWTContainer.getDisplay(), sImagePath);

		Label label = new Label(refSWTContainer, SWT.BORDER);
		label.setSize(iWidth, iHeight);
	    label.setImage(image);
	}

	public void drawView() {
		
//		refGeneralManager.getSingelton().logMsg(
//				this.getClass().getSimpleName() + 
//				": drawView(): Load "+sUrl, 
//				LoggerType.VERBOSE );		
	}
	
	public void setAttributes(int iWidth, int iHeight, String sImagePath) {
		
		super.setAttributes(iWidth, iHeight);
		
		this.sImagePath = sImagePath;
	}
}
