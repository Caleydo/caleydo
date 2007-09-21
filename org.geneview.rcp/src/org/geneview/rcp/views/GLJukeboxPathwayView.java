package org.geneview.rcp.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.geneview.rcp.Application;
import org.geneview.rcp.views.AGLViewPart;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.view.rcp.CmdExternalFlagSetter;
import org.geneview.core.command.view.rcp.EExternalFlagSetterType;

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

public class GLJukeboxPathwayView 
extends AGLViewPart {

	public static final String ID = "org.geneview.rcp.views.GLJukeboxPathwayView";
	
	public static final String ACTION_ENABLE_ANIMATOR_ICON = "resources/icons/PathwayEditor/animator.png";
	public static final String ACTION_ENABLE_PATHWAY_TEXTURES_TEXT = "Turn on/off pathway textures";
	public static final String ACTION_ENABLE_PATHWAY_TEXTURES_ICON = "resources/icons/PathwayEditor/texture_on_off.png";
	public static final String ACTION_ENABLE_GENE_MAPPING_TEXT = "Turn on/off gene mapping";
	public static final String ACTION_ENABLE_GENE_MAPPING_ICON = "resources/icons/PathwayEditor/gene_mapping.png";
	public static final String ACTION_ENABLE_NEIGHBORHOOD_TEXT = "Turn on/off neighborhood highlighting";
	public static final String ACTION_ENABLE_NEIGHBORHOOD_ICON = "resources/icons/PathwayEditor/three_neighborhood.gif";
	public static final String ACTION_ENABLE_IDENTICAL_NODE_HIGHLIGHTING_TEXT = "Turn on/off identical node highlighting";
	public static final String ACTION_ENABLE_IDENTICAL_NODE_HIGHLIGHTING_ICON = "resources/icons/PathwayEditor/identical_node_highlighting.png";
	public static final String ACTION_ENABLE_ANNOTATION_TEXT = "Show/hide annotation";
	public static final String ACTION_ENABLE_ANNOTATION_ICON = "resources/icons/PathwayEditor/annotation.png";
	
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

	/**
	 * The constructor.
	 */
	public GLJukeboxPathwayView() {
		super();
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {

		super.createPartControlSWT(parent);
		
		createAnimatorToggleAction();
		createGeneMappingToggleAction();
		createPathwayTexturesToggleAction();
		createNeighborhoodToggleAction();
		createIdenticalNodeHighlightingAction();
		createAnnotationToggleAction();
		
		contributeToActionBars();

		//super.createPartControlGL();	
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
								
				if ( swtComposite.isVisible() ) {
					/* toggle state */
					setGLCanvasVisible( ! frameGL.isVisible() );
				} //if ( swtComposite.isVisible() ) {
			}
		};
		actToggleAnimatorRunningState.setText("Turn off/on animator");
		actToggleAnimatorRunningState.setToolTipText("Turn off/on animator");
		actToggleAnimatorRunningState.setImageDescriptor(ImageDescriptor.createFromURL(
				this.getClass().getClassLoader().getResource(ACTION_ENABLE_ANIMATOR_ICON)));
		
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
		actEnableGeneMapping.setImageDescriptor(ImageDescriptor.createFromURL(
				this.getClass().getClassLoader().getResource(ACTION_ENABLE_GENE_MAPPING_ICON)));
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
		actEnablePathwayTextures.setImageDescriptor(ImageDescriptor.createFromURL(
				this.getClass().getClassLoader().getResource(ACTION_ENABLE_PATHWAY_TEXTURES_ICON)));
	}

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
		actEnableNeighborhood.setImageDescriptor(ImageDescriptor.createFromURL(
				this.getClass().getClassLoader().getResource(ACTION_ENABLE_NEIGHBORHOOD_ICON)));
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
		actEnableIdenticalNodeHighlighting.setImageDescriptor(ImageDescriptor.createFromURL(
				this.getClass().getClassLoader().getResource(ACTION_ENABLE_IDENTICAL_NODE_HIGHLIGHTING_ICON)));
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
		actEnableAnnotation.setImageDescriptor(ImageDescriptor.createFromURL(
				this.getClass().getClassLoader().getResource(ACTION_ENABLE_ANNOTATION_ICON)));
	}
	
	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
		
		super.dispose();
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