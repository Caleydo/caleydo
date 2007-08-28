package cerberus.view.swt.data.explorer.model;

public interface IModelVisitor {
	
	public void visitSetModel(DataCollectionModel set, 
			Object passAlongArgument);

	public void visitStorageModel(StorageModel storage, 
			Object passAlongArgument);

	public void visitSelectionModel(SelectionModel selection,
			Object passAlongArgument);
	
	public void visitPathwayModel(PathwayModel pathway,
			Object passAlongArgument);
	
}
