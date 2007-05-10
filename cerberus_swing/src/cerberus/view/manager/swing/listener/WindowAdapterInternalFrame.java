package cerberus.view.manager.swing.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowAdapterInternalFrame extends WindowAdapter {

	public WindowAdapterInternalFrame() {
		
	}
	
	public void windowClosing(WindowEvent e)  {
		System.err.println("Close Internal AWT-Frame...");
	}

}
