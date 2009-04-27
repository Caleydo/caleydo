package org.caleydo.core.manager.event;

import java.util.ArrayList;

import org.caleydo.core.data.mapping.EIDType;

/**
 * Reference implementation of {@link IEventContainer} and {@link AEventContainer} for lists of IDs
 * 
 * @author Alexander Lex
 */
@Deprecated
public class IDListEventContainer<T>
	extends AEventContainer {
	ArrayList<T> alIDs;
	EIDType iDType = null;

	/**
	 * Constructor for ID Container without ID Type
	 * 
	 * @param eEventType
	 *            the type of event
	 */
	public IDListEventContainer(EEventType eEventType) {
		super(eEventType);
		alIDs = new ArrayList<T>();
	}

	/**
	 * Constructor for ID Container with ID Type
	 * 
	 * @param eEventType
	 *            the type of event
	 * @param iDType
	 *            the type of the ID
	 */
	public IDListEventContainer(EEventType eEventType, EIDType iDType) {
		this(eEventType);
		this.iDType = iDType;
	}

	/**
	 * Get the type of an ID. Returns null if no iDType was set, which is legal
	 * 
	 * @return the type of the ID or null
	 */
	public EIDType getIDType() {
		return iDType;
	}

	/**
	 * Set an array list of IDs. All previously added elements are lost. Uses the reference passed, does not
	 * copy list.
	 * 
	 * @param alIDs
	 *            the new list of IDs
	 */
	public void setIDs(ArrayList<T> alIDs) {
		this.alIDs = alIDs;
	}

	/**
	 * Adds a new ID to the existing IDs
	 * 
	 * @param ID
	 */
	public void addID(T iD) {
		alIDs.add(iD);
	}

	/**
	 * Get a list of all the stored IDs
	 * 
	 * @return the list of IDs
	 */
	public ArrayList<T> getIDs() {
		return alIDs;
	}

}
