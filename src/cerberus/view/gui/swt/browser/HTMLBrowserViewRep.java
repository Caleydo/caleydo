/**
 * 
 */
package cerberus.view.gui.swt.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
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
    
    protected String sUrl = "www.google.at";
	
	public HTMLBrowserViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);	
	}

	public void initView() {
		
		refBrowser = new Browser (refSWTContainer, SWT.NONE);
		refBrowser.setBounds(5, 75, 1000, 400);
		
	    ToolBar toolbar = new ToolBar(refSWTContainer, SWT.NONE);
	    toolbar.setBounds(5, 5, 200, 30);

	    ToolItem goButton = new ToolItem(toolbar, SWT.PUSH);
	    goButton.setText("Go");

	    ToolItem backButton = new ToolItem(toolbar, SWT.PUSH);
	    backButton.setText("Back");

	    ToolItem stopButton = new ToolItem(toolbar, SWT.PUSH);
	    stopButton.setText("Stop");

	    final Text text = new Text(refSWTContainer, SWT.BORDER);
		text.setBounds(5, 35, 400, 25);

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
					drawView();
					//sUrl = text.getText();
			}
		};

		goButton.addListener(SWT.Selection, listener);
		backButton.addListener(SWT.Selection, listener);
		stopButton.addListener(SWT.Selection, listener);

		text.addListener(SWT.DefaultSelection, new Listener()
		{
			public void handleEvent(Event e)
			{
				sUrl = text.getText();
				drawView();
			}
		});
	}

	public void drawView() {
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg(
				this.getClass().getSimpleName() + 
				": drawView(): Load "+sUrl, 
				LoggerType.VERBOSE );
		
		refBrowser.setUrl(sUrl);
	}

	public void retrieveGUIContainer() {
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}

	public void extractAttributes() {
		
	}
	
	public void setUrl(String sUrl) {
		
		this.sUrl = sUrl;
//		drawView();
	}
}
