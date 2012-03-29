package org.caleydo.core.serialize;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PlugInList {
	@XmlElement
	ArrayList<String> plugIns = new ArrayList<String>();
}
