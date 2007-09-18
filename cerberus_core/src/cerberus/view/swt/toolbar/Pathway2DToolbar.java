package cerberus.view.swt.toolbar;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

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

import com.sun.opengl.util.texture.TextureIO;

import cerberus.manager.IGeneralManager;
import cerberus.view.swt.pathway.APathwayGraphViewRep;

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
	
	protected APathwayGraphViewRep refPathwayGraphViewRep;
	
	protected IGeneralManager refGeneralManager;
	
	protected ToolItem refAddEnzymeNodeItem;
	protected ToolItem refZoomOrigItem;
	protected ToolItem refZoomInItem;
	protected ToolItem refZoomOutItem;
	protected ToolItem refOneNeighborhoodItem;
	protected ToolItem refTwoNeighborhoodItem;
	protected ToolItem refThreeNeighborhoodItem;
	protected ToolItem refShowOverviewMapItem;
	protected ToolItem refFilterEdgesItem;
	protected ToolItem refBackgroundOverlayItem;
	protected ToolItem refKeggMetabolicPathwaysMapItem;
	
	public Pathway2DToolbar(Composite refSWTContainer,
			APathwayGraphViewRep refPathwayGraphViewRep,
			IGeneralManager refGeneralManager) {

		super(refSWTContainer);
		
		this.refPathwayGraphViewRep = refPathwayGraphViewRep;
		this.refGeneralManager = refGeneralManager;

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
				addEnzymeIcon = new Image(refSWTContainer.getDisplay(), 
						this.getClass().getClassLoader().getResource(ADD_ENZYME_ICON_PATH).openStream());
			}
			else
			{
				addEnzymeIcon = new Image(refSWTContainer.getDisplay(), ADD_ENZYME_ICON_PATH);
			}
			
			refAddEnzymeNodeItem = createToolItem(refToolBar,
					SWT.PUSH,
					"",
					addEnzymeIcon,
					null,
					"Add enzyme");
		
			Image zoomOrigIcon;
			if (this.getClass().getClassLoader().getResource(ZOOM_ORIG_ICON_PATH) != null)
			{
				zoomOrigIcon = new Image(refSWTContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(ZOOM_ORIG_ICON_PATH));
			}
			else
			{
				zoomOrigIcon = new Image(refSWTContainer.getDisplay(), ZOOM_ORIG_ICON_PATH);
			}
			
			refZoomOrigItem = createToolItem(refToolBar,
					SWT.PUSH,
					"",
					zoomOrigIcon,
					null,
					"Zoom standard");
		
			Image zoomInIcon;
			if (this.getClass().getClassLoader().getResource(ZOOM_IN_ICON_PATH) != null)
			{
				zoomInIcon = new Image(refSWTContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(ZOOM_IN_ICON_PATH));
			}
			else
			{
				zoomInIcon = new Image(refSWTContainer.getDisplay(), ZOOM_IN_ICON_PATH);
			}
			
		    refZoomInItem = createToolItem(refToolBar,
					SWT.PUSH,
					"",
					zoomInIcon,
					null,
					"Zoom in");
		    
			Image zoomOutIcon;
			if (this.getClass().getClassLoader().getResource(ZOOM_OUT_ICON_PATH) != null)
			{
				zoomOutIcon = new Image(refSWTContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(ZOOM_OUT_ICON_PATH));
			}
			else
			{
				zoomOutIcon = new Image(refSWTContainer.getDisplay(), ZOOM_OUT_ICON_PATH);
			}
		    
			refZoomOutItem = createToolItem(refToolBar,
					SWT.PUSH,
					"",
					zoomOutIcon,
					null,
					"Zoom out");
		    
		  	new ToolItem(refToolBar, SWT.SEPARATOR);
			
			Image oneNeighborhoodIcon;
			if (this.getClass().getClassLoader().getResource(ONE_NEIGHBORHOOD_ICON_PATH) != null)
			{
				oneNeighborhoodIcon = new Image(refSWTContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(ONE_NEIGHBORHOOD_ICON_PATH));
			}
			else
			{
				oneNeighborhoodIcon = new Image(refSWTContainer.getDisplay(), ONE_NEIGHBORHOOD_ICON_PATH);
			}
		  	
			refOneNeighborhoodItem = createToolItem(refToolBar,
					SWT.CHECK,
					"",
					oneNeighborhoodIcon,
					null,
					"Show neighborhood within a distance of 1");
			
			Image twoNeighborhoodIcon;
			if (this.getClass().getClassLoader().getResource(TWO_NEIGHBORHOOD_ICON_PATH) != null)
			{
				twoNeighborhoodIcon = new Image(refSWTContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(TWO_NEIGHBORHOOD_ICON_PATH));
			}
			else
			{
				twoNeighborhoodIcon = new Image(refSWTContainer.getDisplay(), TWO_NEIGHBORHOOD_ICON_PATH);
			}
			
			refTwoNeighborhoodItem = createToolItem(refToolBar,
					SWT.CHECK,
					"",
					twoNeighborhoodIcon,
					null,
					"Show neighborhood within a distance of 2");
			
			Image threeNeighborhoodIcon;
			if (this.getClass().getClassLoader().getResource(THREE_NEIGHBORHOOD_ICON_PATH) != null)
			{
				threeNeighborhoodIcon = new Image(refSWTContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(THREE_NEIGHBORHOOD_ICON_PATH));
			}
			else
			{
				threeNeighborhoodIcon = new Image(refSWTContainer.getDisplay(), THREE_NEIGHBORHOOD_ICON_PATH);
			}
			
			refThreeNeighborhoodItem = createToolItem(refToolBar,
					SWT.CHECK,
					"",
					threeNeighborhoodIcon,
					null,
					"Show neighborhood within a distance of 3");
			
		  	new ToolItem(refToolBar, SWT.SEPARATOR);
			
			Image overviewMapIcon;
			if (this.getClass().getClassLoader().getResource(OVERVIEW_MAP_ICON_PATH) != null)
			{
				overviewMapIcon = new Image(refSWTContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(OVERVIEW_MAP_ICON_PATH));
			}
			else
			{
				overviewMapIcon = new Image(refSWTContainer.getDisplay(), OVERVIEW_MAP_ICON_PATH);
			}
		  	
			refShowOverviewMapItem = createToolItem(refToolBar,
					SWT.PUSH,
					"",
					overviewMapIcon,
					null,
					"Show overview map");
			
			Image backgroundOverlayIcon;
			if (this.getClass().getClassLoader().getResource(BACKGROUND_OVERLAY_ICON_PATH) != null)
			{
				backgroundOverlayIcon = new Image(refSWTContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(BACKGROUND_OVERLAY_ICON_PATH));
			}
			else
			{
				backgroundOverlayIcon = new Image(refSWTContainer.getDisplay(), BACKGROUND_OVERLAY_ICON_PATH);
			}
			
			refBackgroundOverlayItem = createToolItem(refToolBar,
					SWT.CHECK,
					"",
					backgroundOverlayIcon,
					null,
					"Show background overlay");
			refBackgroundOverlayItem.setSelection(true);
			
			refFilterEdgesItem = createToolItem(refToolBar, 
					SWT.DROP_DOWN, 
					"Filter edges",
					null, 
					null, 
					"Filter reactions/relations");
			
			Image homeIcon;
			if (this.getClass().getClassLoader().getResource(HOME_ICON_PATH) != null)
			{
				homeIcon = new Image(refSWTContainer.getDisplay(), 
						this.getClass().getClassLoader().getResourceAsStream(HOME_ICON_PATH));
			}
			else
			{
				homeIcon = new Image(refSWTContainer.getDisplay(), HOME_ICON_PATH);
			}
			
			refKeggMetabolicPathwaysMapItem = createToolItem(refToolBar,
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
		
		final Menu filterEdgeMenu = new Menu (refSWTContainer.getShell(), SWT.POP_UP);
		final MenuItem refShowRelationsItem = new MenuItem (filterEdgeMenu, SWT.CHECK);
		refShowRelationsItem.setText ("Show relations");
		final MenuItem refShowReactionsItem = new MenuItem (filterEdgeMenu, SWT.CHECK);
		refShowReactionsItem.setText ("Show reactions");	
		
		refFilterEdgesItem.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				Rectangle rect = refFilterEdgesItem.getBounds ();
				Point pt = new Point (rect.x, rect.y + rect.height);
				pt = refToolBar.toDisplay (pt);
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
//					refPathwayGraphViewRep.showHideEdgesByType(
//							clickedMenuItem.getSelection(),
//							EdgeType.RELATION);
//				}
//				else if (((MenuItem)event.widget).getText().equals("Show reactions"))
//				{
//					refPathwayGraphViewRep.showHideEdgesByType(
//							clickedMenuItem.getSelection(),
//							EdgeType.REACTION);
//				}					
				
//				// Get current states of edge filter
//				refShowReactionsItem.setSelection(refPathwayGraphViewRep.getEdgeVisibilityStateByType(EdgeType.REACTION));
//				refShowRelationsItem.setSelection(refPathwayGraphViewRep.getEdgeVisibilityStateByType(EdgeType.RELATION));
			}
		};	
		
	    Listener toolbarListener = new Listener() {
	        public void handleEvent(Event event) {
	          ToolItem clickedToolItem = (ToolItem) event.widget;
	          String sToolItemIdentifier = ((String)clickedToolItem.getToolTipText());
	          
	          if (sToolItemIdentifier.equals("Zoom standard"))
	          {
	        	  refPathwayGraphViewRep.zoomOrig();
	          }
	          else if (sToolItemIdentifier.equals("Zoom in"))
	          {
	        	  refPathwayGraphViewRep.zoomIn();  
	          }
	          else if (sToolItemIdentifier.equals("Zoom out"))
	          {
	        	  refPathwayGraphViewRep.zoomOut();
	          }	   
	          else if (sToolItemIdentifier.equals(
	        		  "Show neighborhood within a distance of 1"))
	          {
	        	  refTwoNeighborhoodItem.setSelection(false);
	        	  refThreeNeighborhoodItem.setSelection(false);
	        	  
	        	  if (refOneNeighborhoodItem.getSelection() == true)
	        	  {
		        	  refPathwayGraphViewRep.setNeighbourhoodDistance(1);
	        	  }
	        	  else
	        	  {
	        		  refPathwayGraphViewRep.setNeighbourhoodDistance(0);
	        	  }
	
	          }
	          else if (sToolItemIdentifier.equals(
	        		  "Show neighborhood within a distance of 2"))
	          {
	        	  refOneNeighborhoodItem.setSelection(false);
	        	  refThreeNeighborhoodItem.setSelection(false);
	        	  
	        	  if (refTwoNeighborhoodItem.getSelection() == true)
	        	  {
		        	  refPathwayGraphViewRep.setNeighbourhoodDistance(2);
	        	  }
	        	  else
	        	  {
	        		  refPathwayGraphViewRep.setNeighbourhoodDistance(0);
	        	  }
	          } 
	          else if (sToolItemIdentifier.equals(
						"Show neighborhood within a distance of 3"))
	          {
	        	  refOneNeighborhoodItem.setSelection(false);
	        	  refTwoNeighborhoodItem.setSelection(false);
	        	  
	        	  if (refThreeNeighborhoodItem.getSelection() == true)
	        	  {
		        	  refPathwayGraphViewRep.setNeighbourhoodDistance(3);
	        	  }
	        	  else
	        	  {
	        		  refPathwayGraphViewRep.setNeighbourhoodDistance(0);
	        	  }
	          }
	          else if (sToolItemIdentifier.equals("Show overview map"))
	          {
	        	  refPathwayGraphViewRep.
	        	  	showOverviewMapInNewWindow(new Dimension(250, 250));
	          }
	          else if (sToolItemIdentifier.equals("Show background overlay"))
	          {
	        	  refPathwayGraphViewRep.showBackgroundOverlay(
	        			  refBackgroundOverlayItem.getSelection());
	        	  
	        	  if (refBackgroundOverlayItem.getSelection() == true)
	        	  {
	        		  refShowReactionsItem.setSelection(false);
	        		  refShowRelationsItem.setSelection(false);
	        		  
//	        		  refPathwayGraphViewRep.showHideEdgesByType(false, EdgeType.REACTION);
//	        		  refPathwayGraphViewRep.showHideEdgesByType(false, EdgeType.RELATION);	        		  
	        	  }
	        	  else
	        	  {
	        		  refShowReactionsItem.setSelection(true);
	        		  refShowRelationsItem.setSelection(true);

//	        		  refPathwayGraphViewRep.showHideEdgesByType(true, EdgeType.REACTION);
//	        		  refPathwayGraphViewRep.showHideEdgesByType(true, EdgeType.RELATION);	        		  
	        	  }
	          }
	          else if (sToolItemIdentifier.equals("" +
	          		"Go to KEGG Metabolic Pathways Overview Map"))
	          {
	        	  refPathwayGraphViewRep.setPathwayLevel(1);
	        	  
	        	  refPathwayGraphViewRep.loadImageMapFromFile(
	        			  refGeneralManager.getSingelton().getPathwayManager().
	        			  	getPathwayImageMapPath() + "map01100.xml");
	          }
	        }
	      };
	      
	      refZoomOrigItem.addListener(SWT.Selection, toolbarListener);
	      refZoomInItem.addListener(SWT.Selection, toolbarListener);
	      refZoomOutItem.addListener(SWT.Selection, toolbarListener);
	      refOneNeighborhoodItem.addListener(SWT.Selection, toolbarListener);
	      refTwoNeighborhoodItem.addListener(SWT.Selection, toolbarListener);
	      refThreeNeighborhoodItem.addListener(SWT.Selection, toolbarListener);
	      refShowOverviewMapItem.addListener(SWT.Selection, toolbarListener);
	      refBackgroundOverlayItem.addListener(SWT.Selection, toolbarListener);
	      refKeggMetabolicPathwaysMapItem.addListener(SWT.Selection, toolbarListener);
	      
		  refShowRelationsItem.addListener(SWT.Selection, edgeFilterListener);
		  refShowReactionsItem.addListener(SWT.Selection, edgeFilterListener);
	}
	
}
