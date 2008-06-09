package org.caleydo.core.view.swt.toolbar;

import java.awt.Dimension;
import java.io.IOException;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.view.swt.pathway.APathwayGraphViewRep;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

public class Pathway2DToolbar 
extends AToolbar {

	private static String ADD_ENZYME_ICON_PATH = "resources/icons/PathwayEditor/add.gif";
	private static String ZOOM_ORIG_ICON_PATH = "resources/icons/PathwayEditor/zoom100.gif";
	private static String ZOOM_IN_ICON_PATH = "resources/icons/PathwayEditor/zoomin.gif";
	private static String ZOOM_OUT_ICON_PATH = "resources/icons/PathwayEditor/zoomout.gif";
	private static String ONE_NEIGHBORHOOD_ICON_PATH = "resources/icons/PathwayEditor/one_neighborhood.gif";
	private static String TWO_NEIGHBORHOOD_ICON_PATH = "resources/icons/PathwayEditor/two_neighborhood.gif";
	private static String THREE_NEIGHBORHOOD_ICON_PATH = "resources/icons/PathwayEditor/three_neighborhood.gif";
	private static String OVERVIEW_MAP_ICON_PATH = "resources/icons/PathwayEditor/overview_map.gif";
	private static String BACKGROUND_OVERLAY_ICON_PATH = "resources/icons/PathwayEditor/background_image.gif";
	private static String HOME_ICON_PATH = "resources/icons/PathwayEditor/home.gif";
	
	protected APathwayGraphViewRep pathwayGraphViewRep;
	
	protected IGeneralManager generalManager;
	
	protected ToolItem addEnzymeNodeItem;
	protected ToolItem zoomOrigItem;
	protected ToolItem zoomInItem;
	protected ToolItem zoomOutItem;
	protected ToolItem oneNeighborhoodItem;
	protected ToolItem twoNeighborhoodItem;
	protected ToolItem threeNeighborhoodItem;
	protected ToolItem showOverviewMapItem;
	protected ToolItem filterEdgesItem;
	protected ToolItem backgroundOverlayItem;
	protected ToolItem keggMetabolicPathwaysMapItem;
	
	/**
	 * Constructor.
	 * 
	 * @param swtContainer
	 * @param pathwayGraphViewRep
	 * @param generalManager
	 */
	public Pathway2DToolbar(Composite swtContainer,
			APathwayGraphViewRep pathwayGraphViewRep,
			final IGeneralManager generalManager) {

		super(swtContainer);
		
		this.pathwayGraphViewRep = pathwayGraphViewRep;
		this.generalManager = generalManager;

		initToolbar();
		createActionListener();
	}
	
	protected void initToolbar() {
		
		super.initToolbar();
		try
		{	
			Image addEnzymeIcon;
			if (this.getClass().getClassLoader().getResource(ADD_ENZYME_ICON_PATH) != null)
			{
				addEnzymeIcon = new Image(swtContainer.getDisplay(), 
						this.getClass().getClassLoader().getResource(ADD_ENZYME_ICON_PATH).openStream());
			}
			else
			{
				addEnzymeIcon = new Image(swtContainer.getDisplay(), ADD_ENZYME_ICON_PATH);
			}
			
			addEnzymeNodeItem = createToolItem(toolBar,
					SWT.PUSH,
					"",
					addEnzymeIcon,
					null,
					"Add enzyme");
		
			Image zoomOrigIcon;
			if (this.getClass().getClassLoader().getResource(ZOOM_ORIG_ICON_PATH) != null)
			{
				zoomOrigIcon = new Image(swtContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(ZOOM_ORIG_ICON_PATH));
			}
			else
			{
				zoomOrigIcon = new Image(swtContainer.getDisplay(), ZOOM_ORIG_ICON_PATH);
			}
			
			zoomOrigItem = createToolItem(toolBar,
					SWT.PUSH,
					"",
					zoomOrigIcon,
					null,
					"Zoom standard");
		
			Image zoomInIcon;
			if (this.getClass().getClassLoader().getResource(ZOOM_IN_ICON_PATH) != null)
			{
				zoomInIcon = new Image(swtContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(ZOOM_IN_ICON_PATH));
			}
			else
			{
				zoomInIcon = new Image(swtContainer.getDisplay(), ZOOM_IN_ICON_PATH);
			}
			
		    zoomInItem = createToolItem(toolBar,
					SWT.PUSH,
					"",
					zoomInIcon,
					null,
					"Zoom in");
		    
			Image zoomOutIcon;
			if (this.getClass().getClassLoader().getResource(ZOOM_OUT_ICON_PATH) != null)
			{
				zoomOutIcon = new Image(swtContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(ZOOM_OUT_ICON_PATH));
			}
			else
			{
				zoomOutIcon = new Image(swtContainer.getDisplay(), ZOOM_OUT_ICON_PATH);
			}
		    
			zoomOutItem = createToolItem(toolBar,
					SWT.PUSH,
					"",
					zoomOutIcon,
					null,
					"Zoom out");
		    
		  	new ToolItem(toolBar, SWT.SEPARATOR);
			
			Image oneNeighborhoodIcon;
			if (this.getClass().getClassLoader().getResource(ONE_NEIGHBORHOOD_ICON_PATH) != null)
			{
				oneNeighborhoodIcon = new Image(swtContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(ONE_NEIGHBORHOOD_ICON_PATH));
			}
			else
			{
				oneNeighborhoodIcon = new Image(swtContainer.getDisplay(), ONE_NEIGHBORHOOD_ICON_PATH);
			}
		  	
			oneNeighborhoodItem = createToolItem(toolBar,
					SWT.CHECK,
					"",
					oneNeighborhoodIcon,
					null,
					"Show neighborhood within a distance of 1");
			
			Image twoNeighborhoodIcon;
			if (this.getClass().getClassLoader().getResource(TWO_NEIGHBORHOOD_ICON_PATH) != null)
			{
				twoNeighborhoodIcon = new Image(swtContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(TWO_NEIGHBORHOOD_ICON_PATH));
			}
			else
			{
				twoNeighborhoodIcon = new Image(swtContainer.getDisplay(), TWO_NEIGHBORHOOD_ICON_PATH);
			}
			
			twoNeighborhoodItem = createToolItem(toolBar,
					SWT.CHECK,
					"",
					twoNeighborhoodIcon,
					null,
					"Show neighborhood within a distance of 2");
			
			Image threeNeighborhoodIcon;
			if (this.getClass().getClassLoader().getResource(THREE_NEIGHBORHOOD_ICON_PATH) != null)
			{
				threeNeighborhoodIcon = new Image(swtContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(THREE_NEIGHBORHOOD_ICON_PATH));
			}
			else
			{
				threeNeighborhoodIcon = new Image(swtContainer.getDisplay(), THREE_NEIGHBORHOOD_ICON_PATH);
			}
			
			threeNeighborhoodItem = createToolItem(toolBar,
					SWT.CHECK,
					"",
					threeNeighborhoodIcon,
					null,
					"Show neighborhood within a distance of 3");
			
		  	new ToolItem(toolBar, SWT.SEPARATOR);
			
			Image overviewMapIcon;
			if (this.getClass().getClassLoader().getResource(OVERVIEW_MAP_ICON_PATH) != null)
			{
				overviewMapIcon = new Image(swtContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(OVERVIEW_MAP_ICON_PATH));
			}
			else
			{
				overviewMapIcon = new Image(swtContainer.getDisplay(), OVERVIEW_MAP_ICON_PATH);
			}
		  	
			showOverviewMapItem = createToolItem(toolBar,
					SWT.PUSH,
					"",
					overviewMapIcon,
					null,
					"Show overview map");
			
			Image backgroundOverlayIcon;
			if (this.getClass().getClassLoader().getResource(BACKGROUND_OVERLAY_ICON_PATH) != null)
			{
				backgroundOverlayIcon = new Image(swtContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(BACKGROUND_OVERLAY_ICON_PATH));
			}
			else
			{
				backgroundOverlayIcon = new Image(swtContainer.getDisplay(), BACKGROUND_OVERLAY_ICON_PATH);
			}
			
			backgroundOverlayItem = createToolItem(toolBar,
					SWT.CHECK,
					"",
					backgroundOverlayIcon,
					null,
					"Show background overlay");
			backgroundOverlayItem.setSelection(true);
			
			filterEdgesItem = createToolItem(toolBar, 
					SWT.DROP_DOWN, 
					"Filter edges",
					null, 
					null, 
					"Filter reactions/relations");
			
			Image homeIcon;
			if (this.getClass().getClassLoader().getResource(HOME_ICON_PATH) != null)
			{
				homeIcon = new Image(swtContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(HOME_ICON_PATH));
			}
			else
			{
				homeIcon = new Image(swtContainer.getDisplay(), HOME_ICON_PATH);
			}
			
			keggMetabolicPathwaysMapItem = createToolItem(toolBar,
					SWT.PUSH,
					"",
					homeIcon,
					null,
					"Go to KEGG Metabolic Pathways Overview Map");	
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void createActionListener() {
		
		final Menu filterEdgeMenu = new Menu (swtContainer.getShell(), SWT.POP_UP);
		final MenuItem showRelationsItem = new MenuItem (filterEdgeMenu, SWT.CHECK);
		showRelationsItem.setText ("Show relations");
		final MenuItem showReactionsItem = new MenuItem (filterEdgeMenu, SWT.CHECK);
		showReactionsItem.setText ("Show reactions");	
		
		filterEdgesItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				Rectangle rect = filterEdgesItem.getBounds ();
				Point pt = new Point (rect.x, rect.y + rect.height);
				pt = toolBar.toDisplay (pt);
				filterEdgeMenu.setLocation (pt.x, pt.y);
				filterEdgeMenu.setVisible (true);	
			}
		});
		
		Listener edgeFilterListener = new Listener () {
			public void handleEvent (Event event) {
		
				if (!event.widget.getClass().getSimpleName().equals("MenuItem"))
				{
					return;	
				}
	
//				MenuItem clickedMenuItem = ((MenuItem)event.widget);
//				
//				if (((MenuItem)event.widget).getText().equals("Show relations"))
//				{
//					pathwayGraphViewRep.showHideEdgesByType(
//							clickedMenuItem.getSelection(),
//							EdgeType.RELATION);
//				}
//				else if (((MenuItem)event.widget).getText().equals("Show reactions"))
//				{
//					pathwayGraphViewRep.showHideEdgesByType(
//							clickedMenuItem.getSelection(),
//							EdgeType.REACTION);
//				}					
				
//				// Get current states of edge filter
//				showReactionsItem.setSelection(pathwayGraphViewRep.getEdgeVisibilityStateByType(EdgeType.REACTION));
//				showRelationsItem.setSelection(pathwayGraphViewRep.getEdgeVisibilityStateByType(EdgeType.RELATION));
			}
		};	
		
	    Listener toolbarListener = new Listener() {
	        public void handleEvent(Event event) {
	          ToolItem clickedToolItem = (ToolItem) event.widget;
	          String sToolItemIdentifier = ((String)clickedToolItem.getToolTipText());
	          
	          if (sToolItemIdentifier.equals("Zoom standard"))
	          {
	        	  pathwayGraphViewRep.zoomOrig();
	          }
	          else if (sToolItemIdentifier.equals("Zoom in"))
	          {
	        	  pathwayGraphViewRep.zoomIn();  
	          }
	          else if (sToolItemIdentifier.equals("Zoom out"))
	          {
	        	  pathwayGraphViewRep.zoomOut();
	          }	   
	          else if (sToolItemIdentifier.equals(
	        		  "Show neighborhood within a distance of 1"))
	          {
	        	  twoNeighborhoodItem.setSelection(false);
	        	  threeNeighborhoodItem.setSelection(false);
	        	  
	        	  if (oneNeighborhoodItem.getSelection() == true)
	        	  {
		        	  pathwayGraphViewRep.setNeighbourhoodDistance(1);
	        	  }
	        	  else
	        	  {
	        		  pathwayGraphViewRep.setNeighbourhoodDistance(0);
	        	  }
	
	          }
	          else if (sToolItemIdentifier.equals(
	        		  "Show neighborhood within a distance of 2"))
	          {
	        	  oneNeighborhoodItem.setSelection(false);
	        	  threeNeighborhoodItem.setSelection(false);
	        	  
	        	  if (twoNeighborhoodItem.getSelection() == true)
	        	  {
		        	  pathwayGraphViewRep.setNeighbourhoodDistance(2);
	        	  }
	        	  else
	        	  {
	        		  pathwayGraphViewRep.setNeighbourhoodDistance(0);
	        	  }
	          } 
	          else if (sToolItemIdentifier.equals(
						"Show neighborhood within a distance of 3"))
	          {
	        	  oneNeighborhoodItem.setSelection(false);
	        	  twoNeighborhoodItem.setSelection(false);
	        	  
	        	  if (threeNeighborhoodItem.getSelection() == true)
	        	  {
		        	  pathwayGraphViewRep.setNeighbourhoodDistance(3);
	        	  }
	        	  else
	        	  {
	        		  pathwayGraphViewRep.setNeighbourhoodDistance(0);
	        	  }
	          }
	          else if (sToolItemIdentifier.equals("Show overview map"))
	          {
	        	  pathwayGraphViewRep.
	        	  	showOverviewMapInNewWindow(new Dimension(250, 250));
	          }
	          else if (sToolItemIdentifier.equals("Show background overlay"))
	          {
	        	  pathwayGraphViewRep.showBackgroundOverlay(
	        			  backgroundOverlayItem.getSelection());
	        	  
	        	  if (backgroundOverlayItem.getSelection() == true)
	        	  {
	        		  showReactionsItem.setSelection(false);
	        		  showRelationsItem.setSelection(false);
	        		  
//	        		  pathwayGraphViewRep.showHideEdgesByType(false, EdgeType.REACTION);
//	        		  pathwayGraphViewRep.showHideEdgesByType(false, EdgeType.RELATION);	        		  
	        	  }
	        	  else
	        	  {
	        		  showReactionsItem.setSelection(true);
	        		  showRelationsItem.setSelection(true);

//	        		  pathwayGraphViewRep.showHideEdgesByType(true, EdgeType.REACTION);
//	        		  pathwayGraphViewRep.showHideEdgesByType(true, EdgeType.RELATION);	        		  
	        	  }
	          }
	          else if (sToolItemIdentifier.equals("" +
	          		"Go to KEGG Metabolic Pathways Overview Map"))
	          {
	        	  pathwayGraphViewRep.setPathwayLevel(1);
	        	  
	        	  pathwayGraphViewRep.loadImageMapFromFile(
	        			  generalManager.getPathwayManager()
	        			  		.getPathwayDatabaseByType(EPathwayDatabaseType.KEGG).
	        			  				getImageMapPath() + "map01100.xml");
	          }
	        }
	      };
	      
	      zoomOrigItem.addListener(SWT.Selection, toolbarListener);
	      zoomInItem.addListener(SWT.Selection, toolbarListener);
	      zoomOutItem.addListener(SWT.Selection, toolbarListener);
	      oneNeighborhoodItem.addListener(SWT.Selection, toolbarListener);
	      twoNeighborhoodItem.addListener(SWT.Selection, toolbarListener);
	      threeNeighborhoodItem.addListener(SWT.Selection, toolbarListener);
	      showOverviewMapItem.addListener(SWT.Selection, toolbarListener);
	      backgroundOverlayItem.addListener(SWT.Selection, toolbarListener);
	      keggMetabolicPathwaysMapItem.addListener(SWT.Selection, toolbarListener);
	      
		  showRelationsItem.addListener(SWT.Selection, edgeFilterListener);
		  showReactionsItem.addListener(SWT.Selection, edgeFilterListener);
	}
	
}
