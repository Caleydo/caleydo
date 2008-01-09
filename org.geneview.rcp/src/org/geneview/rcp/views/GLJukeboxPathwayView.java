package org.geneview.rcp.views;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.geneview.rcp.Application;
import org.geneview.rcp.action.search.SearchBox;
import org.geneview.rcp.views.AGLViewPart;
import org.geneview.util.graph.EGraphItemHierarchy;
import org.geneview.util.graph.IGraph;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.view.rcp.CmdExternalActionTrigger;
import org.geneview.core.command.view.rcp.CmdExternalFlagSetter;
import org.geneview.core.command.view.rcp.EExternalActionType;
import org.geneview.core.command.view.rcp.EExternalFlagSetterType;
import org.geneview.core.data.graph.core.PathwayGraph;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class GLJukeboxPathwayView extends AGLViewPart {

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
	public static final String ACTION_CLEAR_ALL_PATHWAYS_TEXT = "Clear all pathways";
	public static final String ACTION_CLEAR_ALL_PATHWAY_ICON = "resources/icons/PathwayEditor/back.png";

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

	private Action actClearAllPathways;

	/**
	 * The constructor.
	 */
	public GLJukeboxPathwayView() {
		super();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {

		super.createPartControlSWT(parent);

		createAnimatorToggleAction();
		createGeneMappingToggleAction();
		createPathwayTexturesToggleAction();
		createNeighborhoodToggleAction();
		createIdenticalNodeHighlightingAction();
		createAnnotationToggleAction();
		createClearAllPathwaysAction();

		contributeToActionBars();

		// super.createPartControlGL();
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
		manager.add(actClearAllPathways);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		
		
		IContributionItem comboCI = new ControlContribution("Quick search") {

			private SearchBox searchBox;

			protected Control createControl(Composite parent) {

				Composite composite = new Composite(parent, SWT.NONE);
				RowLayout rowLayout = new RowLayout();
				rowLayout.fill = true;
				rowLayout.justify = true;
				// rowLayout.pack = false;
				rowLayout.type = SWT.VERTICAL;
				// rowLayout.wrap = false;
				composite.setLayout(rowLayout);
				
				Composite searchInputComposite = new Composite(composite,
						SWT.NONE);
				searchInputComposite.setLayout(new GridLayout(2, true));

				Label searchInputLabel = new Label(searchInputComposite,
						SWT.NULL);
				searchInputLabel.setText("Quick search:");

				searchBox = new SearchBox(searchInputComposite, SWT.NONE);
				
				String items[] = { "No pathways available!" };
				searchBox.setItems(items);
				
				searchBox.addFocusListener(new FocusAdapter() {
			        public void focusGained(FocusEvent e) {
						
						List<IGraph> lLoadedGraphs = Application.geneview_core
						.getGeneralManager().getSingelton()
							.getPathwayManager().getRootPathway()
								.getAllGraphByType(EGraphItemHierarchy.GRAPH_CHILDREN);
			
						String[] sArSearchItems = new String[lLoadedGraphs.size()];
						PathwayGraph tmpPathwayGraph;
						for (int iGraphIndex = 0; iGraphIndex < lLoadedGraphs.size(); iGraphIndex++) 
						{
							tmpPathwayGraph = ((PathwayGraph)lLoadedGraphs.get(iGraphIndex));
							
							sArSearchItems[iGraphIndex] = tmpPathwayGraph.toString() 
								+ " (" + tmpPathwayGraph.getType().toString() +")";
						}
		
						searchBox.setItems(sArSearchItems);
						searchBox.removeFocusListener(this);
					}
				});
				
//				searchBox.addKeyListener(new KeyAdapter() {
//			        public void keyPressed(KeyEvent event) {
//			          switch (event.keyCode) {
//			          case SWT.CR:
//			          {
//					    	 String sSearchEntity = searchBox.getItem(searchBox.getSelectionIndex());
//					    	 sSearchEntity = sSearchEntity.substring(0, sSearchEntity.indexOf(" ("));
//					    	 
//					    	 boolean bFound = Application.geneview_core.getGeneralManager()
//								.getSingelton().getViewGLCanvasManager()
//									.getDataEntitySearcher().searchForEntity(sSearchEntity);
//		
//			          }
//			          case SWT.ESC:
//			            System.out.println(SWT.ESC);
//			            break;
//			          }
//			        }
//			      });
				
				searchBox.addSelectionListener(new SelectionAdapter() {
				     public void widgetSelected(SelectionEvent e) {
				    	 
				    	 String sSearchEntity = searchBox.getItem(searchBox.getSelectionIndex());
				    	 sSearchEntity = sSearchEntity.substring(0, sSearchEntity.indexOf(" ("));
				    	 
				    	 boolean bFound = Application.geneview_core.getGeneralManager()
							.getSingelton().getViewGLCanvasManager()
								.getDataEntitySearcher().searchForEntity(sSearchEntity);
				     }
				});
				
				composite.pack();
				return composite;
			}
		};
		
		manager.add(comboCI);
		
		manager.add(actToggleAnimatorRunningState);
		manager.add(actEnableGeneMapping);
		manager.add(actEnablePathwayTextures);
		manager.add(actEnableNeighborhood);
		manager.add(actEnableIdenticalNodeHighlighting);
		manager.add(actEnableAnnotation);
		manager.add(actClearAllPathways);

//		IContributionItem comboCI = new ControlContribution("Quick search") {
//
//			private Text searchText;
//
//			private Label resultLabel;
//
//			protected Control createControl(Composite parent) {
//
//				Composite composite = new Composite(parent, SWT.NONE);
//				RowLayout rowLayout = new RowLayout();
//				// rowLayout.fill = true;
//				// rowLayout.justify = true;
//				// rowLayout.pack = false;
//				rowLayout.type = SWT.VERTICAL;
//				// rowLayout.wrap = false;
//				composite.setLayout(rowLayout);
//
//				Composite searchInputComposite = new Composite(composite,
//						SWT.NONE);
//				searchInputComposite.setLayout(new GridLayout(2, true));
//				resultLabel = new Label(composite, SWT.CENTER);
//				Label searchInputLabel = new Label(searchInputComposite,
//						SWT.NULL);
//				searchInputLabel.setText("Quick search:");
//
//				searchText = new Text(searchInputComposite, SWT.SEARCH | SWT.CANCEL);
//				searchText.setLayoutData(new GridData(GridData.FILL_BOTH));
//				searchText.addKeyListener(new KeyAdapter() {
//					public void keyPressed(KeyEvent event) 
//					{
//						switch (event.keyCode) 
//						{
//							case SWT.CR:
//							{	
//								boolean bFound = Application.geneview_core.getGeneralManager()
//									.getSingelton().getViewGLCanvasManager()
//										.getDataEntitySearcher().searchForEntity(
//												searchText.getText());
//			
//								if (!bFound) {
//									resultLabel.setText(" NOT FOUND! Try again...");
//									resultLabel.setForeground(resultLabel.getDisplay()
//											.getSystemColor(SWT.COLOR_RED));
//									resultLabel.pack();
//								}
//								else
//								{
//									resultLabel.setText("");
//									resultLabel.pack();
//								}
//							}
//						}
//					}
//				});
//
////				searchText.addSelectionListener(new SelectionAdapter() 
////				{
////					public void widgetDefaultSelected(SelectionEvent e) 
////					{
////						if (e.detail == SWT.CANCEL) 
////						{
////							resultLabel.setText(" NOT FOUND! Try again...");
////							resultLabel.setForeground(resultLabel.getDisplay()
////									.getSystemColor(SWT.COLOR_RED));
////						}
////					}
////				});
//				
//				composite.pack();
//
//				return composite;
//			}
//		};


	}

	private void createAnimatorToggleAction() {

		// showMessage("Action 1", "make new action [toggle JOGL frame]");

		actToggleAnimatorRunningState = new Action() {
			public void run() {

				if (swtComposite.isVisible()) {
					/* toggle state */
					setGLCanvasVisible(!frameGL.isVisible());
				} // if ( swtComposite.isVisible() ) {
			}
		};
		actToggleAnimatorRunningState.setText("Turn off/on animator");
		actToggleAnimatorRunningState.setToolTipText("Turn off/on animator");
		actToggleAnimatorRunningState.setImageDescriptor(ImageDescriptor
				.createFromURL(this.getClass().getClassLoader().getResource(
						ACTION_ENABLE_ANIMATOR_ICON)));

		// showMessage("Action 1","executed toggle JOGL frame");
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
		actEnableGeneMapping.setImageDescriptor(ImageDescriptor
				.createFromURL(this.getClass().getClassLoader().getResource(
						ACTION_ENABLE_GENE_MAPPING_ICON)));
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
		actEnablePathwayTextures
				.setToolTipText(ACTION_ENABLE_PATHWAY_TEXTURES_TEXT);
		actEnablePathwayTextures.setImageDescriptor(ImageDescriptor
				.createFromURL(this.getClass().getClassLoader().getResource(
						ACTION_ENABLE_PATHWAY_TEXTURES_ICON)));
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
		actEnableNeighborhood.setImageDescriptor(ImageDescriptor
				.createFromURL(this.getClass().getClassLoader().getResource(
						ACTION_ENABLE_NEIGHBORHOOD_ICON)));
	}

	private void createIdenticalNodeHighlightingAction() {

		actEnableIdenticalNodeHighlighting = new Action() {
			public void run() {

				bEnableIdenticalNodeHighlighting = !bEnableIdenticalNodeHighlighting;
				triggerCmdSExternalFlagSetter(
						bEnableIdenticalNodeHighlighting,
						EExternalFlagSetterType.PATHWAY_ENABLE_IDENTICAL_NODE_HIGHLIGHTING);
			}
		};

		actEnableIdenticalNodeHighlighting
				.setText(ACTION_ENABLE_IDENTICAL_NODE_HIGHLIGHTING_TEXT);
		actEnableIdenticalNodeHighlighting
				.setToolTipText(ACTION_ENABLE_IDENTICAL_NODE_HIGHLIGHTING_TEXT);
		actEnableIdenticalNodeHighlighting.setImageDescriptor(ImageDescriptor
				.createFromURL(this.getClass().getClassLoader().getResource(
						ACTION_ENABLE_IDENTICAL_NODE_HIGHLIGHTING_ICON)));
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
		actEnableAnnotation.setImageDescriptor(ImageDescriptor
				.createFromURL(this.getClass().getClassLoader().getResource(
						ACTION_ENABLE_ANNOTATION_ICON)));
	}

	private void createClearAllPathwaysAction() {

		actClearAllPathways = new Action() {
			public void run() {

				triggerCmdExternalAction(EExternalActionType.PATHWAY_CLEAR_ALL);
			}
		};

		actClearAllPathways.setText(ACTION_CLEAR_ALL_PATHWAYS_TEXT);
		actClearAllPathways.setToolTipText(ACTION_CLEAR_ALL_PATHWAYS_TEXT);
		actClearAllPathways.setImageDescriptor(ImageDescriptor
				.createFromURL(this.getClass().getClassLoader().getResource(
						ACTION_CLEAR_ALL_PATHWAY_ICON)));
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {

		super.dispose();
	}

	public void triggerCmdSExternalFlagSetter(final boolean bFlag,
			EExternalFlagSetterType type) {

		CmdExternalFlagSetter tmpCmd = (CmdExternalFlagSetter) Application.refGeneralManager
				.getSingelton().getCommandManager().createCommandByType(
						CommandQueueSaxType.EXTERNAL_FLAG_SETTER);

		// FIXME: hard coded view ID
		tmpCmd.setAttributes(82401, bFlag, type);
		tmpCmd.doCommand();
	}

	public void triggerCmdExternalAction(EExternalActionType type) {

		CmdExternalActionTrigger tmpCmd = (CmdExternalActionTrigger) Application.refGeneralManager
				.getSingelton().getCommandManager().createCommandByType(
						CommandQueueSaxType.EXTERNAL_ACTION_TRIGGER);

		// FIXME: hard coded view ID
		tmpCmd.setAttributes(82401, type);
		tmpCmd.doCommand();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {

	}
}