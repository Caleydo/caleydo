package org.caleydo.core.data.selection.delta;

import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.util.collection.UniqueList;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Implementation of {@link IVirtualArrayDelta}
 * 
 * @author Alexander Lex
 */
public class VirtualArrayDelta
	implements IVirtualArrayDelta {
	private UniqueList<VADeltaItem> ulDeltaItems;

	private EIDType idType;
	private EIDType secondaryIDType;
	private EVAType vaType;

	public VirtualArrayDelta(EVAType vaType, EIDType idType) {
		this.vaType = vaType;
		this.idType = idType;
		ulDeltaItems = new UniqueList<VADeltaItem>();
	}

	public VirtualArrayDelta(EVAType vaType, EIDType idType, EIDType secondaryIDType) {
		this(vaType, idType);
		this.secondaryIDType = secondaryIDType;
	}

	@Override
	public EVAType getVAType() {
		return vaType;
	}

	@Override
	public void setVAType(EVAType vaType){
		this.vaType = vaType;
	}

	@Override
	public EIDType getIDType() {
		return idType;
	}

	@Override
	public EIDType getSecondaryIDType() {
		return secondaryIDType;
	}

	@Override
	public void add(VADeltaItem item) {
		ulDeltaItems.add(item);
	}

	@Override
	public Iterator<VADeltaItem> iterator() {
		return ulDeltaItems.iterator();
	}

	@Override
	public Collection<VADeltaItem> getAllItems() {
		return ulDeltaItems;
	}

	@Override
	public int size() {
		return ulDeltaItems.size();
	}

	public void append(VirtualArrayDelta delta) {
		ulDeltaItems.addAll(delta.ulDeltaItems);
	}
}
