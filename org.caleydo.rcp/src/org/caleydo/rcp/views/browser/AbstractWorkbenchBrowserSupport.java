package org.caleydo.rcp.views.browser;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;

public class AbstractWorkbenchBrowserSupport
	extends org.eclipse.ui.browser.AbstractWorkbenchBrowserSupport
{

	public AbstractWorkbenchBrowserSupport()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public IWebBrowser createBrowser(String browserId) throws PartInitException
	{
		return null;
	}

	@Override
	public IWebBrowser createBrowser(int style, String browserId, String name, String tooltip)
			throws PartInitException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
