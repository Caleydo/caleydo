package org.caleydo.core.view.swt.data.meta;

import org.eclipse.swt.widgets.Composite;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.AViewRep;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.swt.data.IDataTableView;

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
