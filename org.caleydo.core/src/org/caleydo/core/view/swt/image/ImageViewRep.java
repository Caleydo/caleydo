package org.caleydo.core.view.swt.image;

import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

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

	@Override
	protected void initViewSwtComposite(Composite swtContainer)
	{

		Image image = new Image(swtContainer.getDisplay(), sImagePath);

		Label label = new Label(swtContainer, SWT.BORDER);
		label.setImage(image);
	}

	@Override
	public void drawView()
	{

	}

	public void setAttributes(String sImagePath)
	{
		this.sImagePath = sImagePath;
	}
}
