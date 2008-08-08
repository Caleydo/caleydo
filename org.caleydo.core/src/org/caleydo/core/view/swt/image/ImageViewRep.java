package org.caleydo.core.view.swt.image;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;

/**
 * Image view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class ImageViewRep
	extends AView
	implements IView
{
	protected String sImagePath;

	/**
	 * Constructor.
	 * 
	 */
	public ImageViewRep(int iParentContainerId, String sLabel)
	{
		super(iParentContainerId, sLabel, ViewType.SWT_IMAGE_VIEWER);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.AView#initViewSwtComposit(org.eclipse.swt.widgets.Composite)
	 */
	protected void initViewSwtComposite(Composite swtContainer)
	{

		Image image = new Image(swtContainer.getDisplay(), sImagePath);

		Label label = new Label(swtContainer, SWT.BORDER);
		label.setSize(iWidth, iHeight);
		label.setImage(image);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.IView#drawView()
	 */
	public void drawView()
	{

	}

	public void setAttributes(int iWidth, int iHeight, String sImagePath)
	{
		super.setAttributes(iWidth, iHeight);

		this.sImagePath = sImagePath;
	}
}
