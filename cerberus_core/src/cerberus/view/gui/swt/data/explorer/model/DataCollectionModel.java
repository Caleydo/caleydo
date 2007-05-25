package cerberus.view.gui.swt.data.explorer.model;

import java.util.ArrayList;
import java.util.List;

public class DataCollectionModel extends AModel {
	
	protected List<DataCollectionModel> setList;

	protected List<StorageModel> storageList;

	protected List<SelectionModel> selectionList;
	
	protected List<PathwayModel> pathwayList;

	private static IModelVisitor adder = new Adder();

	private static IModelVisitor remover = new Remover();

	public DataCollectionModel() {

		setList = new ArrayList<DataCollectionModel>();
		storageList = new ArrayList<StorageModel>();
		selectionList = new ArrayList<SelectionModel>();
		pathwayList = new ArrayList<PathwayModel>();
	}

	public DataCollectionModel(int iId, String sLabel) {

		super(iId, sLabel);

		setList = new ArrayList<DataCollectionModel>();
		storageList = new ArrayList<StorageModel>();
		selectionList = new ArrayList<SelectionModel>();
		pathwayList = new ArrayList<PathwayModel>();
	}

	private static class Adder implements IModelVisitor {
		
		/*
		 *  (non-Javadoc)
		 * @see cerberus.view.gui.swt.data.explorer.model.IModelVisitor#visitSetModel(cerberus.view.gui.swt.data.explorer.model.DataCollectionModel, java.lang.Object)
		 */
		public void visitSetModel(DataCollectionModel set, Object argument) {

			((DataCollectionModel) argument).addSet(set);
		}

		/*
		 *  (non-Javadoc)
		 * @see cerberus.view.gui.swt.data.explorer.model.IModelVisitor#visitStorageModel(cerberus.view.gui.swt.data.explorer.model.StorageModel, java.lang.Object)
		 */
		public void visitStorageModel(StorageModel storage, Object argument) {

			((DataCollectionModel) argument).addStorage(storage);
		}
		
		/*
		 *  (non-Javadoc)
		 * @see cerberus.view.gui.swt.data.explorer.model.IModelVisitor#visitSelectionModel(cerberus.view.gui.swt.data.explorer.model.SelectionModel, java.lang.Object)
		 */
		public void visitSelectionModel(SelectionModel selection,
				Object argument) {

			((DataCollectionModel) argument).addSelection(selection);
		}
		
		/*
		 *  (non-Javadoc)
		 * @see cerberus.view.gui.swt.data.explorer.model.IModelVisitor#visitPathwayModel(cerberus.view.gui.swt.data.explorer.model.PathwayModel, java.lang.Object)
		 */
		public void visitPathwayModel(PathwayModel pathway,
				Object argument) {

			((DataCollectionModel) argument).addPathway(pathway);
		}
	}

	private static class Remover implements IModelVisitor {
		
		/*
		 *  (non-Javadoc)
		 * @see cerberus.view.gui.swt.data.explorer.model.IModelVisitor#visitSetModel(cerberus.view.gui.swt.data.explorer.model.DataCollectionModel, java.lang.Object)
		 */
		public void visitSetModel(DataCollectionModel setModel, Object argument) {

			((DataCollectionModel) argument).removeSetModel(setModel);
			setModel.addListener(NullDeltaListener.getSoleInstance());
		}

		/*
		 *  (non-Javadoc)
		 * @see cerberus.view.gui.swt.data.explorer.model.IModelVisitor#visitStorageModel(cerberus.view.gui.swt.data.explorer.model.StorageModel, java.lang.Object)
		 */
		public void visitStorageModel(StorageModel storage, Object argument) {

			((DataCollectionModel) argument).removeStorageModel(storage);
		}

		/*
		 *  (non-Javadoc)
		 * @see cerberus.view.gui.swt.data.explorer.model.IModelVisitor#visitSelectionModel(cerberus.view.gui.swt.data.explorer.model.SelectionModel, java.lang.Object)
		 */
		public void visitSelectionModel(SelectionModel selection,
				Object argument) {

			((DataCollectionModel) argument).removeSelectionModel(selection);
		}

		/*
		 *  (non-Javadoc)
		 * @see cerberus.view.gui.swt.data.explorer.model.IModelVisitor#visitSelectionModel(cerberus.view.gui.swt.data.explorer.model.SelectionModel, java.lang.Object)
		 */
		public void visitPathwayModel(PathwayModel pathway,
				Object argument) {

			((DataCollectionModel) argument).removePathwayModel(pathway);
		}
	}

	protected void addSet(DataCollectionModel set) {

		setList.add(set);
		set.parent = this;
		fireAdd(set);
	}

	protected void addStorage(StorageModel storage) {

		storageList.add(storage);
		storage.parent = this;
		fireAdd(storage);
	}

	protected void addSelection(SelectionModel selection) {

		selectionList.add(selection);
		selection.parent = this;
		fireAdd(selection);
	}

	protected void addPathway(PathwayModel pathway) {

		pathwayList.add(pathway);
		pathway.parent = this;
		fireAdd(pathway);
	}	
	
	public List getSets() {

		return setList;
	}

	public List getStorages() {

		return storageList;
	}

	public List getSelections() {

		return selectionList;
	}
	
	public List getPathways() {

		return pathwayList;
	}

	public void remove(AModel toRemove) {

		toRemove.accept(remover, this);
	}

	protected void removeStorageModel(StorageModel storageModel) {

		storageList.remove(storageModel);
		storageModel.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(storageModel);
	}

	protected void removeSelectionModel(SelectionModel selectionModel) {

		selectionList.remove(selectionModel);
		selectionModel.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(selectionModel);
	}

	protected void removeSetModel(DataCollectionModel setModel) {

		setList.remove(setModel);
		setModel.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(setModel);
	}
	
	protected void removePathwayModel(PathwayModel pathwayModel) {

		pathwayList.remove(pathwayModel);
		pathwayModel.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(pathwayModel);
	}

	
	public void add(AModel toAdd) {

		toAdd.accept(adder, this);
	}

	//	/** Answer the total number of items the
	//	 * receiver contains. */
	//	public int size() {
	//		return getSets().size() + getStorages().size() + getGames().size();
	//	}

	/*
	 * @see AModel#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {

		visitor.visitSetModel(this, passAlongArgument);
	}
}
