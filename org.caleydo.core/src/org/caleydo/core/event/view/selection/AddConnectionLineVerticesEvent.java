package org.caleydo.core.event.view.selection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.vislink.SelectionPoint2DList;

/**
 * Sends new 2d connection line vertices from a caleydo-client to a server.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class AddConnectionLineVerticesEvent
	extends AEvent {

	private IDType idType;

	private Integer connectionID;

	private SelectionPoint2DList points;

	@Override
	public boolean checkIntegrity() {
		// TODO
		return true;
	}

	public IDType getIdType() {
		return idType;
	}

	public void setIdType(IDType idType) {
		this.idType = idType;
	}

	public SelectionPoint2DList getPoints() {
		return points;
	}

	public void setPoints(SelectionPoint2DList points) {
		this.points = points;
	}

	public Integer getConnectionID() {
		return connectionID;
	}

	public void setConnectionID(Integer connectionID) {
		this.connectionID = connectionID;
	}

}
