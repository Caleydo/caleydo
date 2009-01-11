package org.caleydo.core.view.swt.image;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.ISWTView;
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
	extends ASWTView
	implements ISWTView
{
	protected String sImagePath;

	/**
	 * Constructor.
	 * 
	 */
	public ImageViewRep(int iParentContainerId, String sLabel)
	{
		super(iParentContainerId, sLabel, GeneralManager.get().getIDManager().createID(
				EManagedObjectType.VIEW_SWT_IMAGE));
	}

	@Override
	public void initViewSWTComposite(Composite parentComposite)
	{
		Image image = new Image(parentComposite.getDisplay(), sImagePath);

		Label label = new Label(parentComposite, SWT.BORDER);
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
