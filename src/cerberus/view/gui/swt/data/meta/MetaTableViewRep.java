package cerberus.view.gui.swt.data.meta;

import cerberus.manager.IGeneralManager;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.ViewType;
import cerberus.view.gui.swt.data.IDataTableView;

public class MetaTableViewRep extends AViewRep implements IDataTableView
{	
	public MetaTableViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_META_TABLE);
	}

	public void initView()
	{
		// TODO Auto-generated method stub
		
	}

	public void drawView()
	{
		// TODO Auto-generated method stub
		
	}

	public void retrieveGUIContainer()
	{
		// TODO Auto-generated method stub
		
	}

	public void initTable()
	{
		// TODO Auto-generated method stub
		
	}

	public void createTable()
	{
		// TODO Auto-generated method stub
		
	}

}
