package org.caleydo.core.data.selection.delta;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.IVAType;
import org.caleydo.core.util.collection.UniqueList;

/**
 * Implementation of {@link IVirtualArrayDelta}
 * 
 * @author Alexander Lex
 */
@XmlType
public abstract class VirtualArrayDelta<ConcreteType extends VirtualArrayDelta<ConcreteType, VAType>, VAType extends IVAType>
	implements IDelta<VADeltaItem> {

	@XmlElement
	private UniqueList<VADeltaItem> deltaItems;

	@XmlElement
	private EIDType idType;

	@XmlElement
	private EIDType secondaryIDType;

	@XmlElement
	private VAType vaType;

	public VirtualArrayDelta() {
		deltaItems = new UniqueList<VADeltaItem>();
	}

	public VirtualArrayDelta(VAType vaType, EIDType idType) {
		this.vaType = vaType;
		this.idType = idType;
		deltaItems = new UniqueList<VADeltaItem>();
	}

	public VirtualArrayDelta(VAType vaType, EIDType idType, EIDType secondaryIDType) {
		this(vaType, idType);
		this.secondaryIDType = secondaryIDType;
	}

	public abstract ConcreteType getInstance();

	/**
	 * Returns the type of the virtual array as specified in {@link VAType}
	 * 
	 * @return
	 */
	public VAType getVAType() {
		return vaType;
	}

	/**
	 * Set the type of the VA
	 * 
	 * @param vaType
	 */
	public void setVAType(VAType vaType) {
		this.vaType = vaType;
	}

	@Override
	public void setIDType(EIDType idType) {
		this.idType = idType;
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

	public void append(ConcreteType delta) {
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
