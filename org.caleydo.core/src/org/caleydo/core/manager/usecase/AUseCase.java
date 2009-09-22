package org.caleydo.core.manager.usecase;

import java.util.ArrayList;
import java.util.EnumMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.data.selection.delta.DeltaConverter;
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
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.specialized.clinical.ClinicalUseCase;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.view.opengl.canvas.listener.IVirtualArrayUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.VirtualArrayUpdateListener;
import org.caleydo.core.view.opengl.canvas.storagebased.EDataFilterLevel;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;
import org.caleydo.core.view.opengl.canvas.storagebased.listener.StartClusteringListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Abstract use case class that implements data and view management.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
@XmlSeeAlso( { GeneticUseCase.class, ClinicalUseCase.class, UnspecifiedUseCase.class })
public abstract class AUseCase
	implements IVirtualArrayUpdateHandler, IUseCase, IListenerOwner {

	private ISet oldSet;

	protected String contentLabelSingular = "<not specified>";
	protected String contentLabelPlural = "<not specified>";

	protected EDataFilterLevel dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;

	/**
	 * This mode determines whether the user can load and work with gene expression data or otherwise if an
	 * not further specified data set is loaded. In the case of the unspecified data set some specialized gene
	 * expression features are not available.
	 */
	protected EDataDomain useCaseMode = EDataDomain.GENERAL_DATA;

	/** map selection type to unique id for virtual array */
	protected EnumMap<EVAType, Integer> mapVAIDs;

	/** The set which is currently loaded and used inside the views for this use case. */
	protected ISet set;

	/** parameters for loading the the data-{@link set} */
	protected LoadDataParameters loadDataParameters;

	/** bootstrap filename this application was started with */
	protected String bootsTrapFileName;

	/** central {@link IEventPublisher} to receive and send events */
	private IEventPublisher eventPublisher;

	private StartClusteringListener startClusteringListener;
	private ReplaceVirtualArrayInUseCaseListener replaceVirtualArrayInUseCaseListener;
	private VirtualArrayUpdateListener virtualArrayUpdateListener;

	/** Every use case needs to state all views that can visualize its data */
	protected ArrayList<EManagedObjectType> possibleViews;

	public AUseCase() {
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	@Override
	public EDataDomain getDataDomain() {
		return useCaseMode;
	}

	// public void setUseCaseMode(EDataDomain useCaseMode) {
	// this.useCaseMode = useCaseMode;
	// }

	@Override
	public ArrayList<EManagedObjectType> getPossibleViews() {
		return possibleViews;
	}

	@XmlTransient
	@Override
	public ISet getSet() {
		return set;
	}

	@Override
	public void setSet(ISet set) {

		if ((set.getSetType() == ESetType.GENE_EXPRESSION_DATA && useCaseMode == EDataDomain.GENETIC_DATA)
			|| (set.getSetType() == ESetType.CLINICAL_DATA && useCaseMode == EDataDomain.CLINICAL_DATA)
			|| (set.getSetType() == ESetType.UNSPECIFIED && useCaseMode == EDataDomain.GENERAL_DATA)) {

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
		newSetEvent.setSet((Set) set);
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
	public String getContentLabel(boolean bCapitalized, boolean bPlural) {

		String sContentLabel = "";

		if (bPlural)
			sContentLabel = contentLabelPlural;
		else
			sContentLabel = contentLabelSingular;

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

		// create VA with empty list
		int iVAID = set.createVA(EVAType.CONTENT_CONTEXT, new ArrayList<Integer>());
		mapVAIDs.put(EVAType.CONTENT_CONTEXT, iVAID);

		iVAID = set.createVA(EVAType.CONTENT_EMBEDDED_HM, new ArrayList<Integer>());
		mapVAIDs.put(EVAType.CONTENT_EMBEDDED_HM, iVAID);

		ArrayList<Integer> alTempList = new ArrayList<Integer>();

		alTempList = new ArrayList<Integer>();

		for (int iCount = 0; iCount < set.size(); iCount++) {
			alTempList.add(iCount);
		}

		iVAID = set.createVA(EVAType.STORAGE, alTempList);
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
		int iVAID = set.createVA(EVAType.CONTENT, alTempList);
		mapVAIDs.put(EVAType.CONTENT, iVAID);
	}

	public IVirtualArray getVA(EVAType vaType) {
		IVirtualArray va = set.getVA(mapVAIDs.get(vaType));
		IVirtualArray vaCopy = va.clone();
		return vaCopy;
	}

	@Override
	public void startClustering(ClusterState clusterState) {

		clusterState.setContentVaId(mapVAIDs.get(clusterState.getContentVAType()));
		clusterState.setStorageVaId(mapVAIDs.get(EVAType.STORAGE));

		ArrayList<IVirtualArray> iAlNewVAs = set.cluster(clusterState);

		if (iAlNewVAs != null) {
			set.replaceVA(mapVAIDs.get(clusterState.getContentVAType()), iAlNewVAs.get(0));
			set.replaceVA(mapVAIDs.get(EVAType.STORAGE), iAlNewVAs.get(1));
		}

		// This should be done to avoid problems with group info in HHM
		set.setGeneClusterInfoFlag(false);
		set.setExperimentClusterInfoFlag(false);

		eventPublisher.triggerEvent(new ReplaceVirtualArrayEvent(clusterState.getContentVAType()));
		eventPublisher.triggerEvent(new ReplaceVirtualArrayEvent(EVAType.STORAGE));

	}

	@Override
	/*
	 * * This is the method which is used to synchronize the views with the Virtual Array, which is initiated
	 * from this class. Therefore it should not be called any time!
	 */
	public void replaceVirtualArray(EVAType vaType) {
		throw new IllegalStateException("UseCases shouldn't react to this");

	}

	@Override
	public void replaceVirtualArray(EVAType vaType, IVirtualArray virtualArray) {

		set.replaceVA(mapVAIDs.get(vaType), virtualArray.clone());

		Tree<ClusterNode> tree = null;
		if (vaType == EVAType.CONTENT)
			tree = set.getClusteredTreeGenes();
		else if (vaType == EVAType.STORAGE)
			tree = set.getClusteredTreeExps();

		if (tree != null) {
			GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
				public void run() {
					Shell shell = new Shell();
					MessageBox messageBox = new MessageBox(shell, SWT.CANCEL);
					messageBox.setText("Warning");
					messageBox
						.setMessage("Modifications break tree structure, therefore dendrogram will be closed!");
					messageBox.open();
				}
			});
			if (vaType == EVAType.CONTENT)
				set.setClusteredTreeGenes(null);
			else if (vaType == EVAType.STORAGE)
				set.setClusteredTreeExps(null);
		}

		virtualArray.setGroupList(null);

		eventPublisher.triggerEvent(new ReplaceVirtualArrayEvent(vaType));
	}

	public void setVirtualArray(EVAType vaType, IVirtualArray virtualArray) {
		set.replaceVA(mapVAIDs.get(vaType), virtualArray);
		mapVAIDs.put(vaType, virtualArray.getID());
	}

	@Override
	public void handleVirtualArrayUpdate(IVirtualArrayDelta vaDelta, String info) {

		Integer vaID = mapVAIDs.get(vaDelta.getVAType());

		if (vaDelta.getIDType() == EIDType.REFSEQ_MRNA_INT)
			vaDelta = DeltaConverter.convertDelta(EIDType.EXPRESSION_INDEX, vaDelta);
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

	@Override
	public void resetContextVA() {
		int iUniqueID = mapVAIDs.get(EVAType.CONTENT_CONTEXT);
		set.replaceVA(iUniqueID, new VirtualArray(EVAType.CONTENT_CONTEXT, set.depth(),
			new ArrayList<Integer>()));

	}

	public String getContentLabelSingular() {
		return contentLabelSingular;
	}

	public void setContentLabelSingular(String contentLabelSingular) {
		this.contentLabelSingular = contentLabelSingular;
	}

	public String getContentLabelPlural() {
		return contentLabelPlural;
	}

	public void setContentLabelPlural(String contentLabelPlural) {
		this.contentLabelPlural = contentLabelPlural;
	}

	public EDataFilterLevel getDataFilterLevel() {
		return dataFilterLevel;
	}

	public void setDataFilterLevel(EDataFilterLevel dataFilterLevel) {
		this.dataFilterLevel = dataFilterLevel;
	}

	@Override
	public LoadDataParameters getLoadDataParameters() {
		return loadDataParameters;
	}

	@Override
	public void setLoadDataParameters(LoadDataParameters loadDataParameters) {
		this.loadDataParameters = loadDataParameters;
	}

	public String getBootstrapFileName() {
		return bootsTrapFileName;
	}

	public void setBootsTrapFileName(String bootsTrapFileName) {
		this.bootsTrapFileName = bootsTrapFileName;
	}

}
