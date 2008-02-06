/**
 * 
 */
package org.geneview.rcp.views;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.media.opengl.GLCanvas;

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
import org.geneview.core.view.jogl.JoglCanvasForwarder;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

/**
 * Shared object for all GeneView viewPart objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class AGLViewPart 
extends ViewPart {

	protected Animator animatorGL;
	protected Frame frameGL;
	protected Shell swtShell;
	protected Composite swtComposite;
	protected JoglCanvasForwarder canvasForwarder;
	protected GLCanvas glCanvas;
	
	public static final String ACTION_WRITE_SCREENSHOT_TEXT = "Save screenshot";
	public static final String ACTION_WRITE_SCREENSHOT_ICON = "resources/icons/PathwayEditor/back.png";
	
	private Action actWriteScreenshot;
	private boolean bEnableWriteScreenshot = false;
	
	/**
	 * 
	 */
	public AGLViewPart() {
		super();
	}
	
	public void setCanvasForwader(final JoglCanvasForwarder canvasForwarder) {
		this.canvasForwarder = canvasForwarder;
	}
	
	protected final void showMessage(String title,String message) {
		
		MessageDialog.openInformation(swtShell, "Info " + title, message);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	protected void createPartControlSWT(Composite parent) {
		swtShell = parent.getShell();
		swtComposite = new Composite(parent, SWT.EMBEDDED);
		
		createWriteScreenshotAction();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControlGL() {
		
		if ( frameGL==null ) {
			frameGL = SWT_AWT.new_Frame(swtComposite);	
			
			glCanvas = new GLCanvas();					
			glCanvas.addGLEventListener(canvasForwarder);
			
			frameGL.add(glCanvas);					
		    animatorGL = new FPSAnimator(glCanvas, 60); // restricts maximum FPS to about 40 in reality
		 
		    frameGL.addWindowListener(new WindowAdapter() {
		        public void windowClosing(WindowEvent e) {
		        	
		          // Run this on another thread than the AWT event queue to
		          // make sure the call to Animator.stop() completes before
		          // exiting
		          new Thread(new Runnable() {
		              public void run() {
		                animatorGL.stop();
		                frameGL.setVisible(false);
		              }
		            }).start();
		        }
		      });
		    
		    frameGL.setVisible(true);
		    
		    animatorGL.start();
		}
	}

	protected final void setGLCanvasVisible( boolean visible) {
		if (( frameGL == null)||( animatorGL== null )) {
			return;			
		}
		
		if ( visible != frameGL.isVisible() ) {
			/* state change for GL canvas */			
			frameGL.setVisible(visible);
			
			/* animatorGL */			
			if ( visible ) {	
				// is visible
				//showMessage("Info - Action 1", "enable AWT frame, restart animator");		
				if ( !animatorGL.isAnimating() ) {
					animatorGL.start();
				}				
			} else {
				// not visible
				//showMessage("Info - Action 1", "disable AWT frame, stop animator");	
				if ( animatorGL.isAnimating() ) {
					animatorGL.stop();
				}	
			}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose() {
		
		super.dispose();
		
		this.setGLCanvasVisible(false);
		
		if ( frameGL != null ) {
			frameGL.dispose();
			frameGL = null;
		}
		
		if ( animatorGL!= null ) {
			if ( animatorGL.isAnimating() ) {
				animatorGL.stop();				
			}
			animatorGL = null;
		}
	}

	protected void writeScreenshot() {
		
		String sFilePath = "screenshot_" +getDateTime() +".png";
		
	    GC gc = new GC(swtComposite.getDisplay());
		final Image image = new Image(swtComposite.getDisplay(), swtShell.getBounds());
		gc.copyArea(image, swtShell.getBounds(). x,swtShell.getBounds().y);
		gc.dispose();
		
	    FileDialog saveFileDialog = new FileDialog(swtShell, SWT.SAVE);
	    saveFileDialog.setFileName(sFilePath);
	    sFilePath = saveFileDialog.open();
		
	    ImageLoader loader = new ImageLoader();
	    loader.data = new ImageData[] {image.getImageData()};
	    loader.save(sFilePath, SWT.IMAGE_PNG);
	    
//        MessageBox messageBox = new MessageBox(swtShell, SWT.OK);
//        messageBox.setText("Message from SWT");
//        messageBox.setMessage("Screenshot successfully written to " + sFilePath);
//        messageBox.open();	
	}
	
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
        Date date = new Date();
        return dateFormat.format(date);
    }
	
	protected void fillLocalToolBar(IToolBarManager manager) {
	
		manager.add(actWriteScreenshot);
	}
	
	private void createWriteScreenshotAction() {

		// showMessage("Action 1", "make new action [toggle JOGL frame]");

		actWriteScreenshot = new Action() {
			public void run() {

				bEnableWriteScreenshot = !bEnableWriteScreenshot;
				writeScreenshot();
			}
		};
		actWriteScreenshot.setText(ACTION_WRITE_SCREENSHOT_TEXT);
		actWriteScreenshot.setToolTipText(ACTION_WRITE_SCREENSHOT_TEXT);
		actWriteScreenshot.setImageDescriptor(ImageDescriptor
				.createFromURL(this.getClass().getClassLoader().getResource(
						ACTION_WRITE_SCREENSHOT_ICON)));

		// showMessage("Action 1","executed toggle JOGL frame");
	}
}
