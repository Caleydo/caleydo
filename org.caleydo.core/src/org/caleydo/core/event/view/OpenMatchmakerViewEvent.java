package org.caleydo.core.event.view;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.collection.table.DataTable;

/**
 * Event to create a compare view. The sets to compare are handed over.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class OpenMatchmakerViewEvent
	extends OpenViewEvent {

	ArrayList<DataTable> setsToCompare;

	public ArrayList<DataTable> getTablesToCompare() {
		return setsToCompare;
	}

	public void setTablesToCompare(ArrayList<DataTable> setsToCompare) {
		this.setsToCompare = setsToCompare;
	}
}
