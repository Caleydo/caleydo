package org.caleydo.rcp.views.swt;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.swt.CmdViewCreateDataEntitySearcher;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;
import org.caleydo.core.view.swt.data.search.DataEntitySearcherViewRep;
import org.caleydo.rcp.action.view.TakeSnapshotAction;
import org.caleydo.rcp.util.info.InfoArea;
import org.caleydo.rcp.util.search.SearchBox;
import org.caleydo.rcp.views.opengl.GLHeatMapView;
import org.caleydo.rcp.views.opengl.GLParCoordsView;
import org.caleydo.rcp.views.opengl.GLPathwayView;
import org.caleydo.rcp.views.opengl.GLRemoteRenderingView;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;

public class ToolBarView
	extends ViewPart
	implements IMediatorReceiver
{
	public static final String ID = "org.caleydo.rcp.views.ToolBarView";
	
	public static final int TOOLBAR_WIDTH = 165;
	
	private ExpandBar expandBar;

	private Composite parentComposite;
	
	private DataEntitySearcherViewRep dataEntitySearcher;
	
//	private HTMLBrowserViewRep browserView;

	@Override
	public void createPartControl(Composite parent)
	{
		final Composite parentComposite = new Composite(parent, SWT.NULL);
		parentComposite.setLayout(new RowLayout(SWT.VERTICAL));
		
		this.parentComposite = parentComposite;
		
		expandBar = new ExpandBar (parentComposite, SWT.V_SCROLL);
//		expandBar.setLayoutData(new RowData(parentComposite.getBounds().width, 
//				parentComposite.getBounds().height));
		expandBar.setSpacing(2);
		
		addGeneralToolBar();
		addSearchBar();
		addColorMappingBar();
		addInfoBar();
		
//		expandBar.setLayoutData(new RowData(parentComposite.getBounds().width, 500));
//        expandBar.pack();
//		parentComposite.pack();
//		parentComposite.update();
		
		parentComposite.addListener(SWT.Resize, new Listener() {
	        public void handleEvent(Event e) {
//	          Rectangle rect = composite.getParent().getClientArea();
//	          Point size = toolBar.computeSize(rect.width, SWT.DEFAULT);
//	          toolBar.setSize(size);
////	          toolBar.setBounds(0, 0,size.x, size.y);
//	          toolBar.pack();
	          
	          expandBar.setLayoutData(new RowData(parentComposite.getBounds().width, 
	        		  parentComposite.getBounds().height));
	          expandBar.pack();
	        }
	      });
	}

	@Override
	public void setFocus()
	{

	}

	@Override
	public void dispose()
	{
		super.dispose();
	}
	
//	public void addPathwayLoadingProgress()
//	{
//        try {
//            new ProgressMonitorDialog(parentComposite.getShell()).run(true, true,
//                new PathwayLoadingProgress());
//          } catch (InvocationTargetException e) {
//            MessageDialog.openError(parentComposite.getShell(), "Error", e.getMessage());
//          } catch (InterruptedException e) {
//            MessageDialog.openInformation(parentComposite.getShell(), "Cancelled", e.getMessage());
//          }
//          
////		  PlatformUI.getWorkbench().getProgressService().
////		  	busyCursorWhile(this); 
//	}
	
	public void addViewSpecificToolBar(int iViewID)
	{
		// Check if toolbar for this view is already present
		for (ExpandItem item : expandBar.getItems())
		{
			// Only one pathway toolbar for all pathways is allowed
			if (item.getData("view") instanceof GLPathway
					&& GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID) instanceof GLPathway)
				return;
			
			if(item.getData("viewID") != null && ((Integer)item.getData("viewID")).intValue() == iViewID)
				return;
		}
		
		String sViewTitle = "";
		Image viewIcon = null;
		
		final Composite composite = new Composite (expandBar, SWT.NONE);
		GridLayout layout = new GridLayout ();
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);

		// Needed to simulate toolbar wrapping which is not implemented for linux
		// See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=46025
		ArrayList<ToolBar> alToolBar = new ArrayList<ToolBar>();
		ArrayList<IToolBarManager> alToolBarManager = new ArrayList<IToolBarManager>();
		
		final ToolBar toolBar = new ToolBar(composite, SWT.WRAP | SWT.FLAT);
		toolBar.setBackground(parentComposite.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));		
	    ToolBarManager toolBarManager = new ToolBarManager(toolBar);
	    alToolBar.add(toolBar);
	    alToolBarManager.add(toolBarManager);
	    
		final ToolBar toolBar2 = new ToolBar(composite, SWT.WRAP | SWT.FLAT);
		toolBar2.setBackground(parentComposite.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
	    ToolBarManager toolBarManager2 = new ToolBarManager(toolBar2);
	    alToolBar.add(toolBar2);
	    alToolBarManager.add(toolBarManager2);
	    
	    AGLEventListener glView = GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID);
	    
	    if (glView instanceof GLRemoteRendering)
	    {
			GLRemoteRenderingView.createToolBarItems(iViewID);
			GLRemoteRenderingView.fillToolBar(alToolBarManager);	
			
			sViewTitle = "Bucket";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/remote/remote.png");
	    }
	    else if (glView instanceof GLHeatMap)
	    {
			GLHeatMapView.createToolBarItems(iViewID);
			GLHeatMapView.fillToolBar(alToolBarManager);	
			
			sViewTitle = "Heat Map";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/storagebased/heatmap/heatmap.png");
	    }
	    else if (glView instanceof GLParallelCoordinates)
	    {
			GLParCoordsView.createToolBarItems(iViewID);
			GLParCoordsView.fillToolBar(alToolBarManager);
			
			sViewTitle = "Parallel Coordinates";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/storagebased/parcoords/parcoords.png");
	    }
	    else if (glView instanceof GLPathway)
	    {
			GLPathwayView.createToolBarItems(iViewID);
			GLPathwayView.fillToolBar(alToolBarManager);
			
			sViewTitle = "Pathway";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/pathway/pathway.png");
	    }
	    else
	    {
	    	return;
	    }
	    
		toolBarManager.add(new Separator());
		toolBarManager.update(true);
		
		toolBarManager2.add(new Separator());
		toolBarManager2.update(true);
		
		Label separator= new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		ExpandItem expandItem = new ExpandItem (expandBar, SWT.NONE, 0);
		expandItem.setText(sViewTitle);
		expandItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		expandItem.setControl(composite);
		expandItem.setImage(viewIcon);
		expandItem.setExpanded(true);
		expandItem.setData("viewID", glView.getID());
		expandItem.setData("view", glView);
		expandItem.setData(toolBar);
		
	    if (glView instanceof GLRemoteRendering)
	    {
			// Add toolbars of remote rendered views
			for (int iRemoteRenderedGLViewID : ((GLRemoteRendering)glView).getRemoteRenderedViews())
			{
				addViewSpecificToolBar(iRemoteRenderedGLViewID);
			}
	    }
	}
	
	public void removeViewSpecificToolBar(int iViewID)
	{
		for (ExpandItem item : expandBar.getItems())
		{
			if (!(item.getData("view") instanceof AGLEventListener))
				continue;
			
			if(item.getData("viewID") != null && ((Integer)item.getData("viewID")).intValue() == iViewID)
			{
				item.setExpanded(false);
				item.dispose();
				expandBar.update();
				break;
			}
		}
		
		// Remove toolbars of remote rendered views
		AGLEventListener glView = GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID);
	    
	    if (glView instanceof GLRemoteRendering)
		{
			for (int iRemoteRenderedGLViewID : ((GLRemoteRendering)glView).getRemoteRenderedViews())
			{
				removeViewSpecificToolBar(iRemoteRenderedGLViewID);
			}
		}
	}

	public void highlightViewSpecificToolBar(int iViewID)
	{
		// Unselect old highlights
		for (ExpandItem item : expandBar.getItems())
		{
			if (item.getData("view") instanceof AGLEventListener)
			{
	//			((ToolBar)item.getData()).setBackground(
	//				parentComposite.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				
				item.setExpanded(false);
			}
		}		
		
		for (ExpandItem item : expandBar.getItems())
		{
			AGLEventListener glEventListener = (AGLEventListener)item.getData("view");
			
			if (!(glEventListener instanceof AGLEventListener))
				continue;
			
//			if (!(glEventListener instanceof GLRemoteRendering))
//			{
//				item.setExpanded(false);
////				continue;
//			}
//			}				
			
			if (glEventListener.getID() == iViewID 
					|| (GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID) instanceof GLPathway
					&& glEventListener instanceof GLPathway))
			{
//				((ToolBar)item.getData()).setBackground(
//						parentComposite.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
				
				item.setExpanded(true);
				
				// Highlight also remote rendering parent of the selected sub view
				if (glEventListener.isRenderedRemote())
				{
					AGLEventListener glRemoteEventListener = (AGLEventListener)glEventListener.getRemoteRenderingGLCanvas();
					for (ExpandItem remoteItem : expandBar.getItems())
					{
						if (remoteItem.getData("view") == glRemoteEventListener)
						{
							remoteItem.setExpanded(true);
						}
					}	
				}
			}
		}
	}
	
	private void addSearchBar()
	{
		// Trigger gene/pathway search command
		CmdViewCreateDataEntitySearcher cmd = (CmdViewCreateDataEntitySearcher) GeneralManager
				.get().getCommandManager().createCommandByType(
						ECommandType.CREATE_VIEW_DATA_ENTITY_SEARCHER);
		cmd.doCommand();
		dataEntitySearcher = cmd.getCreatedObject();
		
		final Composite composite = new Composite (expandBar, SWT.NONE);
		GridLayout layout = new GridLayout ();
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		
		// Add search bar
//		if (!GeneralManager.get().getPreferenceStore().getBoolean(
//				PreferenceConstants.XP_CLASSIC_STYLE_MODE))
//		{
		Label searchInputLabel = new Label(composite, SWT.NULL);
		searchInputLabel.setText("Pathway search");
		searchInputLabel.pack();
		
		final SearchBox searchBox = new SearchBox(composite, SWT.BORDER);

		String items[] = { "No pathways available!" };
		GridData data = new GridData();
		data.widthHint = TOOLBAR_WIDTH;
		searchBox.setLayoutData(data);
		searchBox.setItems(items);
		searchBox.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				Collection<PathwayGraph> allPathways = GeneralManager.get()
						.getPathwayManager().getAllItems();
				String[] sArSearchItems = new String[allPathways.size()];
				int iIndex = 0;
				String sPathwayTitle = "";
				for (PathwayGraph pathway : allPathways)
				{
					sPathwayTitle = pathway.getTitle();

					// if (sPathwayTitle.length() > MAX_PATHWAY_TITLE_LENGTH)
					// sPathwayTitle = sPathwayTitle.substring(0,
					// MAX_PATHWAY_TITLE_LENGTH) + "... ";

					// sArSearchItems[iIndex] = pathway.getType().toString()
					// + " - " + sPathwayTitle;

					sArSearchItems[iIndex] = sPathwayTitle + " ("
							+ pathway.getType().toString() + ")";
					iIndex++;
				}

				searchBox.setItems(sArSearchItems);
				searchBox.removeFocusListener(this);
			}
		});

		searchBox.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				String sSearchEntity = searchBox.getItem(searchBox.getSelectionIndex());
				// sSearchEntity = sSearchEntity.substring(0,
				// sSearchEntity.indexOf(" ("));

				dataEntitySearcher.searchForEntity(sSearchEntity);
			}
		});

		// Gene search
		Label entitySearchLabel = new Label(composite, SWT.NULL);
		entitySearchLabel.setText("Gene search");

		final Text geneSearchText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		geneSearchText.setLayoutData(data);
		geneSearchText.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				geneSearchText.setText("");
//				geneSearchText.pack();
			}
		});

		geneSearchText.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent event)
			{
				switch (event.keyCode)
				{
					case SWT.CR:
					{
						boolean bFound = dataEntitySearcher.searchForEntity(
								geneSearchText.getText());

						if (!bFound)
						{
							geneSearchText.setText(" NOT FOUND! Try again...");
							// geneSearchText.setForeground(geneSearchText
							// .getDisplay().getSystemColor(SWT.COLOR_RED));
//							geneSearchText.pack();
						}
					}
				}
			}
		});

//		}
		
		ExpandItem expandItem = new ExpandItem (expandBar, SWT.NONE, 0);
		expandItem.setText("Search");
		expandItem.setHeight(100);
		expandItem.setControl(composite);
//		expandItem.setImage(viewIcon);
		expandItem.setExpanded(true);
	}
	
	private void addColorMappingBar()
	{
		final Composite composite = new Composite (expandBar, SWT.NONE);
		GridLayout layout = new GridLayout ();
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		
		CLabel colorMappingPreviewLabel = new CLabel(composite, SWT.SHADOW_IN);
//		colorMappingPreviewLabel.setBounds(0, 0, 200, 40);
		colorMappingPreviewLabel.setText("");
//		colorMappingPreviewLabel.setLayoutData(new RowData(200, 20));
		
		// TODO for Alex: Read real color mapping values
		Color[] alColorMarkerPoints = new Color[3];
		alColorMarkerPoints[0] = new Color(composite.getDisplay(), 255, 0, 0);
		alColorMarkerPoints[1] = new Color(composite.getDisplay(), 0, 0, 0);
		alColorMarkerPoints[2] = new Color(composite.getDisplay(), 0, 255, 0);		
		colorMappingPreviewLabel.setBackground(alColorMarkerPoints, new int[]{20, 100});
		colorMappingPreviewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		colorMappingPreviewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(parentComposite.getShell(), 
						"org.caleydo.rcp.preferences.ColorMappingPreferencePage", null, null);
				
				if (pref != null)
					pref.open();
			}
		});
		
		ExpandItem expandItem = new ExpandItem (expandBar, SWT.NONE, 0);
		expandItem.setText("Color mapping");
		expandItem.setHeight(40);
		expandItem.setControl(composite);
//		expandItem.setImage(viewIcon);
		expandItem.setExpanded(true);
	}
	
	private void addGeneralToolBar()
	{
		final Composite composite = new Composite (expandBar, SWT.NONE);
		GridLayout layout = new GridLayout ();
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		
		final ToolBar toolBar = new ToolBar(composite, SWT.WRAP | SWT.FLAT);
	    ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		toolBarManager.add(new TakeSnapshotAction());
		toolBarManager.update(true);
		
		ExpandItem expandItem = new ExpandItem (expandBar, SWT.NONE, 0);
		expandItem.setText("General");
		expandItem.setHeight(45);
		expandItem.setControl(composite);
//		expandItem.setImage(viewIcon);
		expandItem.setExpanded(true);
	}
	
	private void addInfoBar()
	{
		final Composite composite = new Composite (expandBar, SWT.NONE);
		GridLayout layout = new GridLayout ();
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		
		InfoArea infoArea = new InfoArea();
		infoArea.createControl(composite);
		
		ExpandItem expandItem = new ExpandItem (expandBar, SWT.NONE, 0);
		expandItem.setText("Selection Info");
		expandItem.setHeight(250);
		expandItem.setControl(composite);
//		expandItem.setImage(viewIcon);
		expandItem.setExpanded(true);
	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta,
			Collection<SelectionCommand> colSelectionCommand, EMediatorType mediatorType)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleVAUpdate(IUniqueObject eventTrigger, IVirtualArrayDelta delta,
			Collection<SelectionCommand> colSelectionCommand, EMediatorType mediatorType)
	{
		// TODO Auto-generated method stub
		
	}


}
