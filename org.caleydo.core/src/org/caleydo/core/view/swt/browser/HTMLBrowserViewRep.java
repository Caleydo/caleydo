/**
 * 
 */
package org.caleydo.core.view.swt.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.data.CmdDataCreateSelectionSetMakro;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.view.AViewRep;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;

/**
 * Simple HTML browser.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class HTMLBrowserViewRep 
extends AViewRep 
implements IView {

	public EBrowserType browserType;
	
	public static String CALEYDO_HOME = "http://www.caleydo.org";
	
    protected HookedBrowser refBrowser;
    
    protected String sUrl = CALEYDO_HOME;
    
    protected Text refTextField;
    
    protected int iSelectionSetId;
    
    private IDExtractionLocationListener idExtractionLocationListener;
	
	public HTMLBrowserViewRep(
			final IGeneralManager refGeneralManager, 
			final int iViewId, 
			final int iParentContainerId, 
			final String sLabel) {
		
		super(refGeneralManager,
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_HTML_BROWSER);	
		
		// Default browser type
		this.browserType = EBrowserType.GENERAL;
		
		iSelectionSetId = refGeneralManager.getSingleton().getSetManager()
			.createId(ManagerObjectType.SET_LINEAR);
		
		CmdDataCreateSelectionSetMakro selectedSetCmd = (CmdDataCreateSelectionSetMakro) refGeneralManager.getSingleton().getCommandManager()
			.createCommandByType(CommandQueueSaxType.CREATE_SET_SELECTION_MAKRO);
		
		selectedSetCmd.setAttributes(iSelectionSetId);
		selectedSetCmd.doCommand();
	}

	
	public void setAttributes(int iWidth, int iHeight, EBrowserType browserType) {
		
		super.setAttributes(iWidth, iHeight);
		
		this.browserType = browserType;
	}
	
	/**
	 * 
	 * @see org.caleydo.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer) {
		
		refSWTContainer = swtContainer;
		refSWTContainer.setLayout(new GridLayout(1, false));
		
	    ToolBar toolbar = new ToolBar(refSWTContainer, SWT.NONE);
	    toolbar.setBounds(0, 0, 300, 30);

	    ToolItem goButton = new ToolItem(toolbar, SWT.PUSH);
	    goButton.setText("Go");

	    ToolItem backButton = new ToolItem(toolbar, SWT.PUSH);
	    backButton.setText("Back");

	    ToolItem stopButton = new ToolItem(toolbar, SWT.PUSH);
	    stopButton.setText("Stop");

	    refTextField = new Text(refSWTContainer, SWT.BORDER);
	    //refTextField.setBounds(0, 30, 300, 25);
	    refTextField.setText(sUrl);
	    
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		refTextField.setLayoutData(data);
	    
		Listener listener = new Listener() {
			public void handleEvent(Event event)
			{
				ToolItem item = (ToolItem) event.widget;
				String string = item.getText();
				if (string.equals("Back"))
					refBrowser.back();
				else if (string.equals("Stop"))
					refBrowser.stop();
				else if (string.equals("Go"))
				{
					sUrl = refTextField.getText();
					drawView();
				}
			}
		};

		goButton.addListener(SWT.Selection, listener);
		backButton.addListener(SWT.Selection, listener);
		stopButton.addListener(SWT.Selection, listener);

		refTextField.addListener(SWT.DefaultSelection, new Listener()
		{
			public void handleEvent(Event e)
			{
				sUrl = refTextField.getText();
				drawView();
			}
		});
		
		refSWTContainer.getDisplay().addFilter(SWT.FocusIn, new Listener() {
		    public void handleEvent(Event event) {
		    	
		        if(!(event.widget.getClass().equals(this.getClass()))) 
		        	return;
		    }
		});
		
		
		refBrowser = new HookedBrowser (
				refSWTContainer, 
				SWT.NONE, generalManager);
				
		idExtractionLocationListener = new IDExtractionLocationListener(generalManager,
				refBrowser, iUniqueId, iSelectionSetId);
		refBrowser.addLocationListener(idExtractionLocationListener);
		
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		refBrowser.setLayoutData(data);
	}

	public void drawView() {
		
		generalManager.getSingleton().logMsg(
				this.getClass().getSimpleName() + 
				": drawView(): Load "+sUrl, 
				LoggerType.VERBOSE );

//		// Check internet connection
//		try
//		{
//			InetAddress.getByName("www.google.at");
//			
//		} catch (UnknownHostException e)
//		{
//			refGeneralManager.getSingelton().logMsg(
//					this.getClass().getSimpleName() + 
//					": No internet connection found!", 
//					LoggerType.VERBOSE );
//			
//			refTextField.setText("No internet connection found!");
//			return;
//		}
		
		try {
			refSWTContainer.getDisplay().asyncExec(new Runnable() {
				public void run() {
					refTextField.setText(sUrl);				
					refBrowser.setUrl(sUrl);
					//refBrowser.refresh();
				}
			});
		}
			catch (SWTException swte) 
		{
				generalManager.getSingleton().logMsg(
						this.getClass().getSimpleName() + 
						": error while setURL ["+sUrl + "]", 
						LoggerType.STATUS );
		}
	}
	
	public void setUrl(String sUrl) {
		
		if (browserType.equals(EBrowserType.GENERAL))
		{
			this.sUrl = sUrl;
		}
		else if(browserType.equals(EBrowserType.PUBMED))
		{
			this.sUrl = browserType.getBrowserQueryStringPrefix() +sUrl;
		}
		
		idExtractionLocationListener.updateSkipNextChangeEvent(true);
		drawView();
	}
	
//	public void setUrlByBrowserQueryType(String sUrl,
//			final EBrowserQueryType browserQueryType) {
//		
//		this.sUrl = browserQueryType.getBrowserQueryStringPrefix() + sUrl;
//	}
}
