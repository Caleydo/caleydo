/**
 * 
 */
package org.caleydo.core.view.swt.pathway;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.swt.pathway.jgraph.PathwayGraphViewRep;
import org.caleydo.core.view.swt.toolbar.Pathway2DToolbar;

/**
 * Pathway view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class Pathway2DViewRep 
extends AView 
implements IView, IMediatorSender, IMediatorReceiver {
	
	protected int iHTMLBrowserId;
	
	protected APathwayGraphViewRep pathwayGraphViewRep;
	
	public Pathway2DViewRep(
			IGeneralManager generalManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(generalManager, 
				iViewId, 
				iParentContainerId,
				sLabel,
				ViewType.SWT_PATHWAY2D);	
		
		// Pass the ID of the parent composite to the graph view rep instead of 
		// the ID of the PathwayViewRep.
		// That means that the PathwayViewRep and PathwayGraphViewRep are both
		// put in the same parent composite.
		pathwayGraphViewRep = 
			new PathwayGraphViewRep(generalManager, iViewId);
	}

	/**
	 * 
	 * @see org.caleydo.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer) {
		
		swtContainer.setLayout(new GridLayout(1, false));
		
		new Pathway2DToolbar(swtContainer, 
				pathwayGraphViewRep,
				generalManager);
		
		// Graph initialization
		pathwayGraphViewRep.setExternalGUIContainer(swtContainer);
		
		// Convert ArrayList<Integer> to int[]
		int[] iArSetDataTmp = new int[alSetData.size()];
		for(int index = 0; index < alSetData.size(); index++)
			iArSetDataTmp[index] = alSetData.get(index).getId();
		
		int[] iArSetSelectionTmp = new int[alSetSelection.size()];
		for(int index = 0; index < alSetSelection.size(); index++)
			iArSetSelectionTmp[index] = alSetSelection.get(index).getId();
		
		// Forwarding selection data to the JGraph ViewRep
		pathwayGraphViewRep.addSetId(iArSetDataTmp);
		pathwayGraphViewRep.addSetId(iArSetSelectionTmp);
		
		pathwayGraphViewRep.initView();
		
		// Inside this method drawView is called
		// (which is not optimal)
		pathwayGraphViewRep.showBackgroundOverlay(true);
	}

	public void setAttributes(int iHTMLBrowserId) {

		this.iHTMLBrowserId = iHTMLBrowserId;
	}

	public void drawView() {

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.AViewRep#updateReceiver(java.lang.Object, org.caleydo.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, 
			ISet updatedSet) {
		
		// Just pass on to embedded JGraph 2D Pathway ViewRep
		pathwayGraphViewRep.updateReceiver(eventTrigger, updatedSet);
	}
}
