/**
 * 
 */
package cerberus.view.gui.swt.pathway;

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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.pathway.jgraph.PathwayGraphViewRep;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

/**
 * Image view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class PathwayViewRep 
extends AViewRep 
implements IView {
	
	protected Composite refSWTContainer;
	
	protected int iHTMLBrowserId;
	
	protected ToolBar refToolBar;
	
	protected ToolItem refAddEnzymeNodeItem;
	protected ToolItem refZoomOrigItem;
	protected ToolItem refZoomInItem;
	protected ToolItem refZoomOutItem;
	protected ToolItem refNeighbourItem;
	protected ToolItem refShowOverviewMapItem;
	protected ToolItem refFilterEdgesItem;
	
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
			
		initToolbar();
		
//		// Graph initialization
		refPathwayGraphViewRep.setExternalGUIContainer(refSWTContainer);
		refPathwayGraphViewRep.setWidthAndHeight(iWidth, iHeight-30);
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
	    
		refNeighbourItem = createToolItem(refToolBar,
				SWT.PUSH,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/neighbour.gif"),
				null,
				"Change neighbourhood distance");
		
		refShowOverviewMapItem = createToolItem(refToolBar,
				SWT.PUSH,
				"Show overview map",
				null,
				null,
				"Show overview map");
		
		refFilterEdgesItem = createToolItem(refToolBar, 
				SWT.DROP_DOWN, 
				"Filter edges",
				null, 
				null, 
				"Filter reactions/relations");
		
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
		
				System.out.println("Name: " +event.widget.getClass().getSimpleName());

				if (!event.widget.getClass().getSimpleName().equals("MenuItem"))
					return;	

				MenuItem clickedMenuItem = ((MenuItem)event.widget);
				
				if (((MenuItem)event.widget).getText().equals("Show relations"))
				{
					refPathwayGraphViewRep.showRelationEdges(clickedMenuItem.getSelection());
				}
				else if (((MenuItem)event.widget).getText().equals("Show reactions"))
				{
					System.out.println("SELECTED: " +clickedMenuItem.getSelection());
					refPathwayGraphViewRep.showReactionEdges(clickedMenuItem.getSelection());
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
	          else if (sToolItemIdentifier.equals("Change neighbourhood distance"))
	          {
	        	  //refPathwayGraphViewRep.setNeighbourhoodDistance(2);
	          }
	          else if (sToolItemIdentifier.equals("Show overview map"))
	          {
	        	  refPathwayGraphViewRep.
	        	  	showOverviewMapInNewWindow(new Dimension(250, 250));
	          }
	        }
	      };
	      
	      refZoomOrigItem.addListener(SWT.Selection, toolbarListener);
	      refZoomInItem.addListener(SWT.Selection, toolbarListener);
	      refZoomOutItem.addListener(SWT.Selection, toolbarListener);
	      refNeighbourItem.addListener(SWT.Selection, toolbarListener);
	      refShowOverviewMapItem.addListener(SWT.Selection, toolbarListener);
	      
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
