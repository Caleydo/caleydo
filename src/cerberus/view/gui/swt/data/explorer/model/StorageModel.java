package cerberus.view.gui.swt.data.explorer.model;

import cerberus.manager.GeneralManager;

public class StorageModel extends Model
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
