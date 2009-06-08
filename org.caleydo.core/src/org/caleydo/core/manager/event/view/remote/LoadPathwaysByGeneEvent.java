package org.caleydo.core.manager.event.view.remote;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that pathways should be added to the bucket in which a certain gene is contained.
 * 
 * @author Marc Streit
 */
public class LoadPathwaysByGeneEvent
	extends AEvent {

	/** gene ID of the idType */
	private int geneID = -1;

	private EIDType idType;

	public int getGeneID() {
		return geneID;
	}

	public void setGeneID(int geneId) {
		geneID = geneId;
	}

	public EIDType getIdType() {
		return idType;
	}

	public void setIdType(EIDType idType) {
		this.idType = idType;
	}

	@Override
	public boolean checkIntegrity() {
		if (geneID == -1)
			throw new IllegalStateException("geneID was not set");
		if (idType == null)
			throw new NullPointerException("idType is null");
		return true;
	}

}
