package org.caleydo.core.manager.usecase;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.view.NewSetEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.IView;

/**
 * Abstract use case class that implements data and view management.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AUseCase
	implements IUseCase {

	protected ArrayList<IView> alView;

	private ISet oldSet;

	protected String sContentLabelSingular = "<not specified>";
	protected String sContentLabelPlural = "<not specified>";

	/**
	 * This mode determines whether the user can load and work with gene expression data or otherwise if an
	 * not further specified data set is loaded. In the case of the unspecified data set some specialized gene
	 * expression features are not available.
	 */
	protected EUseCaseMode eUseCaseMode = EUseCaseMode.UNSPECIFIED_DATA;

	public AUseCase() {
		alView = new ArrayList<IView>();
	}

	/**
	 * The set which is currently loaded and used inside the views for this use case.
	 */
	protected ISet set;

	@Override
	public EUseCaseMode getUseCaseMode() {
		return eUseCaseMode;
	}

	@Override
	public ISet getSet() {
		return set;
	}

	@Override
	public void setSet(ISet set) {

		if ((set.getSetType() == ESetType.GENE_EXPRESSION_DATA && eUseCaseMode == EUseCaseMode.GENETIC_DATA)
			|| (set.getSetType() == ESetType.CLINICAL_DATA && eUseCaseMode == EUseCaseMode.CLINICAL_DATA)
			|| (set.getSetType() == ESetType.UNSPECIFIED && eUseCaseMode == EUseCaseMode.UNSPECIFIED_DATA)) {

			oldSet = this.set;
			this.set = set;
			if (oldSet != null) {
				oldSet.destroy();
				oldSet = null;
			}
		}
		else {
			throw new IllegalStateException("The Set " + set + " specified is not suited for the use case "
				+ this);
		}
	}

	@Override
	public void updateSetInViews() {

		NewSetEvent newSetEvent = new NewSetEvent();
		newSetEvent.setSet(set);
		GeneralManager.get().getEventPublisher().triggerEvent(newSetEvent);
		// GLRemoteRendering glRemoteRenderingView = null;
		//
		// // Update set in the views
		// for (IView view : alView) {
		// view.setSet(set);
		//			
		//
		// if (view instanceof GLRemoteRendering) {
		// glRemoteRenderingView = (GLRemoteRendering) view;
		// }
		// }

		// TODO check
		// oldSet.destroy();
		// oldSet = null;
		// When new data is set, the bucket will be cleared because the internal heatmap and parcoords cannot
		// be updated in the context mode.
		// if (glRemoteRenderingView != null)
		// glRemoteRenderingView.clearAll();
	}

	@Override
	public void addView(IView view) {

		if (alView.contains(view))
			return;

		alView.add(view);
	}

	@Override
	public void removeView(IView view) {

		alView.remove(view);
	}

	@Override
	public String getContentLabel(boolean bCapitalized, boolean bPlural) {

		String sContentLabel = "";

		if (bPlural)
			sContentLabel = sContentLabelPlural;
		else
			sContentLabel = sContentLabelSingular;

		if (bCapitalized) {
		
			// Make first char capitalized
			sContentLabel =
				sContentLabel.substring(0, 1).toUpperCase()
					+ sContentLabel.substring(1, sContentLabel.length());
		}
		
		return sContentLabel;
	}
}
