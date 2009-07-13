package org.caleydo.core.manager.usecase;

import java.util.ArrayList;
import java.util.EnumMap;

import javax.naming.OperationNotSupportedException;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayEvent;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayInUseCaseEvent;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.event.view.NewSetEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.listener.IVirtualArrayUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.VirtualArrayUpdateListener;
import org.caleydo.core.view.opengl.canvas.storagebased.EDataFilterLevel;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;
import org.caleydo.core.view.opengl.canvas.storagebased.listener.StartClusteringListener;

/**
 * Abstract use case class that implements data and view management.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AUseCase
	implements IVirtualArrayUpdateHandler, IUseCase, IListenerOwner {

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
	protected EnumMap<EVAType, Integer> mapVAIDs;

	private IEventPublisher eventPublisher;

	private StartClusteringListener startClusteringListener;
	private ReplaceVirtualArrayInUseCaseListener replaceVirtualArrayInUseCaseListener;
	private VirtualArrayUpdateListener virtualArrayUpdateListener;

	public AUseCase() {
		alView = new ArrayList<IView>();
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
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

		mapVAIDs = new EnumMap<EVAType, Integer>(EVAType.class);

		if (!mapVAIDs.isEmpty()) {

			for (EVAType eSelectionType : EVAType.values()) {
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
		int iVAID = set.createContentVA(EVAType.CONTENT_CONTEXT, alTempList);
		mapVAIDs.put(EVAType.CONTENT_CONTEXT, iVAID);

		alTempList = new ArrayList<Integer>();

		for (int iCount = 0; iCount < set.size(); iCount++) {
			alTempList.add(iCount);
		}

		iVAID = set.createStorageVA(EVAType.STORAGE, alTempList);
		mapVAIDs.put(EVAType.STORAGE, iVAID);

		initFullVA();
	}

	protected void initFullVA() {
		// initialize virtual array that contains all (filtered) information
		ArrayList<Integer> alTempList = new ArrayList<Integer>(set.depth());

		for (int iCount = 0; iCount < set.depth(); iCount++) {

			alTempList.add(iCount);
		}

		// TODO: remove possible old virtual array
		int iVAID = set.createContentVA(EVAType.CONTENT, alTempList);
		mapVAIDs.put(EVAType.CONTENT, iVAID);
	}

	public IVirtualArray getVA(EVAType vaType) {
		IVirtualArray va = set.getVA(mapVAIDs.get(vaType));
		IVirtualArray vaCopy = va.clone();
		return vaCopy;
	}

	@Override
	public void startClustering(ClusterState clusterState) {

		clusterState.setContentVaId(mapVAIDs.get(EVAType.CONTENT));
		clusterState.setStorageVaId(mapVAIDs.get(EVAType.STORAGE));

		ArrayList<IVirtualArray> iAlNewVAs = set.cluster(clusterState);

		if (iAlNewVAs != null) {
			set.replaceVA(mapVAIDs.get(EVAType.CONTENT), iAlNewVAs.get(0));
			set.replaceVA(mapVAIDs.get(EVAType.STORAGE), iAlNewVAs.get(1));
		}
		// if (iAlNewVAIDs != null) {
		// mapVAIDs.put(EVAType.CONTENT, iAlNewVAIDs.get(0));
		// mapVAIDs.put(EVAType.STORAGE, iAlNewVAIDs.get(1));
		// }

		// This should be done to avoid problems with group info in HHM
		set.setGeneClusterInfoFlag(false);
		set.setExperimentClusterInfoFlag(false);

		eventPublisher.triggerEvent(new ReplaceVirtualArrayEvent(EVAType.CONTENT));

	}

	@Override
	/*
	 * * This is the method which is used to synchronize the views with the Virtual Array, which is initiated
	 * from this class. Therefore it should not be called any time!
	 */
	public void replaceVirtualArray(EVAType vaType) {
		throw new IllegalStateException("UseCases shouldn't react to this");

	}

	public void replaceVirtualArray(EVAType vaType, IVirtualArray virtualArray) {

		set.replaceVA(mapVAIDs.get(vaType), virtualArray.clone());

		eventPublisher.triggerEvent(new ReplaceVirtualArrayEvent(vaType));
	}

	@Override
	public void handleVirtualArrayUpdate(IVirtualArrayDelta vaDelta, String info) {

		Integer vaID = mapVAIDs.get(vaDelta.getVAType());
		IVirtualArray va = set.getVA(vaID);
		va.setDelta(vaDelta);
	}

	public void registerEventListeners() {

		// groupMergingActionListener = new GroupMergingActionListener();
		// groupMergingActionListener.setHandler(this);
		// eventPublisher.addListener(MergeGroupsEvent.class, groupMergingActionListener);
		//
		// groupInterChangingActionListener = new GroupInterChangingActionListener();
		// groupInterChangingActionListener.setHandler(this);
		// eventPublisher.addListener(InterchangeGroupsEvent.class, groupInterChangingActionListener);

		startClusteringListener = new StartClusteringListener();
		startClusteringListener.setHandler(this);
		eventPublisher.addListener(StartClusteringEvent.class, startClusteringListener);

		replaceVirtualArrayInUseCaseListener = new ReplaceVirtualArrayInUseCaseListener();
		replaceVirtualArrayInUseCaseListener.setHandler(this);
		eventPublisher.addListener(ReplaceVirtualArrayInUseCaseEvent.class,
			replaceVirtualArrayInUseCaseListener);

		virtualArrayUpdateListener = new VirtualArrayUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class, virtualArrayUpdateListener);

	}

	// TODO this is never called!
	public void unregisterEventListeners() {

		// if (groupMergingActionListener != null) {
		// eventPublisher.removeListener(groupMergingActionListener);
		// groupMergingActionListener = null;
		// }
		// if (groupInterChangingActionListener != null) {
		// eventPublisher.removeListener(groupInterChangingActionListener);
		// groupInterChangingActionListener = null;
		// }

		if (startClusteringListener != null) {
			eventPublisher.removeListener(startClusteringListener);
			startClusteringListener = null;
		}

		if (replaceVirtualArrayInUseCaseListener != null) {
			eventPublisher.removeListener(replaceVirtualArrayInUseCaseListener);
			replaceVirtualArrayInUseCaseListener = null;
		}

		if (virtualArrayUpdateListener != null) {
			eventPublisher.removeListener(virtualArrayUpdateListener);
			virtualArrayUpdateListener = null;
		}
	}

	@Override
	public synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {

		// FIXME: concurrency issues?
		listener.handleEvent(event);

	}

}
