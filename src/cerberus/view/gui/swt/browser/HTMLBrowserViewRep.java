/**
 * 
 */
package cerberus.view.gui.swt.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.ViewType;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

/**
 * Simple HTML browser.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class HTMLBrowserViewRep 
extends AViewRep 
implements IView {
	
	protected Composite refSWTContainer;

    protected Browser refBrowser;
    
    protected String sUrl = "";
    
    protected Text refTextField;
	
	public HTMLBrowserViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager,
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_HTML_BROWSER);	
	}

	public void initView() {
		
		if (iWidth == -1)
			iWidth = 500;
		
		if (iHeight == -1)
			iHeight = 1000;
		
		refBrowser = new Browser (refSWTContainer, SWT.NONE);
		refBrowser.setBounds(5, 75, iWidth, iHeight);
		
	    ToolBar toolbar = new ToolBar(refSWTContainer, SWT.NONE);
	    toolbar.setBounds(0, 0, 300, 30);

	    ToolItem goButton = new ToolItem(toolbar, SWT.PUSH);
	    goButton.setText("Go");

	    ToolItem backButton = new ToolItem(toolbar, SWT.PUSH);
	    backButton.setText("Back");

	    ToolItem stopButton = new ToolItem(toolbar, SWT.PUSH);
	    stopButton.setText("Stop");

	    refTextField = new Text(refSWTContainer, SWT.BORDER);
	    refTextField.setBounds(0, 35, iWidth, 25);
	    refTextField.setText(sUrl);

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
			refTextField.setText(sUrl);				
			refBrowser.setUrl(sUrl);
			//refBrowser.refresh();
		} 
			catch (SWTException swte) 
		{
				refGeneralManager.getSingelton().logMsg(
						this.getClass().getSimpleName() + 
						": error while setURL ["+sUrl + "]", 
						LoggerType.STATUS );
		}
	}

	public void retrieveGUIContainer() {
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}
	
	public void setUrl(String sUrl) {
		
		this.sUrl = sUrl;
		drawView();
	}
}
