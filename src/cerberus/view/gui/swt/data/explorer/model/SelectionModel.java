package cerberus.view.gui.swt.data.explorer.model;

import cerberus.manager.GeneralManager;

public class SelectionModel extends Model
{
	public SelectionModel(int iId, String sLabel)
	{
		super(iId, sLabel);
	}

	@Override
	public void accept(IModelVisitor visitor, Object passAlongArgument)
	{
		visitor.visitSelectionModel(this, passAlongArgument);	
	}
}
