package cerberus.view.gui.swt.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolItem;

import cerberus.view.gui.opengl.canvas.pathway.GLCanvasPathway3D;

public class Pathway3DToolbar 
extends AToolbar {

	protected GLCanvasPathway3D refPathwayViewRep;
	
	protected ToolItem refShowPathwayTextureItem;
	
	public Pathway3DToolbar(Composite refSWTContainer,
			 GLCanvasPathway3D refPathwayViewRep) {

		super(refSWTContainer);
		
		this.refPathwayViewRep = refPathwayViewRep;

		initToolbar();
		createActionListener();
	}

	protected void initToolbar() {
		
		super.initToolbar();
		
		refShowPathwayTextureItem = createToolItem(refToolBar,
				SWT.CHECK,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/background_image.gif"),
				null,
				"Show pathway texture");
	}	
	
	protected void createActionListener() {
		
	    Listener toolbarListener = new Listener() {
	        public void handleEvent(Event event) {
	          ToolItem clickedToolItem = (ToolItem) event.widget;
	          String sToolItemIdentifier = ((String)clickedToolItem.getToolTipText());
	          
	          if (sToolItemIdentifier.equals("Show pathway texture"))
	          {
	        	  refPathwayViewRep.showBackgroundOverlay(refShowPathwayTextureItem.getSelection());
	          }
	        }
	    };
	    
	    refShowPathwayTextureItem.addListener(SWT.Selection, toolbarListener);
	}
}
