package cerberus.view.gui.swt.data.explorer.model;

import java.util.ArrayList;
import java.util.List;

import cerberus.manager.GeneralManager;

public class SetModel extends Model
{
	protected List<SetModel> setList;
	protected List<StorageModel> storageList;
	protected List<SelectionModel> selectionList;
	
	private static IModelVisitor adder = new Adder();
	private static IModelVisitor remover = new Remover();
	
	public SetModel() 
	{
		setList = new ArrayList<SetModel>();
		storageList = new ArrayList<StorageModel>();
		selectionList = new ArrayList<SelectionModel>();
	}
	
	public SetModel(int iId, String sLabel) 
	{
		super(iId, sLabel);
		
		setList = new ArrayList<SetModel>();
		storageList = new ArrayList<StorageModel>();
		selectionList = new ArrayList<SelectionModel>();
	}
	
	private static class Adder implements IModelVisitor 
	{
		/*
		 * @see ModelVisitorI#visitSetModel(SetModel, Object)
		 */
		public void visitSetModel(SetModel set, Object argument) 
		{
			((SetModel) argument).addSet(set);
		}

		/*
		 * @see ModelVisitorI#visitStorageModel(StorageModel, Object)
		 */
		public void visitStorageModel(StorageModel storage, Object argument) 
		{
			((SetModel) argument).addStorage(storage);
		}

		/*
		 * @see ModelVisitorI#visitMovingBox(MovingBox, Object)
		 */
		public void visitSelectionModel(SelectionModel selection, Object argument) 
		{
			((SetModel) argument).addSelection(selection);
		}
	}

	private static class Remover implements IModelVisitor 
	{
		public void visitSetModel(SetModel setModel, Object argument) 
		{
			((SetModel) argument).removeSetModel(setModel);
			setModel.addListener(NullDeltaListener.getSoleInstance());
		}

		/*
		 * @see ModelVisitorI#visitStorageModel(StorageModel, Object)
		 */
		public void visitStorageModel(StorageModel storage, Object argument) 
		{
			((SetModel) argument).removeStorageModel(storage);
		}

		/*
		 * @see ModelVisitorI#visitSelectionModel(ISelection, Object)
		 */
		public void visitSelectionModel(SelectionModel selection, Object argument) 
		{
			((SetModel) argument).removeSelectionModel(selection);
		}
	}
	
	
	protected void addSet(SetModel set) 
	{
		setList.add(set);
		set.parent = this;
		fireAdd(set);
	}		
	
	protected void addStorage(StorageModel storage) 
	{
		storageList.add(storage);
		storage.parent = this;
		fireAdd(storage);
	}		
	
	protected void addSelection(SelectionModel selection) 
	{
		selectionList.add(selection);
		selection.parent = this;
		fireAdd(selection);
	}
	
	public List getSets() 
	{
		return setList;
	}
	
	public List getStorages() 
	{
		return storageList;
	}
	
	public List getSelections() 
	{
		return selectionList;
	}
	
	public void remove(Model toRemove) {
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
	
	protected void removeSetModel(SetModel setModel) {
		setList.remove(setModel);
		setModel.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(setModel);	
	}

	public void add(Model toAdd) 
	{
		toAdd.accept(adder, this);
	}
	
//	/** Answer the total number of items the
//	 * receiver contains. */
//	public int size() {
//		return getSets().size() + getStorages().size() + getGames().size();
//	}
	
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) 
	{
		visitor.visitSetModel(this, passAlongArgument);
	}
}
