package org.geneview.rcp.views;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCanvas;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.part.ViewPart;
import org.geneview.rcp.Application;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.view.rcp.CmdExternalFlagSetter;
import cerberus.command.view.rcp.EExternalFlagSetterType;
import cerberus.view.jogl.JoglCanvasForwarder;

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

public class GLJukeboxPathwayView extends ViewPart {

	public static final String ID = "org.geneview.rcp.views.GLJukeboxPathwayView";
	
	public static final String ACTION_ENABLE_ANIMATOR_ICON = "data/icons/PathwayEditor/animator.png";
	public static final String ACTION_ENABLE_PATHWAY_TEXTURES_TEXT = "Turn on/off pathway textures";
	public static final String ACTION_ENABLE_PATHWAY_TEXTURES_ICON = "data/icons/PathwayEditor/texture_on_off.png";
	public static final String ACTION_ENABLE_GENE_MAPPING_TEXT = "Turn on/off gene mapping";
	public static final String ACTION_ENABLE_GENE_MAPPING_ICON = "data/icons/PathwayEditor/gene_mapping.png";
	public static final String ACTION_ENABLE_NEIGHBORHOOD_TEXT = "Turn on/off neighborhood highlighting";
	public static final String ACTION_ENABLE_NEIGHBORHOOD_ICON = "data/icons/PathwayEditor/three_neighborhood.gif";
	public static final String ACTION_ENABLE_IDENTICAL_NODE_HIGHLIGHTING_TEXT = "Turn on/off identical node highlighting";
	public static final String ACTION_ENABLE_IDENTICAL_NODE_HIGHLIGHTING_ICON = "data/icons/PathwayEditor/identical_node_highlighting.png";
	public static final String ACTION_ENABLE_ANNOTATION_TEXT = "Show/hide annotation";
	public static final String ACTION_ENABLE_ANNOTATION_ICON = "data/icons/PathwayEditor/annotation.png";
	
	private Action actToggleAnimatorRunningState;
	
	private Action actEnableGeneMapping;
	private boolean bEnableGeneMapping = true;
	
	private Action actEnablePathwayTextures;
	private boolean bEnablePathwayTextures = true;
	
	private Action actEnableIdenticalNodeHighlighting;
	private boolean bEnableIdenticalNodeHighlighting = true;
	
	private Action actEnableNeighborhood;
	private boolean bEnableNeighborhood = false;
	
	private Action actEnableAnnotation;
	private boolean bEnableAnnotation = true;
	
	private Animator animatorGL;
	private GLCanvas canvasGL;
	private Frame frameGL;
	private Shell swtShell;
	private Composite swtComposit;

	/**
	 * The constructor.
	 */
	public GLJukeboxPathwayView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {

		swtShell = parent.getShell();
		swtComposit = new Composite(parent, SWT.EMBEDDED);
		
		createAnimatorToggleAction();
		createGeneMappingToggleAction();
		createPathwayTexturesToggleAction();
		createNeighborhoodToggleAction();
		createIdenticalNodeHighlightingAction();
		createAnnotationToggleAction();
		
		contributeToActionBars();


		if ( frameGL==null ) {
			frameGL = SWT_AWT.new_Frame(swtComposit);		
			canvasGL= new GLCanvas();
		}

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
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {		
		manager.add(actToggleAnimatorRunningState);
		manager.add(actEnableGeneMapping);
		manager.add(actEnablePathwayTextures);
		manager.add(actEnableNeighborhood);
		manager.add(actEnableIdenticalNodeHighlighting);
		manager.add(actEnableAnnotation);
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actToggleAnimatorRunningState);
		manager.add(actEnableGeneMapping);
		manager.add(actEnablePathwayTextures);
		manager.add(actEnableNeighborhood);
		manager.add(actEnableIdenticalNodeHighlighting);
		manager.add(actEnableAnnotation);
	}

	private void createAnimatorToggleAction() {
		
		//showMessage("Action 1", "make new action [toggle JOGL frame]");
		
		actToggleAnimatorRunningState = new Action() {
			public void run() {
								
				if ( swtComposit.isVisible() ) {
					/* toggle state */
					setGLCanvasVisible( ! frameGL.isVisible() );
				} //if ( swtComposit.isVisible() ) {
			}
		};
		actToggleAnimatorRunningState.setText("Turn off/on animator");
		actToggleAnimatorRunningState.setToolTipText("Turn off/on animator");
		actToggleAnimatorRunningState.setImageDescriptor(ImageDescriptor.createFromFile(
				null, ACTION_ENABLE_ANIMATOR_ICON));
		
//		showMessage("Action 1","executed toggle JOGL frame");
	}
	
	private void createGeneMappingToggleAction() {
		
		actEnableGeneMapping = new Action() {
			public void run() {
				
				bEnableGeneMapping = !bEnableGeneMapping;
				triggerCmdSExternalFlagSetter(bEnableGeneMapping, 
						EExternalFlagSetterType.PATHWAY_ENABLE_GENE_MAPPING);	
			}
		};
		
		actEnableGeneMapping.setText(ACTION_ENABLE_GENE_MAPPING_TEXT);
		actEnableGeneMapping.setToolTipText(ACTION_ENABLE_GENE_MAPPING_TEXT);
		actEnableGeneMapping.setImageDescriptor(ImageDescriptor.createFromFile(
				null, ACTION_ENABLE_GENE_MAPPING_ICON));
	}
	
	private void createPathwayTexturesToggleAction() {
		
		actEnablePathwayTextures = new Action() {
			public void run() {
				
				bEnablePathwayTextures = !bEnablePathwayTextures;
				triggerCmdSExternalFlagSetter(bEnablePathwayTextures, 
						EExternalFlagSetterType.PATHWAY_ENABLE_TEXTURES);	
			}
		};
		
		actEnablePathwayTextures.setText(ACTION_ENABLE_PATHWAY_TEXTURES_TEXT);
		actEnablePathwayTextures.setToolTipText(ACTION_ENABLE_PATHWAY_TEXTURES_TEXT);
		actEnablePathwayTextures.setImageDescriptor(ImageDescriptor.createFromFile(
				null, ACTION_ENABLE_PATHWAY_TEXTURES_ICON));	}

	private void createNeighborhoodToggleAction() {
		
		actEnableNeighborhood = new Action() {
			public void run() {
				
				bEnableNeighborhood = !bEnableNeighborhood;
				triggerCmdSExternalFlagSetter(bEnableNeighborhood, 
						EExternalFlagSetterType.PATHWAY_ENABLE_NEIGHBORHOOD);	
			}
		};
		
		actEnableNeighborhood.setText(ACTION_ENABLE_NEIGHBORHOOD_TEXT);
		actEnableNeighborhood.setToolTipText(ACTION_ENABLE_NEIGHBORHOOD_TEXT);
		actEnableNeighborhood.setImageDescriptor(ImageDescriptor.createFromFile(
				null, ACTION_ENABLE_NEIGHBORHOOD_ICON));
	}
	
	private void createIdenticalNodeHighlightingAction() {
		
		actEnableIdenticalNodeHighlighting = new Action() {
			public void run() {
				
				bEnableIdenticalNodeHighlighting = !bEnableIdenticalNodeHighlighting;
				triggerCmdSExternalFlagSetter(bEnableIdenticalNodeHighlighting, 
						EExternalFlagSetterType.PATHWAY_ENABLE_IDENTICAL_NODE_HIGHLIGHTING);	
			}
		};
		
		actEnableIdenticalNodeHighlighting.setText(ACTION_ENABLE_IDENTICAL_NODE_HIGHLIGHTING_TEXT);
		actEnableIdenticalNodeHighlighting.setToolTipText(ACTION_ENABLE_IDENTICAL_NODE_HIGHLIGHTING_TEXT);
		actEnableIdenticalNodeHighlighting.setImageDescriptor(ImageDescriptor.createFromFile(
				null, ACTION_ENABLE_IDENTICAL_NODE_HIGHLIGHTING_ICON));
	}
	
	private void createAnnotationToggleAction() {
		
		actEnableAnnotation = new Action() {
			public void run() {
				
				bEnableAnnotation = !bEnableAnnotation;
				triggerCmdSExternalFlagSetter(bEnableAnnotation, 
						EExternalFlagSetterType.PATHWAY_ENABLE_ANNOTATION);	
			}
		};
		
		actEnableAnnotation.setText(ACTION_ENABLE_ANNOTATION_TEXT);
		actEnableAnnotation.setToolTipText(ACTION_ENABLE_ANNOTATION_TEXT);
		actEnableAnnotation.setImageDescriptor(ImageDescriptor.createFromFile(
				null, ACTION_ENABLE_ANNOTATION_ICON));
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
	
	private void showMessage(String title,String message) {
		
		MessageDialog.openInformation(swtShell, "Info " + title, message);
	}
	
	public void triggerCmdSExternalFlagSetter(final boolean bFlag, EExternalFlagSetterType type) {
		
		CmdExternalFlagSetter tmpCmd = 
			(CmdExternalFlagSetter)Application.refGeneralManager.getSingelton()
				.getCommandManager().createCommandByType(CommandQueueSaxType.EXTERNAL_FLAG_SETTER);

		tmpCmd.setAttributes(82401, bFlag, type);
		tmpCmd.doCommand();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		
	}
}