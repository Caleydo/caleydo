package cerberus.view.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

import cerberus.view.gui.Widget;

public class SWTEmbeddedGraphWidget extends Widget 
{
	Composite refSWTComposite = null;
	java.awt.Frame embeddedFrame = null;
	
	public SWTEmbeddedGraphWidget(Composite parentComposite)
	{
		refSWTComposite = new Composite(parentComposite, SWT.EMBEDDED);
		embeddedFrame = SWT_AWT.new_Frame(refSWTComposite);
	}

	public java.awt.Frame getEmbeddedFrame()
	{
		return embeddedFrame;
	}
}
