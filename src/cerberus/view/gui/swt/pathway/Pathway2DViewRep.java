/**
 * 
 */
package cerberus.view.gui.swt.pathway;

import org.eclipse.swt.layout.GridLayout;

import cerberus.data.collection.ISet;
import cerberus.manager.IGeneralManager;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.ViewType;
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
implements IView, IMediatorSender, IMediatorReceiver {
	
	protected int iHTMLBrowserId;
	
	protected APathwayGraphViewRep refPathwayGraphViewRep;
	
	public Pathway2DViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId,
				sLabel,
				ViewType.SWT_PATHWAY2D);	
		
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
		
		// Convert ArrayList<Integer> to int[]
		int[] iArSetDataTmp = new int[alSetData.size()];
		for(int index = 0; index < alSetData.size(); index++)
			iArSetDataTmp[index] = alSetData.get(index).getId();
		
		int[] iArSetSelectionTmp = new int[alSetSelection.size()];
		for(int index = 0; index < alSetSelection.size(); index++)
			iArSetSelectionTmp[index] = alSetSelection.get(index).getId();
		
		// Forwarding selection data to the JGraph ViewRep
		refPathwayGraphViewRep.addSetId(iArSetDataTmp);
		refPathwayGraphViewRep.addSetId(iArSetSelectionTmp);
		
		refPathwayGraphViewRep.retrieveGUIContainer();
		refPathwayGraphViewRep.initView();
		refPathwayGraphViewRep.drawView();
	}

	public void setAttributes(int iWidth, int iHeight,
			int iHTMLBrowserId) {
		
		super.setAttributes(iWidth, iHeight);

		this.iHTMLBrowserId = iHTMLBrowserId;
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
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.view.gui.AViewRep#updateReceiver(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, 
			ISet updatedSet) {
		
		// Just pass on to embedded JGraph 2D Pathway ViewRep
		refPathwayGraphViewRep.updateReceiver(eventTrigger, updatedSet);
	}
}
