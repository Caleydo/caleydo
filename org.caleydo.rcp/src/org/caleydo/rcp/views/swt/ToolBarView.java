package org.caleydo.rcp.views.swt;

import java.util.ArrayList;
import java.util.Collection;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.swt.CmdViewCreateDataEntitySearcher;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHierarchicalHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;
import org.caleydo.core.view.swt.data.search.DataEntitySearcherViewRep;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.action.toolbar.general.ExportDataAction;
import org.caleydo.rcp.action.toolbar.view.TakeSnapshotAction;
import org.caleydo.rcp.perspective.GenomePerspective;
import org.caleydo.rcp.util.info.InfoArea;
import org.caleydo.rcp.util.search.SearchBox;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;

public class ToolBarView
	extends ViewPart
	implements IMediatorReceiver//, ISizeProvider
{
	public static final String ID = "org.caleydo.rcp.views.ToolBarView";

	public static final int TOOLBAR_WIDTH = 240;
	public static final int TOOLBAR_HEIGHT = 150;	

	private Composite parentComposite;

	private DataEntitySearcherViewRep dataEntitySearcher;

	// private HTMLBrowserViewRep browserView;
	
	private ArrayList<Group> viewSpecificGroups;

	public static boolean bHorizontal = false;
	
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

	public void addViewSpecificToolBar(int iViewID)
	{
		String sViewTitle = "";
		Image viewIcon = null;

	    Group group = new Group(parentComposite, SWT.NULL);
	    group.setLayout(new GridLayout(1, false));
	    
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

		AGLEventListener glView = GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(iViewID);

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
		else if (glView instanceof GLHierarchicalHeatMap)
		{
			GLHierarchicalHeatMapView.createToolBarItems(iViewID);
			GLHierarchicalHeatMapView.fillToolBar(alToolBarManager);

			sViewTitle = "Full Heat Map";
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
		else if (glView instanceof GLGlyph)
		{
			GLGlyphView.createToolBarItems(iViewID);
			GLGlyphView.fillToolBar(alToolBarManager);

			sViewTitle = "Glyph";
			viewIcon = GeneralManager.get().getResourceLoader().getImage(
					PlatformUI.getWorkbench().getDisplay(),
					"resources/icons/view/glyph/glyph.png");
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

		toolBarManager.update(true);

		if (toolBarManager2.isEmpty())
			toolBarManager2.dispose();
		else
			toolBarManager2.update(true);

		if (glView instanceof GLRemoteRendering)
		{
			// Add toolbars of remote rendered views
			for (int iRemoteRenderedGLViewID : ((GLRemoteRendering) glView)
					.getRemoteRenderedViews())
			{
				addViewSpecificToolBar(iRemoteRenderedGLViewID);
			}
		}
		
		if (bHorizontal)
		{
			Label spacer = new Label(group, SWT.NULL);
			spacer.setLayoutData(new GridData(GridData.FILL_BOTH));			
		}
		
		Label label = new Label(group, SWT.CENTER);
		label.setText(sViewTitle);
    	label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		
		group.setData("view", glView);
		group.setData("viewID", glView.getID());
		
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
		}
	}
	
	public void removeAllViewSpecificToolBars()
	{
		for (Group group : viewSpecificGroups)
		{
			group.dispose();
		}
		
		viewSpecificGroups.clear();
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
	    Group group = new Group(parentComposite, SWT.NULL);
	    group.setLayout(new GridLayout(1, false));
	    if (bHorizontal)
	    	group.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	    else
	    	group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
		// Trigger gene/pathway search command
		CmdViewCreateDataEntitySearcher cmd = (CmdViewCreateDataEntitySearcher) GeneralManager
				.get().getCommandManager().createCommandByType(
						ECommandType.CREATE_VIEW_DATA_ENTITY_SEARCHER);
		cmd.doCommand();
		dataEntitySearcher = cmd.getCreatedObject();

		Composite searchComposite = new Composite(group, SWT.NULL);
		searchComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    if (bHorizontal)
			searchComposite.setLayout(new GridLayout(2, false));
	    else
			searchComposite.setLayout(new GridLayout(1, false));
		
		Label searchInputLabel = new Label(searchComposite, SWT.NULL);
		searchInputLabel.setText("Pathway");
//		searchInputLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final SearchBox searchBox = new SearchBox(searchComposite, SWT.BORDER);

		String items[] = { "No pathways available!" };
		searchBox.setItems(items);
		searchBox.setTextLimit(21);
		searchBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if (!Application.bNoPathwayData || Application.bLoadPathwayData)
		{
			searchBox.addFocusListener(new FocusAdapter()
			{
				@Override
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
		}
		else
		{
			searchInputLabel.setEnabled(false);
			searchBox.setEnabled(false);
		}

		searchBox.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String sSearchEntity = searchBox.getItem(searchBox.getSelectionIndex());
				// sSearchEntity = sSearchEntity.substring(0,
				// sSearchEntity.indexOf(" ("));

				dataEntitySearcher.searchForEntity(sSearchEntity);
			}
		});

		// Gene search
		Label entitySearchLabel = new Label(searchComposite, SWT.NULL);
		entitySearchLabel.setText("Gene");
//		entitySearchLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

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
		
		if (bHorizontal)
		{
			Label spacer = new Label(group, SWT.NULL);
			spacer.setLayoutData(new GridData(GridData.FILL_BOTH));			
		}
		
		Label label = new Label(group, SWT.CENTER);
		label.setText("Search");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	}

	private void addColorMappingBar()
	{
	    Group group = new Group(parentComposite, SWT.NULL);
	    group.setLayout(new GridLayout(1, false));

	    GridData gridData;
	    if (bHorizontal)
	    {
	    	gridData = new GridData(GridData.FILL_VERTICAL);
	    	gridData.minimumWidth = 140;
	    	gridData.widthHint = 140;
	    }
	    else
	    {
	    	gridData = new GridData(GridData.FILL_HORIZONTAL);
	    }
    	group.setLayoutData(gridData);
	    
		CLabel colorMappingPreviewLabel = new CLabel(group, SWT.SHADOW_IN);
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

		if (bHorizontal)
		{
			Label spacer = new Label(group, SWT.NULL);
			spacer.setLayoutData(new GridData(GridData.FILL_BOTH));			
		}
		
		Label label = new Label(group, SWT.CENTER);
		label.setText("Color Mapping");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	}

	private void addGeneralToolBar()
	{
	    Group group = new Group(parentComposite, SWT.NULL);
	    group.setLayout(new GridLayout(1, false));
	    if (bHorizontal)
	    	group.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	    else
	    	group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
		final ToolBar toolBar = new ToolBar(group, SWT.WRAP | SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		toolBarManager.add(new ExportDataAction());
		toolBarManager.add(new TakeSnapshotAction());
		toolBarManager.update(true);

//		final ToolBar toolBar2 = new ToolBar(group, SWT.WRAP | SWT.FLAT);
//		ToolBarManager toolBarManager2 = new ToolBarManager(toolBar2);
//		toolBarManager2.add(new ExportDataAction());
//		toolBarManager2.add(new TakeSnapshotAction());
//		toolBarManager2.update(true);

		if (bHorizontal)
		{
			Label spacer = new Label(group, SWT.NULL);
			spacer.setLayoutData(new GridData(GridData.FILL_BOTH));			
		}
		
		Label label = new Label(group, SWT.CENTER);
		label.setText("General");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	}

	private void addInfoBar()
	{
	    Group group = new Group(parentComposite, SWT.NULL);
	    group.setLayout(new GridLayout(1, false));
	    if (bHorizontal)
	    	group.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	    else
	    	group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
		Composite infoComposite = new Composite(group, SWT.NULL);
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    if (bHorizontal)
	    	infoComposite.setLayout(new GridLayout(2, false));
	    else
	    	infoComposite.setLayout(new GridLayout(1, false));
	    
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
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer)
	{
		// TODO Auto-generated method stub

	}
	

//	@Override
//	public int computePreferredSize(boolean width, int availableParallel,
//			int availablePerpendicular, int preferredResult)
//	{
//		// Set minimum size of the view
//		if (width == true)
//			return (int)(ToolBarView.TOOLBAR_WIDTH * 1.25f);
//		
//		return 1000;
//	}
//
//	@Override
//	public int getSizeFlags(boolean width)
//	{
//		return SWT.MIN;
//	}
}
