package cerberus.view.gui.swt.data.explorer.model;

public interface IModelVisitor 
{
	public void visitSetModel(SetModel set, Object passAlongArgument);
	public void visitStorageModel(StorageModel storage, Object passAlongArgument);
	public void visitSelectionModel(SelectionModel selection, Object passAlongArgument);
}
