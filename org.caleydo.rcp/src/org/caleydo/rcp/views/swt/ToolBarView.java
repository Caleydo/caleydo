package org.caleydo.rcp.views.swt;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.swt.CmdViewCreateDataEntitySearcher;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;
import org.caleydo.core.view.swt.data.search.DataEntitySearcherViewRep;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.action.toolbar.general.ExportDataAction;
import org.caleydo.rcp.action.toolbar.general.LoadDataAction;
import org.caleydo.rcp.action.toolbar.view.TakeSnapshotAction;
import org.caleydo.rcp.perspective.GenomePerspective;
import org.caleydo.rcp.util.info.InfoArea;
import org.caleydo.rcp.util.search.SearchBox;
import org.caleydo.rcp.views.CaleydoViewPart;
import org.caleydo.rcp.views.opengl.AGLViewPart;
import org.caleydo.rcp.views.opengl.GLGlyphView;
import org.caleydo.rcp.views.opengl.GLHeatMapView;
import org.caleydo.rcp.views.opengl.GLHierarchicalHeatMapView;
import org.caleydo.rcp.views.opengl.GLParCoordsView;
import org.caleydo.rcp.views.opengl.GLPathwayView;
import org.caleydo.rcp.views.opengl.GLRemoteRenderingView;
import org.eclipse.jface.action.IToolBarManager;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;

public class ToolBarView
	extends ViewPart
	implements IMediatorReceiver, ISizeProvider
{
	public static final String ID = "org.caleydo.rcp.views.swt.ToolBarView";

	public static final int TOOLBAR_WIDTH = 173;
	public static final int TOOLBAR_HEIGHT = 123;	

	public static boolean bHorizontal = false;

	private Composite parentComposite;

	private DataEntitySearcherViewRep dataEntitySearcher;

	// private HTMLBrowserViewRep browserView;
	
	private ArrayList<Group> viewSpecificGroups;
	
	private boolean bIsBucketViewActive = false;
	
	private Label pathwaySearchLabel;
	private SearchBox pathwaySearchBox;
	
	@Override
	public void createPartControl(Composite parent)
	{
		final Composite parentComposite = new Composite(parent, SWT.NULL);

		if (!GenomePerspective.bIsWideScreen)
			bHorizontal = true;
		
		if (bHorizontal)
			parentComposite.setLayout(new GridLayout(10, false));
		else 
			parentComposite.setLayout(new GridLayout(1, false));			
		
		this.parentComposite = parentComposite;
		
		viewSpecificGroups = new ArrayList<Group>();
		
		addGeneralToolBar();
		addSearchBar();
		addColorMappingBar();
		addInfoBar();
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

	public void addViewSpecificToolBar(CaleydoViewPart viewPart)
	{
		addViewSpecificToolBar(viewPart.getViewID(), 
			viewPart.getClass().getName());
		
		if (viewPart instanceof GLRemoteRenderingView)
		{	
			// Add toolbars of remote rendered views
			for (int iRemoteRenderedGLViewID : ((GLRemoteRendering) ((AGLViewPart)viewPart).getGLEventListener())
					.getRemoteRenderedViews())
			{
				AGLEventListener glView = GeneralManager.get().getViewGLCanvasManager()
					.getGLEventListener(iRemoteRenderedGLViewID);
				
				String sViewType = "";
				
				if (glView instanceof GLHeatMap)
					sViewType = GLHeatMapView.ID;
				else if (glView instanceof GLPathway)
					sViewType = GLPathwayView.ID;
				else if (glView instanceof GLParallelCoordinates)
					sViewType = GLParCoordsView.ID;
				else 
					break;
				
				addViewSpecificToolBar(iRemoteRenderedGLViewID, sViewType);
			}
		}
		
	}
	
	public void addViewSpecificToolBar(int iViewID, String sViewType)
	{
		// Check if toolbar is already present
		for (Group group : viewSpecificGroups)
		{
			// Only one pathway toolbar for all pathways is allowed
			if (group.getData("viewType").equals(GLPathwayView.ID)
					&& GeneralManager.get().getViewGLCanvasManager().getGLEventListener(
							iViewID) instanceof GLPathway)
				return;

			if (group.getData("viewID") != null
					&& ((Integer) group.getData("viewID")).intValue() == iViewID)
				return;
		}
		
		String sViewTitle = "";
		Image viewIcon = null;

	    Group group = new Group(parentComposite, SWT.NULL);
	    GridLayout layout = new GridLayout(1, false);
	    layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight =
	    	layout.horizontalSpacing = layout.verticalSpacing = 0;
	    layout.marginHeight = layout.marginWidth = 3;
	    group.setLayout(layout);
	    
	    if (bHorizontal)
	    	group.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	    else
	    	group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    viewSpecificGroups.add(group);
	    
		// Needed to simulate toolbar wrapping which is not implemented for
		// linux
		// See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=46025
		ArrayList<ToolBar> alToolBar = new ArrayList<ToolBar>();
		ArrayList<IToolBarManager> alToolBarManager = new ArrayList<IToolBarManager>();

		final ToolBar toolBar = new ToolBar(group, SWT.WRAP | SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		alToolBar.add(toolBar);
		alToolBarManager.add(toolBarManager);

		final ToolBar toolBar2 = new ToolBar(group, SWT.WRAP | SWT.FLAT);
		ToolBarManager toolBarManager2 = new ToolBarManager(toolBar2);
		alToolBar.add(toolBar2);
		alToolBarManager.add(toolBarManager2);
		
		if (sViewType.equals(GLRemoteRenderingView.ID))
		{
			GLRemoteRenderingView.createToolBarItems(iViewID);
			GLRemoteRenderingView.fillToolBar(alToolBarManager);
			
			sViewTitle = "Bucket";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/remote/remote.png");
			
			bIsBucketViewActive = true;
			updateSearchBar(true);
		}
		else if (sViewType.equals(GLHeatMapView.ID))
		{
			GLHeatMapView.createToolBarItems(iViewID);
			GLHeatMapView.fillToolBar(alToolBarManager);
			
			sViewTitle = "Heat Map";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/storagebased/heatmap/heatmap.png");
		}
		else if (sViewType.equals(GLHierarchicalHeatMapView.ID))
		{
			GLHierarchicalHeatMapView.createToolBarItems(iViewID);
			GLHierarchicalHeatMapView.fillToolBar(alToolBarManager);
			
			sViewTitle = "Full Heat Map";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/storagebased/heatmap/heatmap.png");
		}
		else if (sViewType.equals(GLParCoordsView.ID))
		{
			GLParCoordsView.createToolBarItems(iViewID);
			GLParCoordsView.fillToolBar(alToolBarManager);
			
			sViewTitle = "Parallel Coordinates";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/storagebased/parcoords/parcoords.png");
		}
		else if (sViewType.equals(GLGlyphView.ID))
		{
			GLGlyphView.createToolBarItems(iViewID);
			GLGlyphView.fillToolBar(alToolBarManager);
			
			sViewTitle = "Glyph";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/glyph/glyph.png");
		}
		else if (sViewType.equals(GLPathwayView.ID))
		{
			GLPathwayView.createToolBarItems(iViewID);
			GLPathwayView.fillToolBar(alToolBarManager);
			
			sViewTitle = "Pathway";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/pathway/pathway.png");
		}
		else if (sViewType.equals(HTMLBrowserView.ID))
		{
			toolBar.dispose();
		
			((HTMLBrowserView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView(HTMLBrowserView.ID)).createToolBarItems(iViewID, group);
			
			sViewTitle = "Browser";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/browser/browser.png");			
		}
		else
		{
			return;
		}

		toolBarManager.update(true);

		if (toolBarManager2.isEmpty())
			toolBarManager2.dispose();
		else
			toolBarManager2.update(true);

		if (bHorizontal)
		{
			Label spacer = new Label(group, SWT.NULL);
			spacer.setLayoutData(new GridData(GridData.FILL_BOTH));			
		}
		
		Label label = new Label(group, SWT.CENTER);
		label.setText(sViewTitle);
    	label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		
		group.setData("viewType", sViewType);
		group.setData("viewID", iViewID);
		
		group.layout();
		parentComposite.layout();
	}

	public void removeViewSpecificToolBar(int iViewID)
	{
		Group removedGroup = null;
		for (Group group : viewSpecificGroups)
		{
			if (!(group.getData("view") instanceof AGLEventListener))
				continue;

			if (group.getData("viewID") != null
				&& ((Integer) group.getData("viewID")).intValue() == iViewID)
			{
				group.dispose();
				removedGroup = group;
				break;
			}
		}	
		
		if (removedGroup != null)
			viewSpecificGroups.remove(removedGroup);
		
		// Remove toolbars of remote rendered views
		AGLEventListener glView = GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(iViewID);

		if (glView instanceof GLRemoteRendering)
		{
			for (int iRemoteRenderedGLViewID : ((GLRemoteRendering) glView)
					.getRemoteRenderedViews())
			{
				removeViewSpecificToolBar(iRemoteRenderedGLViewID);
			}
			
			// Update search bar
			bIsBucketViewActive = false;
			updateSearchBar(false);
		}
	}
	
	public void removeAllViewSpecificToolBars()
	{
		for (Group group : viewSpecificGroups)
		{
			group.dispose();
		}
		
		viewSpecificGroups.clear();
		
		// Update search bar
		bIsBucketViewActive = false;
		updateSearchBar(false);
	}

//	public void highlightViewSpecificToolBar(int iViewID)
//	{
//		// Unselect old highlights
//		for (Group group : viewSpecificGroups)
//		{
//			if (item.getData("view") instanceof AGLEventListener)
//			{
//				// ((ToolBar)item.getData()).setBackground(
//				// parentComposite.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
//
//				AGLEventListener glEventListener = (AGLEventListener) item.getData("view");
//
////				if(glEventListener.isRenderedRemote())
////					continue;
//				
//				item.setExpanded(false);
//			}
//		}
//
//		for (ExpandItem item : expandBar.getItems())
//		{
//			AGLEventListener glEventListener = (AGLEventListener) item.getData("view");
//
//			if (!(glEventListener instanceof AGLEventListener))
//				continue;
//
//			// if (!(glEventListener instanceof GLRemoteRendering))
//			// {
//			// item.setExpanded(false);
//			// // continue;
//			// }
//			// }
//
//			if (glEventListener.getID() == iViewID
//					|| (GeneralManager.get().getViewGLCanvasManager().getGLEventListener(
//							iViewID) instanceof GLPathway && glEventListener instanceof GLPathway))
//			{
//				// ((ToolBar)item.getData()).setBackground(
//				// parentComposite.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
//
//				item.setExpanded(true);
//
//				// Highlight also remote rendering parent of the selected sub
//				// view
//				if (glEventListener.isRenderedRemote())
//				{
//					AGLEventListener glRemoteEventListener = (AGLEventListener) glEventListener
//							.getRemoteRenderingGLCanvas();
//					for (ExpandItem remoteItem : expandBar.getItems())
//					{
//						if (remoteItem.getData("view") == glRemoteEventListener)
//						{
//							remoteItem.setExpanded(true);
//						}
//					}
//				}
//			}
//		}
//	}

	private void addSearchBar()
	{
		Group searchGroup = new Group(parentComposite, SWT.NULL);

	    GridLayout layout = new GridLayout(1, false);
	    layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight =
	    	layout.horizontalSpacing = layout.verticalSpacing = 0;
	    layout.marginHeight = layout.marginWidth = 3;
	    searchGroup.setLayout(layout);
	    GridData gridData;
	    if (bHorizontal)
	    {
	    	gridData = new GridData(GridData.FILL_VERTICAL);
	    	gridData.minimumWidth = 230;
	    	gridData.widthHint = 230;
	    }
	    else
	    {
	    	gridData = new GridData(GridData.FILL_HORIZONTAL);
	    }
    	searchGroup.setLayoutData(gridData);
	    
		// Trigger gene/pathway search command
		CmdViewCreateDataEntitySearcher cmd = (CmdViewCreateDataEntitySearcher) GeneralManager
				.get().getCommandManager().createCommandByType(
						ECommandType.CREATE_VIEW_DATA_ENTITY_SEARCHER);
		cmd.doCommand();
		dataEntitySearcher = cmd.getCreatedObject();

		Composite searchComposite = new Composite(searchGroup, SWT.NULL);
		searchComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (bHorizontal)
		    layout = new GridLayout(2, false);
	    else
		    layout = new GridLayout(1, false);

	    layout.marginHeight = layout.marginWidth = 0;
	    searchComposite.setLayout(layout);
	    
		pathwaySearchLabel = new Label(searchComposite, SWT.NULL);
		pathwaySearchLabel.setText("Pathway");

		pathwaySearchBox = new SearchBox(searchComposite, SWT.BORDER);
	    
		String items[] = { "No pathways available!" };
		pathwaySearchBox.setItems(items);
		pathwaySearchBox.setTextLimit(21);
		pathwaySearchBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if (Application.bLoadPathwayData)
		{
			pathwaySearchBox.addFocusListener(new FocusAdapter()
			{
				@Override
				public void focusGained(FocusEvent e)
				{
					if (!Application.bLoadPathwayData)
					{
						pathwaySearchBox.setEnabled(false);
						return;
					}
						
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

					pathwaySearchBox.setItems(sArSearchItems);
					pathwaySearchBox.removeFocusListener(this);
				}
			});
		}
		else
		{
			pathwaySearchLabel.setEnabled(false);
			pathwaySearchBox.setEnabled(false);
		}

		pathwaySearchBox.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String sSearchEntity = pathwaySearchBox.getItem(pathwaySearchBox.getSelectionIndex());
				// sSearchEntity = sSearchEntity.substring(0,
				// sSearchEntity.indexOf(" ("));

				dataEntitySearcher.searchForEntity(sSearchEntity);
			}
		});

		// Gene search
		Label entitySearchLabel = new Label(searchComposite, SWT.NULL);
		entitySearchLabel.setText("Gene");

		final Text geneSearchText = new Text(searchComposite, SWT.BORDER | SWT.SINGLE);
	    geneSearchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
	    geneSearchText.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				geneSearchText.setText("");
				// geneSearchText.pack();
			}
		});

		geneSearchText.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent event)
			{
				switch (event.keyCode)
				{
					case SWT.CR:
					{
						boolean bFound = dataEntitySearcher.searchForEntity(geneSearchText
								.getText());

						if (!bFound)
						{
							geneSearchText.setText(" NOT FOUND! Try again...");
							// geneSearchText.setForeground(geneSearchText
							// .getDisplay().getSystemColor(SWT.COLOR_RED));
							// geneSearchText.pack();
						}
					}
				}
			}
		});
		
		Label spacer = new Label(searchGroup, SWT.NULL);
		if (bHorizontal)
		{
			spacer.setLayoutData(new GridData(GridData.FILL_BOTH));			
		}
		else
		{
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.minimumHeight = 10;
			data.heightHint = 10;
			spacer.setLayoutData(data);
		}
		
		Label label = new Label(searchGroup, SWT.CENTER);
		label.setText("Gene search");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	}

	private void updateSearchBar(boolean bIsVisible)
	{
		pathwaySearchBox.setVisible(bIsVisible);
		pathwaySearchLabel.setVisible(bIsVisible);
		parentComposite.layout();
	}
	
	private void addColorMappingBar()
	{
	    Group group = new Group(parentComposite, SWT.NULL);
	    GridLayout layout = new GridLayout(1, false);
	    layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight =
	    	layout.horizontalSpacing = layout.verticalSpacing = 0;
	    layout.marginHeight = layout.marginWidth = 3;
	    group.setLayout(layout);

	    GridData gridData;
	    if (bHorizontal)
	    {
	    	gridData = new GridData(GridData.FILL_VERTICAL);
	    	gridData.minimumWidth = 110;
	    	gridData.widthHint = 110;
	    }
	    else
	    {
	    	gridData = new GridData(GridData.FILL_HORIZONTAL);
	    }
    	group.setLayoutData(gridData);
	    
		CLabel colorMappingPreviewLabel = new CLabel(group, SWT.SHADOW_NONE);
		// colorMappingPreviewLabel.setBounds(0, 0, 200, 40);
		colorMappingPreviewLabel.setText("");
		
		// TODO for Alex: Read real color mapping values
		Color[] alColorMarkerPoints = new Color[3];
		alColorMarkerPoints[0] = new Color(Display.getCurrent(), 255, 0, 0);
		alColorMarkerPoints[1] = new Color(Display.getCurrent(), 0, 0, 0);
		alColorMarkerPoints[2] = new Color(Display.getCurrent(), 0, 255, 0);
		colorMappingPreviewLabel.setBackground(alColorMarkerPoints, new int[] { 20, 100 });
		colorMappingPreviewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		colorMappingPreviewLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(
						parentComposite.getShell(),
						"org.caleydo.rcp.preferences.ColorMappingPreferencePage", null, null);

				if (pref != null)
					pref.open();
			}
		});

		Label spacer = new Label(group, SWT.NULL);
		if (bHorizontal)
		{
			spacer.setLayoutData(new GridData(GridData.FILL_BOTH));			
		}
		else
		{
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.minimumHeight = 10;
			data.heightHint = 10;
			spacer.setLayoutData(data);
		}
		
		Label label = new Label(group, SWT.CENTER);
		label.setText("Color Mapping");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	}

	private void addGeneralToolBar()
	{
	    Group group = new Group(parentComposite, SWT.NULL);
	    GridLayout layout = new GridLayout(1, false);
	    layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight =
	    	layout.horizontalSpacing = layout.verticalSpacing = 0;
	    layout.marginHeight = layout.marginWidth = 3;
	    group.setLayout(layout);
	    if (bHorizontal)
	    	group.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	    else
	    	group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
		final ToolBar toolBar = new ToolBar(group, SWT.WRAP | SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		toolBarManager.add(new LoadDataAction());
		toolBarManager.add(new ExportDataAction());

		if (bHorizontal)
		{
			final ToolBar toolBar2 = new ToolBar(group, SWT.WRAP | SWT.FLAT);
			ToolBarManager toolBarManager2 = new ToolBarManager(toolBar2);
			toolBarManager2.add(new TakeSnapshotAction());
			toolBarManager2.update(true);

			Label spacer = new Label(group, SWT.NULL);
			spacer.setLayoutData(new GridData(GridData.FILL_BOTH));			
		}
		else
		{
			toolBarManager.add(new TakeSnapshotAction());			
		}

		toolBarManager.update(true);
		
		Label label = new Label(group, SWT.CENTER);
		label.setText("General");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	}

	private void addInfoBar()
	{
	    Group group = new Group(parentComposite, SWT.NULL);
	    GridLayout layout = new GridLayout(1, false);
	    layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight =
	    	layout.horizontalSpacing = layout.verticalSpacing = 0;
	    layout.marginHeight = layout.marginWidth = 3;
	    group.setLayout(layout);
	    if (bHorizontal)
	    	group.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	    else
	    	group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
		Composite infoComposite = new Composite(group, SWT.NULL);
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
		if (bHorizontal)
		    layout = new GridLayout(2, false);
	    else
		    layout = new GridLayout(1, false);

		layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight =
	    	layout.horizontalSpacing = layout.verticalSpacing = 0;
	    layout.marginHeight = layout.marginWidth = 0;
	    
	    infoComposite.setLayout(layout);
		InfoArea infoArea = new InfoArea();
		infoArea.createControl(infoComposite);
		
		if (bHorizontal)
		{
			Label spacer = new Label(group, SWT.NULL);
			spacer.setLayoutData(new GridData(GridData.FILL_BOTH));			
		}
		
		Label label = new Label(group, SWT.CENTER);
		label.setText("Info");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	}

	@Override
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer, EMediatorType eMediatorType)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public int computePreferredSize(boolean width, int availableParallel,
			int availablePerpendicular, int preferredResult)
	{
		// Set minimum size of the view
		if (width == true)
			return (int)(ToolBarView.TOOLBAR_WIDTH);
		
		return (int)(ToolBarView.TOOLBAR_HEIGHT);
	}

	@Override
	public int getSizeFlags(boolean width)
	{
		return SWT.MIN;
	}
}
