/**
 * 
 */
package cerberus.view.swt.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;


/**
 * @author michael
 *
 */
public class HookedBrowser extends Browser {

	/**
	 * @param parent
	 * @param style
	 */
	public HookedBrowser(Composite parent, int style) {

		super(parent, style | SWT.NO_FOCUS );
		
		System.err.println("HookedBrowser( parent=[" + parent.toString() + "] , style=[" + style + "] )");
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
		
		System.err.println("HookedBrowser.refresh()");
		super.refresh();		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#stop()
	 */
	public void stop() {
		
		System.err.println("HookedBrowser.stop()");
		super.stop();		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#setUrl(java.lang.String)
	 */
	public boolean setUrl(String url) {
		
		System.err.println("HookedBrowser.setUrl(" +  url + ")");
		return super.setUrl(url);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#setText(java.lang.String)
	 */
	public boolean setText(String html) {
		
		System.err.println("HookedBrowser.setText(" +  html + ")");
		return super.setText(html);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#getUrl()
	 */
	public String getUrl() {
		
		String info = super.getUrl();
		System.err.println("HookedBrowser.getUrl() => " + info);
		
		return info;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.browser.Browser#execute(java.lang.String)
	 */
	public boolean execute(String script) {
		
		System.err.println("HookedBrowser.execute(" + script+ ")");
		
		return super.execute(script);		
	}

}
