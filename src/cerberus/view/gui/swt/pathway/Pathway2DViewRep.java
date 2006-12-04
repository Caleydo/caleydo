/**
 * 
 */
package cerberus.view.gui.swt.pathway;

import org.eclipse.swt.layout.GridLayout;

import cerberus.manager.IGeneralManager;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.pathway.jgraph.PathwayGraphViewRep;
import cerberus.view.gui.swt.toolbar.Pathway2DToolbar;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

/**
 * Pathway view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class Pathway2DViewRep 
extends AViewRep 
implements IView, IMediatorSender {
	
	protected int iHTMLBrowserId;
	
	protected APathwayGraphViewRep refPathwayGraphViewRep;
	
	public Pathway2DViewRep(
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
		
		new Pathway2DToolbar(refSWTContainer, refPathwayGraphViewRep);
		
		// Graph initialization
		refPathwayGraphViewRep.setExternalGUIContainer(refSWTContainer);
		refPathwayGraphViewRep.setWidthAndHeight(iWidth-5, iHeight-75);
		refPathwayGraphViewRep.setHTMLBrowserId(iHTMLBrowserId);
		refPathwayGraphViewRep.retrieveGUIContainer();
		refPathwayGraphViewRep.initView();
		refPathwayGraphViewRep.drawView();
	}

	/**
	 * Retrieves the HTML browser ID.
	 */
	public void extractAttributes() {
		
		iHTMLBrowserId = 
			refParameterHandler.getValueInt( "iHTMLBrowserId" );
	}

	public void drawView() {

		// TODO Auto-generated method stub

	}
	
	final public void retrieveGUIContainer() {
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}
}
