/**
 * 
 */
package org.geneview.core.view.swt.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;

/**
 * @author michael
 *
 */
public class HookedBrowser extends Browser {

	private final IGeneralManager refGeneralManager;
	
	/**
	 * @param parent
	 * @param style
	 */
	public HookedBrowser(Composite parent, 
			int style,
			final IGeneralManager refGeneralManager ) {

		super(parent, style | SWT.NO_FOCUS );
		this.refGeneralManager = refGeneralManager;
		
		refGeneralManager.getSingelton().logMsg(
				"HookedBrowser( parent=[" + parent.toString() + "] , style=[" + style + "] ) Constructor",
				LoggerType.VERBOSE);
	}
	
	/**
	 * need to implement to enable subclass from SWT object.
	 * 
	 * @see org.eclipse.swt.browser.Browser#checkSubclass()
	 */
	protected void checkSubclass() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#refresh()
	 */
	public void refresh() {
		
		refGeneralManager.getSingelton().logMsg("HookedBrowser.refresh()",
				LoggerType.VERBOSE);
		super.refresh();		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#stop()
	 */
	public void stop() {
		
		refGeneralManager.getSingelton().logMsg("HookedBrowser.stop()",
				LoggerType.VERBOSE);
		super.stop();		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#setUrl(java.lang.String)
	 */
	public boolean setUrl(String url) {
		
		refGeneralManager.getSingelton().logMsg("HookedBrowser.setUrl(" +  url + ")",
				LoggerType.VERBOSE);
		return super.setUrl(url);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#setText(java.lang.String)
	 */
	public boolean setText(String html) {
		
		refGeneralManager.getSingelton().logMsg("HookedBrowser.setText(" +  html + ")",
				LoggerType.VERBOSE);
		return super.setText(html);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#getUrl()
	 */
	public String getUrl() {
		
		String info = super.getUrl();
		refGeneralManager.getSingelton().logMsg("HookedBrowser.getUrl() => " + info,
				LoggerType.VERBOSE);
		
		return info;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#execute(java.lang.String)
	 */
	public boolean execute(String script) {
		
		refGeneralManager.getSingelton().logMsg("HookedBrowser.execute(" + script+ ")",
				LoggerType.VERBOSE);
		
		return super.execute(script);		
	}

}
