package cerberus.view.gui.swt.data.explorer.model;

import cerberus.manager.IGeneralManager;

public class StorageModel extends AModel
{	
	public StorageModel(int iId, String sLabel)
	{
		super(iId, sLabel);
	}

	@Override
	public void accept(IModelVisitor visitor, Object passAlongArgument)
	{
		visitor.visitStorageModel(this, passAlongArgument);		
	}
}
