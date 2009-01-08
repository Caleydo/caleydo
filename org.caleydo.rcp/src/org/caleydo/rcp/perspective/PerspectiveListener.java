package org.caleydo.rcp.perspective;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;

public class PerspectiveListener
	extends PerspectiveAdapter
{

	/**
	 * Constructor.
	 */
	public PerspectiveListener()
	{
		super();
	}

	@Override
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective,
			String changeId)
	{
		System.out.println("perspectiveChanged()");
		System.out.println(page.getClass().toString());
		System.out.println(perspective.toString());
		System.out.println(changeId);

		super.perspectiveChanged(page, perspective, changeId);

//		MessageDialog.openQuestion(null, "test", "please, dont close me! ok?");
	}

}
