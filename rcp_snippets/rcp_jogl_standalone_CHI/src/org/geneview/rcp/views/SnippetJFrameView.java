package org.geneview.rcp.views;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCanvas;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.geneview.rcp.Application;
import org.geneview.rcp.views.awt.Gears;

import cerberus.view.jogl.JoglCanvasForwarder;
import cerberus.view.jogl.JoglCanvasForwarderType;
import cerberus.view.opengl.RcpCanvasDirector;
import cerberus.view.opengl.canvas.GLCanvasTestTriangle;
import cerberus.view.opengl.canvas.pathway.GLCanvasJukeboxPathway3D;

import com.sun.opengl.util.Animator;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SnippetJFrameView extends ViewPart {

	public static final String ID = "org.geneview.rcp.views.SnippetJFrameView";
	
	private Action action1;
	//private Action doubleClickAction;

	private Animator animatorGL;
	private GLCanvas canvasGL;
	private Frame frameGL;
	private Shell swtShell;
	private Composite swtComposit;
	

	
	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return new String[] { "One", "Two", "Three" };
		}
	}
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public SnippetJFrameView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {

		swtShell = parent.getShell();
		swtComposit = new Composite(parent, SWT.EMBEDDED);
		
		makeActionToggle();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();


		if ( frameGL==null ) {
			frameGL = SWT_AWT.new_Frame(swtComposit);		
			canvasGL= new GLCanvas();
		}

		//canvasGL.addGLEventListener(new Gears());
	    				
//		RcpCanvasDirector rcpCanvasDirector = new RcpCanvasDirector(24, 25,
//				Application.refGeneralManager, JoglCanvasForwarderType.ONLY_2D_FORWARDER);
//
//		JoglCanvasForwarder canvasForwarder = rcpCanvasDirector.getJoglCanvasForwarder();
//		
//		canvasForwarder.addGLCanvasUser(
//				(new GLCanvasTestTriangle(Application.refGeneralManager, 0, 24, "")));

		// FIXME: static canvas director ID is a hack.
		JoglCanvasForwarder canvasForwarder = Application.refGeneralManager.getSingelton().getViewGLCanvasManager()
			.getGLCanvasDirector(24).getJoglCanvasForwarder();
		
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

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SnippetJFrameView.this.fillContextMenu(manager);
			}
		});
//		Menu menu = menuMgr.createContextMenu(viewer.getControl());
//		viewer.getControl().setMenu(menu);
//		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {		
		manager.add(action1);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
	}

	private void setGLCanvasVisible( boolean visible) {
		if (( frameGL == null)||( animatorGL== null )) {
			return;			
		}
		
		if ( visible != frameGL.isVisible() ) {
			/* state change for GL canvas */			
			frameGL.setVisible(visible);
			
			/* animatorGL */			
			if ( visible ) {	
				// is visible
				showMessage("Info - Action 1", "enable AWT frame, restart animator");		
				if ( !animatorGL.isAnimating() ) {
					animatorGL.start();
				}				
			} else {
				// not visisble
				showMessage("Info - Action 1", "disable AWT frame, stop animator");	
				if ( animatorGL.isAnimating() ) {
					animatorGL.stop();
				}	
			}
			
		}
	}
	
	private void makeActionToggle() {
		
		showMessage("Action 1", "make new action [toggle JOGL frame]");
		
		action1 = new Action() {
			public void run() {
				
				if ( swtComposit.isVisible() ) {
					/* toggle state */
					setGLCanvasVisible( ! frameGL.isVisible() );
				} //if ( swtComposit.isVisible() ) {
				
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		showMessage("Action 1","executed toggle JOGL frame");
		
//		doubleClickAction = new Action() {
//			public void run() {
//				ISelection selection = viewer.getSelection();
//				Object obj = ((IStructuredSelection)selection).getFirstElement();
//				showMessage("Double-click detected on "+obj.toString());
//			}
//		};
	}

	private void hookDoubleClickAction() {
//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				doubleClickAction.run();
//			}
//		});
		
		showMessage( "Info", "hookDoubleClickAction");
	}
	private void showMessage(String title,String message) {
		
		MessageDialog.openInformation(swtShell, "Info " + title, message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		
	}
	
	
	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
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