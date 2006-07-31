package cerberus.view.gui.swt.widget;

import javax.media.opengl.GLCanvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import cerberus.view.gui.Widget;

public class SWTEmbeddedJoglWidget extends Widget 
{
	protected GLCanvas refGLCanvas;
	protected java.awt.Frame refEmbeddedFrame;
	
	public SWTEmbeddedJoglWidget(Composite refComposite)
	{
		//FIXME: this is only a realy shitty workaround to make the frame larger
		Button ok = new Button (refComposite, SWT.PUSH);
		ok.setText ("OK");
		ok.setSize(400, 300);
		
		refEmbeddedFrame = SWT_AWT.new_Frame(refComposite);
		refGLCanvas = new GLCanvas();
		refEmbeddedFrame.add(refGLCanvas);
	}
	
	public GLCanvas getGLCanvas()
	{
		return refGLCanvas;
	}
}
