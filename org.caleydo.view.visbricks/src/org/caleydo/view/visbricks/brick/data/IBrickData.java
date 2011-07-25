package org.caleydo.view.visbricks.brick.data;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.view.visbricks.brick.GLBrick;

public interface IBrickData {

	public IDataDomain getDataDomain();
	
	public RecordVirtualArray getRecordVA();
	
	public Group getGroup();
	
	public void setBrickData(GLBrick brick);
	
	public String getLabel();
	
}
