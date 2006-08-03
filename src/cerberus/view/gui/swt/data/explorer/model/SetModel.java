package cerberus.view.gui.swt.data.explorer.model;

import java.util.ArrayList;
import java.util.List;

public class SetModel extends Model
{
	protected List setList;
	protected List storageList;
	protected List selectionList;
	
	private static IModelVisitor adder = new Adder();
	//private static IModelVisitor remover = new Remover();
	
	public SetModel() 
	{
		setList = new ArrayList();
		storageList = new ArrayList();
		selectionList = new ArrayList();
	}
	
	public SetModel(int iId, String sLabel) 
	{
		super(iId, sLabel);
	}
	
	private static class Adder implements IModelVisitor 
	{
		/*
		 * @see ModelVisitorI#visitSetModel(SetModel, Object)
		 */
		public void visitSetModel(SetModel setModel, Object argument) 
		{
			//((SetModel) argument).addSet(setModel);
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

//	private static class Remover implements IModelVisitor {
//		public void visitBoardgame(BoardGame boardgame, Object argument) {
//			((MovingBox) argument).removeBoardGame(boardgame);
//		}
//
//		/*
//		 * @see ModelVisitorI#visitBook(MovingBox, Object)
//		 */
//		public void visitBook(Book book, Object argument) {
//			((MovingBox) argument).removeBook(book);
//		}
//
//		/*
//		 * @see ModelVisitorI#visitMovingBox(MovingBox, Object)
//		 */
//		public void visitMovingBox(MovingBox box, Object argument) {
//			((MovingBox) argument).removeBox(box);
//			box.addListener(NullDeltaListener.getSoleInstance());
//		}
//
//	}
	
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
//	
//	public void remove(Model toRemove) {
//		toRemove.accept(remover, this);
//	}
//	
//	protected void removeBoardGame(BoardGame boardGame) {
//		games.remove(boardGame);
//		boardGame.addListener(NullDeltaListener.getSoleInstance());
//		fireRemove(boardGame);
//	}
//	
//	protected void removeBook(Book book) {
//		books.remove(book);
//		book.addListener(NullDeltaListener.getSoleInstance());
//		fireRemove(book);
//	}
//	
//	protected void removeBox(MovingBox box) {
//		boxes.remove(box);
//		box.addListener(NullDeltaListener.getSoleInstance());
//		fireRemove(box);	
//	}
//
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
