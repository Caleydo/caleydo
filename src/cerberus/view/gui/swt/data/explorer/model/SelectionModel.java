package cerberus.view.gui.swt.data.explorer.model;

import cerberus.manager.IGeneralManager;

public class SelectionModel extends AModel
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
