package org.caleydo.core.manager.usecase;

import java.util.ArrayList;
import java.util.EnumMap;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.view.NewSetEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.storagebased.EDataFilterLevel;
import org.caleydo.core.view.opengl.canvas.storagebased.EStorageBasedVAType;

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

	protected EDataFilterLevel dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;

	/**
	 * This mode determines whether the user can load and work with gene expression data or otherwise if an
	 * not further specified data set is loaded. In the case of the unspecified data set some specialized gene
	 * expression features are not available.
	 */
	protected EUseCaseMode eUseCaseMode = EUseCaseMode.UNSPECIFIED_DATA;

	/**
	 * map selection type to unique id for virtual array
	 */
	protected EnumMap<EStorageBasedVAType, Integer> mapVAIDs;

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

		initVAs();
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

	private void initVAs() {
		// super.initData();

		// bRenderOnlyContext = bIsRenderedRemote;

		mapVAIDs = new EnumMap<EStorageBasedVAType, Integer>(EStorageBasedVAType.class);

		if (!mapVAIDs.isEmpty()) {

			for (EStorageBasedVAType eSelectionType : EStorageBasedVAType.values()) {
				if (mapVAIDs.containsKey(eSelectionType)) {
					set.removeVirtualArray(mapVAIDs.get(eSelectionType));
				}
			}

			mapVAIDs.clear();
		}

		if (set == null) {
			mapVAIDs.clear();
			return;
		}

		ArrayList<Integer> alTempList = new ArrayList<Integer>();
		// create VA with empty list
		int iVAID = set.createStorageVA(alTempList);
		mapVAIDs.put(EStorageBasedVAType.EXTERNAL_SELECTION, iVAID);

		alTempList = new ArrayList<Integer>();

		for (int iCount = 0; iCount < set.size(); iCount++) {
			alTempList.add(iCount);
		}

		iVAID = set.createSetVA(alTempList);
		mapVAIDs.put(EStorageBasedVAType.STORAGE_SELECTION, iVAID);

		initFullVA();
	}

	protected void initFullVA() {
		// initialize virtual array that contains all (filtered) information
		ArrayList<Integer> alTempList = new ArrayList<Integer>(set.depth());

		for (int iCount = 0; iCount < set.depth(); iCount++) {

			alTempList.add(iCount);
		}

		// TODO: remove possible old virtual array
		int iVAID = set.createStorageVA(alTempList);
		mapVAIDs.put(EStorageBasedVAType.COMPLETE_SELECTION, iVAID);
	}

	public IVirtualArray getVA(EStorageBasedVAType vaType) {
		IVirtualArray va = set.getVA(mapVAIDs.get(vaType));
		IVirtualArray vaCopy = va.clone();
		return vaCopy;
	}

	public void cluster(ClusterState clusterState) {
		int iCurrentContentVAID = 0;
		int iCurrentStorageVAID = 0;
		int iNewContentVAID = 0;
		int iNewStorageVAID = 0;

		iCurrentContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);

		iCurrentStorageVAID = mapVAIDs.get(EStorageBasedVAType.STORAGE_SELECTION);

		if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING) {

			int iVAid = set.cluster(iCurrentContentVAID, iCurrentStorageVAID, clusterState, 0, 2);
			if (iVAid < 0)
				iNewContentVAID = iCurrentContentVAID;
			else
				iNewContentVAID = iVAid;

			iNewStorageVAID = iCurrentStorageVAID;
		}
		else if (clusterState.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING) {

			int iVAid = set.cluster(iCurrentContentVAID, iCurrentStorageVAID, clusterState, 0, 2);
			if (iVAid < 0)
				iNewStorageVAID = iCurrentStorageVAID;
			else
				iNewStorageVAID = iVAid;

			iNewContentVAID = iCurrentContentVAID;
		}
		else {

			boolean bSkipGeneClustering = false;

			clusterState.setClustererType(EClustererType.EXPERIMENTS_CLUSTERING);
			int iVAid = set.cluster(iCurrentContentVAID, iCurrentStorageVAID, clusterState, 0, 1);
			if (iVAid < 0) {
				iNewStorageVAID = iCurrentStorageVAID;
				iNewContentVAID = iCurrentContentVAID;
				bSkipGeneClustering = true;
			}
			else
				iNewStorageVAID = iVAid;

			// in case of user requests abort during experiment clustering do not cluster genes
			if (bSkipGeneClustering == false) {
				clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
				iVAid = set.cluster(iCurrentContentVAID, iNewStorageVAID, clusterState, 50, 1);
				if (iVAid < 0)
					iNewContentVAID = iCurrentContentVAID;
				else
					iNewContentVAID = iVAid;
			}
		}

		mapVAIDs.put(EStorageBasedVAType.COMPLETE_CLUSTERED_SELECTION, iNewContentVAID);

		// AlSelection.clear();
	}

}
