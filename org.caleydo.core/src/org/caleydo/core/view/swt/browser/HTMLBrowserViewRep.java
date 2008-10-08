package org.caleydo.core.view.swt.browser;

import java.util.logging.Level;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Simple HTML browser.
 * 
 * @author Marc Streit
 */
public class HTMLBrowserViewRep
	extends AView
	implements IView
{
	public final static String CALEYDO_HOME = "http://www.caleydo.org";

	protected Browser browser;

	protected String sUrl = CALEYDO_HOME;

	protected Text textURL;

	protected IDExtractionLocationListener idExtractionLocationListener;

	/**
	 * Constructor.
	 */
	public HTMLBrowserViewRep(final int iParentContainerId, final String sLabel)
	{
		super(iParentContainerId, sLabel, ViewType.SWT_HTML_BROWSER);
	}
	
	@Override
	protected void initViewSwtComposite(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL,GridData.FILL,true,true));
		
		Composite browserBarComposite = new Composite(composite, SWT.NONE);
		browserBarComposite.setLayout(new GridLayout(2, false));
		
		ToolBar toolbar = new ToolBar(browserBarComposite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_VERTICAL);
		toolbar.setLayoutData(data);
		
		ToolItem goButton = new ToolItem(toolbar, SWT.PUSH);
		goButton.setImage(new Image(parent.getDisplay(), 
				"resources/icons/view/browser/refresh.png"));
//		goButton.setText("Go");

		ToolItem backButton = new ToolItem(toolbar, SWT.PUSH);
		backButton.setImage(new Image(parent.getDisplay(), 
			"resources/icons/view/browser/back.png"));
//		backButton.setText("Back");

		ToolItem stopButton = new ToolItem(toolbar, SWT.PUSH);
		stopButton.setImage(new Image(parent.getDisplay(), 
			"resources/icons/view/browser/stop.png"));
//		stopButton.setText("Stop");

		textURL = new Text(browserBarComposite, SWT.BORDER);
		textURL.setText(sUrl);
		data = new GridData(GridData.FILL_BOTH);
		textURL.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.heightHint = 45;
		browserBarComposite.setLayoutData(data);

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
					sUrl = textURL.getText();
					drawView();
				}
			}
		};

		goButton.addListener(SWT.Selection, listener);
		backButton.addListener(SWT.Selection, listener);
		stopButton.addListener(SWT.Selection, listener);

		textURL.addListener(SWT.DefaultSelection, new Listener()
		{

			public void handleEvent(Event e)
			{

				sUrl = textURL.getText();
				drawView();
			}
		});

		parent.getDisplay().addFilter(SWT.FocusIn, new Listener()
		{

			public void handleEvent(Event event)
			{

				if (!(event.widget.getClass().equals(this.getClass())))
					return;
			}
		});

		browser = new Browser(composite, SWT.NONE);

		idExtractionLocationListener = new IDExtractionLocationListener(browser, iUniqueID, -1);
		browser.addLocationListener(idExtractionLocationListener);

		data = new GridData();
		browser.setLayoutData(new GridData(GridData.FILL,GridData.FILL,true,true));
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
			parent.getDisplay().asyncExec(new Runnable()
			{

				public void run()
				{

					textURL.setText(sUrl);
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
		this.sUrl = sUrl;

		idExtractionLocationListener.updateSkipNextChangeEvent(true);
		drawView();
	}
}
