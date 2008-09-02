package org.caleydo.rcp.command.handler.view;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class OpenBrowserHandler
	extends AbstractHandler
	implements IHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench()
				.getBrowserSupport();

		try
		{
//			IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.AS_VIEW, 
//					"org.eclipse.ui.internal.browser.DefaultWorkbenchBrowserSupport", "Web Browser", "Web Browser");
			IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.AS_VIEW, 
			"org.caleydo.rcp.browser", "Web Browser", "Web Browser");

			
			browser.openURL(new URL("http://www.google.com"));
		}
		catch (PartInitException e)
		{
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
