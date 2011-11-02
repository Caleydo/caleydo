package org.caleydo.core.view.contextmenu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;


/**
 * Demonstrates the workaround for displaying a SWT popup menu 
 * over swing components under GTK (menu not displayed / visible bug)
 * 
 * @author Samuel Thiriot, INRIA
 *
 */
public class PopupOverAwtHelper {

	private Display swtDisplay;
	
	private Menu swtPopupMenu;
	
	private final static int MAX_RETRIES = 10;
	
	public PopupOverAwtHelper(Menu swtPopupMenu) {
		this.swtPopupMenu = swtPopupMenu;
		swtDisplay = swtPopupMenu.getDisplay();
	}
	
    /**
     * Workaround: due to a GTK problem (Linux and other Unix), 
     * popup menus are not always displayed. This tries several 
     * times to display it. 
     * 
     * see
     * http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg33992.html
     * http://www.eclipsezone.com/eclipse/forums/t95687.html
     * @param menu
     * @param retriesRemaining
     */
    protected void retryVisible(final int retriesRemaining) {
    	
    	swtDisplay.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				
				if (swtPopupMenu.isVisible()) {
					System.out.println("made visible after "+(MAX_RETRIES-retriesRemaining)+" attempts");
					
				} else if (retriesRemaining > 0) {
					
					System.out.println("retrying (remains "+(retriesRemaining-1)+")");
					
					//swtHost.getShell().forceFocus();
					//swtHost.getShell().forceActive();
					//menu.setVisible(false);
					swtPopupMenu.setVisible(false);
					
					{
						Shell shell = new Shell(swtDisplay, 
								SWT.APPLICATION_MODAL | // should lead the window manager to switch another window to the front
								SWT.DIALOG_TRIM	// not displayed into taskbars nor in task managers 
								);
						shell.setSize(10, 10); // big enough to avoid errors from the gtk layer
						shell.setBackground(swtDisplay.getSystemColor(SWT.COLOR_RED));
						shell.setText("Not visible");
						shell.setVisible(false);
						shell.open();
						shell.dispose();
					}
					swtPopupMenu.getShell().forceActive();
					
					//forceFocus();
					//forceActive();
					swtPopupMenu.setVisible(true);
					
					retryVisible(retriesRemaining-1);
					
				} else {
					System.err.println("unable to display the menu, sorry :-(");
					
				}
					
			}
		});
    }
    
    protected void swtDirectShowMenu(int x, int y) {
    	
		if (swtDisplay.isDisposed())	 // possible quick exit
    		return;
		
		swtPopupMenu.setLocation(new Point(x, y));
    	
		System.out.println("Displaying the menu at coordinates "+x+","+y);
		swtPopupMenu.setVisible(true);
		
		// if GUI not based on GTK, the menu should already be displayed. 
		
		retryVisible(MAX_RETRIES); // but just in case, we ensure this is the case :-)
    }
}