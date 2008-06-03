package org.caleydo.core.view.swt.data.meta;

import org.eclipse.swt.widgets.Composite;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.swt.data.IDataTableView;

public class MetaTableViewRep extends AView implements IDataTableView
{	
	public MetaTableViewRep(IGeneralManager generalManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(generalManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_META_TABLE);
	}

	protected void initViewSwtComposit(Composite swtContainer) {	
		// TODO Auto-generated method stub
		
	}

	public void drawView()
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
