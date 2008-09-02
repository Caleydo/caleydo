package org.caleydo.rcp.views.browser;

import java.net.URL;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.AbstractWebBrowser;

public class WebBrowser
	extends AbstractWebBrowser
{

	/**
	 * Constructor.
	 * 
	 */
	public WebBrowser(String id)
	{
		super(id);
	}

	@Override
	public void openURL(URL url) throws PartInitException
	{

	}

}
//org.eclipse.ui.examples.rcp.browser