package org.caleydo.core.data.selection;

import org.caleydo.core.data.mapping.EIDType;

public interface IVirtualArrayDelta
extends Iterable<VADeltaItem>
{
	public EIDType getIDType();	
//	public Collection<VADeltaItem> getSelectionData();	
	public void add(VADeltaItem item);

}
