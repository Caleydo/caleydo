package org.caleydo.view.visbricks.brick.data;

import org.caleydo.core.data.container.ISegmentData;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.visbricks.brick.GLBrick;

public interface IBrickData extends ISegmentData {

	public RecordVirtualArray getRecordVA();

	public DimensionVirtualArray getDimensionVA();

	public Group getGroup();

	public void setBrickData(GLBrick brick);

	public String getLabel();

}
