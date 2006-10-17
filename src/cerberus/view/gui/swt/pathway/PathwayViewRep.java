/**
 * 
 */
package cerberus.view.gui.swt.pathway;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.PathwayEdge;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.data.view.rep.pathway.jgraph.PathwayVertexRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.pathway.PathwayManager;
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
		
		refAddEnzymeNodeItem = new ToolItem(refToolBar, SWT.PUSH);
	    refAddEnzymeNodeItem.setText("Add enzyme");
	    
	    refZoomOrigItem = new ToolItem(refToolBar, SWT.PUSH);
	    refZoomOrigItem.setText("Zoom orig");
	    
		refZoomInItem = new ToolItem(refToolBar, SWT.PUSH);
	    refZoomInItem.setText("Zoom in");
	    
		refZoomOutItem = new ToolItem(refToolBar, SWT.PUSH);
	    refZoomOutItem.setText("Zoom out");
	    
	    Listener toolbarListener = new Listener() {
	        public void handleEvent(Event event) {
	          ToolItem clickedToolItem = (ToolItem) event.widget;
	          String clickedToolItemText = clickedToolItem.getText();
	          
	          if (clickedToolItemText.equals("Zoom orig"))
	          {
	        	  refPathwayGraphViewRep.zoomOrig();
	          }
	          else if (clickedToolItemText.equals("Zoom in"))
	          {
	        	  refPathwayGraphViewRep.zoomIn();  
	          }
	          else if (clickedToolItemText.equals("Zoom out"))
	          {
	        	  refPathwayGraphViewRep.zoomOut();
	          }	          
	        }
	      };
	      
	      refZoomOrigItem.addListener(SWT.Selection, toolbarListener);
	      refZoomInItem.addListener(SWT.Selection, toolbarListener);
	      refZoomOutItem.addListener(SWT.Selection, toolbarListener);
	}
}
