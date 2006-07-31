package cerberus.view.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import cerberus.view.gui.Widget;

public class SWTEmbeddedGraphWidget extends Widget 
{
//	protected Composite refSWTComposite;
	protected java.awt.Frame refEmbeddedFrame;
	
	public SWTEmbeddedGraphWidget(Composite refComposite)
	{
		//FIXME: this is only a realy shitty workaround to make the frame larger
		Button ok = new Button (refComposite, SWT.PUSH);
		ok.setText ("OK");
		ok.setSize(400, 300);
		
		refEmbeddedFrame = SWT_AWT.new_Frame(refComposite);
	}

	public java.awt.Frame getEmbeddedFrame()
	{
		return refEmbeddedFrame;
	}
}
