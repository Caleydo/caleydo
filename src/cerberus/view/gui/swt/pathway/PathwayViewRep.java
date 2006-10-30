/**
 * 
 */
package cerberus.view.gui.swt.pathway;

import java.awt.Dimension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import cerberus.data.pathway.element.APathwayEdge.EdgeType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.pathway.jgraph.NeighborhoodInputDialog;
import cerberus.view.gui.swt.pathway.jgraph.PathwayGraphViewRep;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

/**
 * Pathway view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class PathwayViewRep 
extends AViewRep 
implements IView {
	
	protected static final String KEGG_OVERVIEW_PATHWAY_IMAGE_MAP_PATH = 
		"data/XML/imagemap/map01100.xml";
	
	protected Composite refSWTContainer;
	
	protected int iHTMLBrowserId;
	
	protected ToolBar refToolBar;
	
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
	
	protected APathwayGraphViewRep refPathwayGraphViewRep;
	
	public PathwayViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);	
		
		// Pass the ID of the parent composite to the graph view rep instead of 
		// the ID of the PathwayViewRep.
		// That means that the PathwayViewRep and PathwayGraphViewRep are both
		// put in the same parent composite.
		refPathwayGraphViewRep = 
			new PathwayGraphViewRep(refGeneralManager, iViewId);
	}

	public void initView() {
			
		refSWTContainer.setLayout(new GridLayout(1, false));
		
		initToolbar();
		
		// Graph initialization
		refPathwayGraphViewRep.setExternalGUIContainer(refSWTContainer);
		refPathwayGraphViewRep.setWidthAndHeight(iWidth-5, iHeight-50);
		refPathwayGraphViewRep.setHTMLBrowserId(iHTMLBrowserId);
		refPathwayGraphViewRep.retrieveGUIContainer();
		refPathwayGraphViewRep.initView();
		refPathwayGraphViewRep.drawView();
	}

	public void drawView() {
		

	}
	
	public void retrieveGUIContainer() {
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}

	/**
	 * Retrieves the HTML browser ID.
	 */
	public void extractAttributes() {
		
		iHTMLBrowserId = 
			refParameterHandler.getValueInt( "iHTMLBrowserId" );
	}
	
	protected void initToolbar() {
		refToolBar = new ToolBar(refSWTContainer, SWT.NONE);
		refToolBar.setBounds(0, 0, iWidth, 30);
			    
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
		
		final Menu filterEdgeMenu = new Menu (refSWTContainer.getShell(), SWT.POP_UP);
		MenuItem refShowRelationsItem = new MenuItem (filterEdgeMenu, SWT.CHECK);
		refShowRelationsItem.setText ("Show relations");
		refShowRelationsItem.setSelection(true);
		MenuItem refShowReactionsItem = new MenuItem (filterEdgeMenu, SWT.CHECK);
		refShowReactionsItem.setText ("Show reactions");
		refShowReactionsItem.setSelection(true);
		
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
	          }
	          else if (sToolItemIdentifier.equals("" +
	          		"Go to KEGG Metabolic Pathways Overview Map"))
	          {
	        	  refPathwayGraphViewRep.loadImageMapFromFile(
	        			  KEGG_OVERVIEW_PATHWAY_IMAGE_MAP_PATH);
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
	
	/**
   	* Helper function to create tool item
   	* 
   	* @param parent the parent toolbar
   	* @param type the type of tool item to create
   	* @param text the text to display on the tool item
   	* @param image the image to display on the tool item
   	* @param hotImage the hot image to display on the tool item
   	* @param toolTipText the tool tip text for the tool item
   	* @return ToolItem
   	*/
	private ToolItem createToolItem(ToolBar parent, int type, String text,
		  Image image, Image hotImage, String toolTipText) {
		
	  	ToolItem item = new ToolItem(parent, type);
    	item.setText(text);
    	item.setImage(image);
    	item.setHotImage(hotImage);
    	item.setToolTipText(toolTipText);
    	return item;
  	}
}
