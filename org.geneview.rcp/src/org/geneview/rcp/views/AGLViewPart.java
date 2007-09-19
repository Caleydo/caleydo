/**
 * 
 */
package org.geneview.rcp.views;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCanvas;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;
import org.geneview.core.view.jogl.JoglCanvasForwarder;
import org.geneview.rcp.Application;

import com.sun.opengl.util.Animator;

/**
 * Shared object for all GeneView viewPart objects.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AGLViewPart extends ViewPart {

	protected Animator animatorGL;
	protected Frame frameGL;
	protected Shell swtShell;
	protected Composite swtComposit;
	
	/**
	 * 
	 */
	public AGLViewPart() {
		super();
	}
	
	protected final void showMessage(String title,String message) {
		
		MessageDialog.openInformation(swtShell, "Info " + title, message);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	protected void createPartControlSWT(Composite parent) {
		swtShell = parent.getShell();
		swtComposit = new Composite(parent, SWT.EMBEDDED);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	protected void createPartControlGL(Composite parent, final int iGLCanvasDirectorId) {
		
		if ( frameGL==null ) {
			frameGL = SWT_AWT.new_Frame(swtComposit);	
			
			GLCanvas canvasGL= new GLCanvas();
			
			// FIXME: static canvas director ID is a hack.
			JoglCanvasForwarder canvasForwarder = Application.refGeneralManager.getSingelton().getViewGLCanvasManager()
				.getGLCanvasDirector(iGLCanvasDirectorId).getJoglCanvasForwarder();
					
			canvasGL.addGLEventListener(canvasForwarder);
			
			frameGL.add(canvasGL);		
			//frameGL.setSize(300, 300);
			
		    animatorGL = new Animator(canvasGL);
		    
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
		    
			//frameGL.setTitle("Cerberus JFrame");
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
				// not visisble
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

}
