package org.caleydo.rcp.views;

import java.awt.Frame;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.media.opengl.GLCanvas;

import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

/**
 * Shared object for all Caleydo viewPart objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AGLViewPart
	extends ViewPart
{

	protected Frame frameGL;
	protected Shell swtShell;
	protected Composite swtComposite;
	protected GLCaleydoCanvas glCanvas;

	protected int iViewID;

	public static final String ACTION_WRITE_SCREENSHOT_TEXT = "Save screenshot";
	public static final String ACTION_WRITE_SCREENSHOT_ICON = "resources/icons/PathwayEditor/back.png";

	private Action actWriteScreenshot;
	private boolean bEnableWriteScreenshot = false;

	/**
	 * Constructor.
	 */
	public AGLViewPart()
	{
		super();
	}

	public void setGLCanvas(final GLCaleydoCanvas glCanvas)
	{
		this.glCanvas = glCanvas;
	}

	public void setViewId(final int iViewID)
	{
		this.iViewID = iViewID;
	}

	protected final void showMessage(String title, String message)
	{

		MessageDialog.openInformation(swtShell, "Info " + title, message);
	}

	protected void createPartControlSWT(Composite parent)
	{
		swtShell = parent.getShell();
		swtComposite = new Composite(parent, SWT.EMBEDDED);

		createWriteScreenshotAction();
	}

	public void createPartControlGL()
	{

		if (frameGL == null)
		{
			frameGL = SWT_AWT.new_Frame(swtComposite);
		}

		frameGL.add(glCanvas);
		// frameGL.addWindowListener(new WindowAdapter() {
		// public void windowClosing(WindowEvent e) {
		//		        	
		// // Run this on another thread than the AWT event queue to
		// // make sure the call to Animator.stop() completes before
		// // exiting
		// new Thread(new Runnable() {
		// public void run() {
		// animatorGL.stop();
		// frameGL.setVisible(false);
		// }
		// }).start();
		// }
		// });
		//		    
		// frameGL.setVisible(true);
		//		    
		// animatorGL.start();
		// }
	}
	@Override
	public final void setFocus()
	{
		// nothing to do at the moment
	}

	@Override
	public void dispose()
	{

		super.dispose();

		// this.setGLCanvasVisible(false);
		//		
		// if ( frameGL != null ) {
		// frameGL.dispose();
		// frameGL = null;
		// }
		//		
		// if ( animatorGL!= null ) {
		// if ( animatorGL.isAnimating() ) {
		// animatorGL.stop();
		// }
		// animatorGL = null;
		// }
	}

	protected void writeScreenshot()
	{

		String sFilePath = "screenshot_" + getDateTime() + ".png";

		GC gc = new GC(swtComposite.getDisplay());
		final Image image = new Image(swtComposite.getDisplay(), swtShell.getBounds());
		gc.copyArea(image, swtShell.getBounds().x, swtShell.getBounds().y);
		gc.dispose();

		FileDialog saveFileDialog = new FileDialog(swtShell, SWT.SAVE);
		saveFileDialog.setFileName(sFilePath);
		sFilePath = saveFileDialog.open();

		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };
		loader.save(sFilePath, SWT.IMAGE_PNG);

		// MessageBox messageBox = new MessageBox(swtShell, SWT.OK);
		// messageBox.setText("Message from SWT");
		// messageBox.setMessage("Screenshot successfully written to " +
		// sFilePath);
		// messageBox.open();
	}

	private String getDateTime()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
		Date date = new Date();
		return dateFormat.format(date);
	}

	protected void fillLocalToolBar(IToolBarManager manager)
	{

		manager.add(actWriteScreenshot);
	}

	private void createWriteScreenshotAction()
	{
		actWriteScreenshot = new Action()
		{
			public void run()
			{

				bEnableWriteScreenshot = !bEnableWriteScreenshot;
				writeScreenshot();
			}
		};
		actWriteScreenshot.setText(ACTION_WRITE_SCREENSHOT_TEXT);
		actWriteScreenshot.setToolTipText(ACTION_WRITE_SCREENSHOT_TEXT);
		actWriteScreenshot.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass()
				.getClassLoader().getResource(ACTION_WRITE_SCREENSHOT_ICON)));
	}
}
