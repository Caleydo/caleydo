package cerberus.manager.gui;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Shell;

import cerberus.data.IUniqueObject;

public class CerberusWindow 
extends ApplicationWindow
implements IUniqueObject
{
	public CerberusWindow(Shell arg0)
	{
		super(arg0);
	}

	int iUniqueId = 0;

	public void setId(int iUniqueId)
	{
		this.iUniqueId = iUniqueId;
		
	}

	public int getId()
	{
		return iUniqueId;
	}
}
