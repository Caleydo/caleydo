package cerberus.view.gui.swt.toolbar;

import java.awt.Dimension;

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

import cerberus.data.pathway.element.APathwayEdge.EdgeType;
import cerberus.manager.IGeneralManager;
import cerberus.view.gui.swt.pathway.APathwayGraphViewRep;

public class Pathway2DToolbar 
extends AToolbar {

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
		
		refAddEnzymeNodeItem = createToolItem(refToolBar,
				SWT.PUSH,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/add.gif"),
				null,
				"Add enzyme");
	
		refZoomOrigItem = createToolItem(refToolBar,
				SWT.PUSH,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/zoom100.gif"),
				null,
				"Zoom standard");
	
	    refZoomInItem = createToolItem(refToolBar,
				SWT.PUSH,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/zoomin.gif"),
				null,
				"Zoom in");
	    
		refZoomOutItem = createToolItem(refToolBar,
				SWT.PUSH,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/zoomout.gif"),
				null,
				"Zoom out");
	    
	  	new ToolItem(refToolBar, SWT.SEPARATOR);
		
		refOneNeighborhoodItem = createToolItem(refToolBar,
				SWT.CHECK,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/one_neighborhood.gif"),
				null,
				"Show neighborhood within a distance of 1");
		
		refTwoNeighborhoodItem = createToolItem(refToolBar,
				SWT.CHECK,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/two_neighborhood.gif"),
				null,
				"Show neighborhood within a distance of 2");
		
		refThreeNeighborhoodItem = createToolItem(refToolBar,
				SWT.CHECK,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/three_neighborhood.gif"),
				null,
				"Show neighborhood within a distance of 3");
		
	  	new ToolItem(refToolBar, SWT.SEPARATOR);
		
		refShowOverviewMapItem = createToolItem(refToolBar,
				SWT.PUSH,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/overview_map.gif"),
				null,
				"Show overview map");
		
		refBackgroundOverlayItem = createToolItem(refToolBar,
				SWT.CHECK,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/background_image.gif"),
				null,
				"Show background overlay");
		refBackgroundOverlayItem.setSelection(true);
		
		refFilterEdgesItem = createToolItem(refToolBar, 
				SWT.DROP_DOWN, 
				"Filter edges",
				null, 
				null, 
				"Filter reactions/relations");
		
		refKeggMetabolicPathwaysMapItem = createToolItem(refToolBar,
				SWT.PUSH,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/home.gif"),
				null,
				"Go to KEGG Metabolic Pathways Overview Map");	  
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
	
				MenuItem clickedMenuItem = ((MenuItem)event.widget);
				
				if (((MenuItem)event.widget).getText().equals("Show relations"))
				{
					refPathwayGraphViewRep.showHideEdgesByType(
							clickedMenuItem.getSelection(),
							EdgeType.RELATION);
				}
				else if (((MenuItem)event.widget).getText().equals("Show reactions"))
				{
					refPathwayGraphViewRep.showHideEdgesByType(
							clickedMenuItem.getSelection(),
							EdgeType.REACTION);
				}					

				
				// Get current states of edge filter
				refShowReactionsItem.setSelection(refPathwayGraphViewRep.getEdgeVisibilityStateByType(EdgeType.REACTION));
				refShowRelationsItem.setSelection(refPathwayGraphViewRep.getEdgeVisibilityStateByType(EdgeType.RELATION));
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
	        	  }
	        	  else
	        	  {
	        		  refShowReactionsItem.setSelection(true);
	        		  refShowRelationsItem.setSelection(true);
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
