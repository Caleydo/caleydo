package org.caleydo.core.data.filter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class AFilterRepresentation {

	private Shell popupShell;
	
	public AFilterRepresentation() {
		popupShell = new Shell();
		
		popupShell.setLayout(new RowLayout());
	    final Button ok = new Button(popupShell, SWT.PUSH);
	    ok.setText("Apply");
	    Button cancel = new Button(popupShell, SWT.PUSH);
	    cancel.setText("Cancel");
	    Listener listener = new Listener() {
	      public void handleEvent(Event event) {
//	        result[0] = event.widget == ok;
	        popupShell.close();
	      }
	    };
	    ok.addListener(SWT.Selection, listener);
	    cancel.addListener(SWT.Selection, listener);
		popupShell.pack();
		
		Monitor primary = popupShell.getDisplay().getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = popupShell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		popupShell.setLocation (x, y);
	}
	
	public void open(){
		
		popupShell.open();	
	}
}
