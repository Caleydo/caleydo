/**
 * 
 */
package cerberus.view.swt.browser;

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

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.AViewRep;
import cerberus.view.IView;
import cerberus.view.ViewType;
import cerberus.view.swt.browser.HookedBrowser;
import cerberus.view.swt.widget.SWTEmbeddedGraphWidget;

/**
 * Simple HTML browser.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class HTMLBrowserViewRep 
extends AViewRep 
implements IView {

    protected HookedBrowser refBrowser;
    
    protected String sUrl = "";
    
    protected Text refTextField;
	
	public HTMLBrowserViewRep(
			final IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager,
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_HTML_BROWSER);	
	}

	
	/**
	 * 
	 * @see cerberus.view.IView#initView()
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
		    	
		        if(!(event.widget.getClass().getName().equals(this.getClass().getName()))) 
		        	return;
		    }
		});
		
		
		refBrowser = new HookedBrowser (
				refSWTContainer, 
				SWT.NONE, refGeneralManager);
		
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		refBrowser.setLayoutData(data);
	}

	public void drawView() {
		
		refGeneralManager.getSingelton().logMsg(
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
				refGeneralManager.getSingelton().logMsg(
						this.getClass().getSimpleName() + 
						": error while setURL ["+sUrl + "]", 
						LoggerType.STATUS );
		}
	}
	
	public void setUrl(String sUrl) {
		
		this.sUrl = sUrl;
		drawView();
	}
}
