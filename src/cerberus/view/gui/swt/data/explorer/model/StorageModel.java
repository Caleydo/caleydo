package cerberus.view.gui.swt.data.explorer.model;

import cerberus.manager.GeneralManager;
import cerberus.view.gui.swt.data.storage.StorageTableViewRep;

public class StorageModel extends Model
{
	protected StorageTableViewRep refStorageTableViewRep;
	
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
