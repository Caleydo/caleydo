package org.caleydo.core.view.swt.browser;

import java.util.logging.Level;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Simple HTML browser.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class HTMLBrowserViewRep
	extends AView
	implements IView
{
	public EBrowserType browserType;

	public final static String CALEYDO_HOME = "http://www.caleydo.org";

	protected Browser browser;

	protected String sUrl = CALEYDO_HOME;

	protected Text textField;

//	protected int iSelectionSetId;

	private IDExtractionLocationListener idExtractionLocationListener;

	/**
	 * Constructor.
	 */
	public HTMLBrowserViewRep(final int iParentContainerId, final String sLabel)
	{
		super(iParentContainerId, sLabel, ViewType.SWT_HTML_BROWSER);

		// Default browser type
		this.browserType = EBrowserType.GENERAL;
		
//		CmdDataCreateSelection selectedSetCmd = (CmdDataCreateSelection) generalManager
//				.getCommandManager().createCommandByType(CommandType.CREATE_SELECTION);
//
//		selectedSetCmd.doCommand();
	}

	public void setAttributes(EBrowserType browserType)
	{
		this.browserType = browserType;
	}

	@Override
	protected void initViewSwtComposite(Composite swtContainer)
	{
		swtContainer.setLayout(new GridLayout(1, false));

		ToolBar toolbar = new ToolBar(swtContainer, SWT.NONE);
		toolbar.setBounds(0, 0, 300, 30);

		ToolItem goButton = new ToolItem(toolbar, SWT.PUSH);
		goButton.setText("Go");

		ToolItem backButton = new ToolItem(toolbar, SWT.PUSH);
		backButton.setText("Back");

		ToolItem stopButton = new ToolItem(toolbar, SWT.PUSH);
		stopButton.setText("Stop");

		textField = new Text(swtContainer, SWT.BORDER);
		// textField.setBounds(0, 30, 300, 25);
		textField.setText(sUrl);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		textField.setLayoutData(data);

		Listener listener = new Listener()
		{

			public void handleEvent(Event event)
			{

				ToolItem item = (ToolItem) event.widget;
				String string = item.getText();
				if (string.equals("Back"))
					browser.back();
				else if (string.equals("Stop"))
					browser.stop();
				else if (string.equals("Go"))
				{
					sUrl = textField.getText();
					drawView();
				}
			}
		};

		goButton.addListener(SWT.Selection, listener);
		backButton.addListener(SWT.Selection, listener);
		stopButton.addListener(SWT.Selection, listener);

		textField.addListener(SWT.DefaultSelection, new Listener()
		{

			public void handleEvent(Event e)
			{

				sUrl = textField.getText();
				drawView();
			}
		});

		swtContainer.getDisplay().addFilter(SWT.FocusIn, new Listener()
		{

			public void handleEvent(Event event)
			{

				if (!(event.widget.getClass().equals(this.getClass())))
					return;
			}
		});

		browser = new Browser(swtContainer, SWT.NONE);

		idExtractionLocationListener = new IDExtractionLocationListener(
				browser, iUniqueID, -1);
		browser.addLocationListener(idExtractionLocationListener);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		browser.setLayoutData(data);
	}

	public void drawView()
	{
		generalManager.getLogger().log(Level.INFO, "Load " + sUrl);

		// // Check internet connection
		// try
		// {
		// InetAddress.getByName("www.google.at");
		//			
		// } catch (UnknownHostException e)
		// {
		// generalManager.getSingelton().logMsg(
		// this.getClass().getSimpleName() +
		// ": No internet connection found!",
		// LoggerType.VERBOSE );
		//			
		// textField.setText("No internet connection found!");
		// return;
		// }

		try
		{
			swtContainer.getDisplay().asyncExec(new Runnable()
			{

				public void run()
				{

					textField.setText(sUrl);
					browser.setUrl(sUrl);
					// browser.refresh();
				}
			});
		}
		catch (SWTException swte)
		{
			generalManager.getLogger().log(Level.SEVERE, "Error while loading " + sUrl);
		}
	}

	public void setUrl(String sUrl)
	{

		if (browserType.equals(EBrowserType.GENERAL))
		{
			this.sUrl = sUrl;
		}
		else if (browserType.equals(EBrowserType.PUBMED))
		{
			this.sUrl = browserType.getBrowserQueryStringPrefix() + sUrl;
		}

		idExtractionLocationListener.updateSkipNextChangeEvent(true);
		drawView();
	}

	// public void setUrlByBrowserQueryType(String sUrl,
	// final EBrowserQueryType browserQueryType) {
	//		
	// this.sUrl = browserQueryType.getBrowserQueryStringPrefix() + sUrl;
	// }
}
