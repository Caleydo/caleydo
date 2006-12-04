package cerberus.view.gui.swt.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;

import cerberus.view.gui.opengl.canvas.pathway.GLCanvasPathway3D;

public class Pathway3DToolbar 
extends AToolbar {

	protected GLCanvasPathway3D refPathwayViewRep;
	
	protected ToolItem refTestItem;
	
	public Pathway3DToolbar(Composite refSWTContainer,
			 GLCanvasPathway3D refPathwayViewRep) {

		super(refSWTContainer);
		
		this.refPathwayViewRep = refPathwayViewRep;

		initToolbar();
	}

	protected void initToolbar() {
		
		super.initToolbar();
		
		refTestItem = createToolItem(refToolBar,
				SWT.PUSH,
				"",
				new Image(refSWTContainer.getDisplay(), "data/icons/PathwayEditor/add.gif"),
				null,
				"Test tool item");
	}	
}
