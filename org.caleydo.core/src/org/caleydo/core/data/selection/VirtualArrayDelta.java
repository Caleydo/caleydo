package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.caleydo.core.data.mapping.EIDType;

/**
 * 
 * @author Alexander Lex
 *
 */
public class VirtualArrayDelta
	implements IVirtualArrayDelta, Iterable<VADeltaItem>
{
	private ArrayList<VADeltaItem> alDeltaItems;

	private EIDType idType;
	
	public VirtualArrayDelta(EIDType idType)
	{
		this.idType = idType;
		alDeltaItems = new ArrayList<VADeltaItem>();
	}
	
	public EIDType getIDType()
	{
		return idType;
	}
	
	public Collection<VADeltaItem> getSelectionData()
	{
		return alDeltaItems;
	}
	
	public void add(VADeltaItem item)
	{}

	@Override
	public Iterator<VADeltaItem> iterator()
	{
		return alDeltaItems.iterator();
	}
}
