package org.caleydo.core.manager.event.view;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;

/**
 * Event to create a compare view. The sets to compare are handed over.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class OpenMatchmakerViewEvent
	extends OpenViewEvent {

	ArrayList<ISet> setsToCompare;
	
	public ArrayList<ISet> getSetsToCompare() {
		return setsToCompare;
	}
	
	public void setSetsToCompare(ArrayList<ISet> setsToCompare) {
		this.setsToCompare = setsToCompare;
	}
}
