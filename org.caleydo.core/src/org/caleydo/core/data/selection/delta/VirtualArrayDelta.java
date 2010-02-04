package org.caleydo.core.data.selection.delta;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.util.collection.UniqueList;

/**
 * Implementation of {@link IVirtualArrayDelta}
 * 
 * @author Alexander Lex
 */
@XmlType
public class VirtualArrayDelta
	implements IVirtualArrayDelta {

	@XmlElement
	private UniqueList<VADeltaItem> deltaItems;

	@XmlElement
	private EIDType idType;

	@XmlElement
	private EIDType secondaryIDType;

	@XmlElement
	private EVAType vaType;

	public VirtualArrayDelta() {
		deltaItems = new UniqueList<VADeltaItem>();
	}

	public VirtualArrayDelta(EVAType vaType, EIDType idType) {
		this.vaType = vaType;
		this.idType = idType;
		deltaItems = new UniqueList<VADeltaItem>();
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
	public void setVAType(EVAType vaType) {
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
		deltaItems.add(item);
	}

	@Override
	public Iterator<VADeltaItem> iterator() {
		return deltaItems.iterator();
	}

	@Override
	public Collection<VADeltaItem> getAllItems() {
		return deltaItems;
	}

	@Override
	public int size() {
		return deltaItems.size();
	}

	public void append(VirtualArrayDelta delta) {
		deltaItems.addAll(delta.deltaItems);
	}

	@Override
	public String toString() {
		String output = "";
		for (VADeltaItem deltaItem : deltaItems) {
			output = output + deltaItem;
		}

		return output;
	}
}
