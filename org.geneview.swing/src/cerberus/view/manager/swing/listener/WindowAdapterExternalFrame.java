package cerberus.view.manager.swing.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import cerberus.view.manager.swing.listener.IWindowAdapterTarget;

public class WindowAdapterExternalFrame extends WindowAdapter {

	private final IWindowAdapterTarget target;
	
	public WindowAdapterExternalFrame(IWindowAdapterTarget target) {
		this.target = target;
	}
	
	public void windowClosing(WindowEvent e)  {
		System.err.println("Close external AWT-Frame...");
		target.windowClosingAction();
	}

}
