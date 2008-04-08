/**
 * 
 */
package org.geneview.core.view.swt.pathway;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.view.AViewRep;
import org.geneview.core.view.IView;
import org.geneview.core.view.ViewType;
import org.geneview.core.view.swt.pathway.jgraph.PathwayGraphViewRep;
import org.geneview.core.view.swt.toolbar.Pathway2DToolbar;

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

	/**
	 * 
	 * @see org.geneview.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer) {
		
		refSWTContainer.setLayout(new GridLayout(1, false));
		
		new Pathway2DToolbar(refSWTContainer, 
				refPathwayGraphViewRep,
				generalManager);
		
		// Graph initialization
		refPathwayGraphViewRep.setExternalGUIContainer(refSWTContainer);
		
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
		
		refPathwayGraphViewRep.initView();
		
		// Inside this method drawView is called
		// (which is not optimal)
		refPathwayGraphViewRep.showBackgroundOverlay(true);
	}

	public void setAttributes(int iHTMLBrowserId) {

		this.iHTMLBrowserId = iHTMLBrowserId;
	}

	public void drawView() {

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.AViewRep#updateReceiver(java.lang.Object, org.geneview.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, 
			ISet updatedSet) {
		
		// Just pass on to embedded JGraph 2D Pathway ViewRep
		refPathwayGraphViewRep.updateReceiver(eventTrigger, updatedSet);
	}
}
