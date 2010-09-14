package org.caleydo.core.data.virtualarray.delta;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.delta.IDelta;
import org.caleydo.core.util.collection.UniqueList;

/**
 * Implementation of {@link IVirtualArrayDelta}
 * 
 * @author Alexander Lex
 */
@XmlType
public abstract class VirtualArrayDelta<ConcreteType extends VirtualArrayDelta<ConcreteType>>
	implements IDelta<VADeltaItem> {

	@XmlElement
	private UniqueList<VADeltaItem> deltaItems;

	@XmlElement
	private IDType idType;

	@XmlElement
	private IDType secondaryIDType;

	@XmlElement
	private String vaType;

	public VirtualArrayDelta() {
		deltaItems = new UniqueList<VADeltaItem>();
	}

	public VirtualArrayDelta(String vaType, IDType idType) {
		this.vaType = vaType;
		this.idType = idType;
		deltaItems = new UniqueList<VADeltaItem>();
	}

	public VirtualArrayDelta(String vaType, IDType idType, IDType secondaryIDType) {
		this(vaType, idType);
		this.secondaryIDType = secondaryIDType;
	}

	public abstract ConcreteType getInstance();

	/**
	 * Returns the type of the virtual array as specified in {@link VAType}
	 * 
	 * @return
	 */
	public String getVAType() {
		return vaType;
	}

	/**
	 * Set the type of the VA
	 * 
	 * @param vaType
	 */
	public void setVAType(String vaType) {
		this.vaType = vaType;
	}

	@Override
	public void setIDType(IDType idType) {
		this.idType = idType;
	}

	@Override
	public IDType getIDType() {
		return idType;
	}

	@Override
	public IDType getSecondaryIDType() {
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
