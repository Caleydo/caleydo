package daemon;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BoundingBoxList {

	ArrayList<BoundingBox> list;

	public BoundingBoxList() {
		list = new ArrayList<BoundingBox>();
	}
	
	@XmlElement(name="boundingBox")
	public ArrayList<BoundingBox> getList() {
		return list;
	}

	public void setList(ArrayList<BoundingBox> list) {
		this.list = list;
	}

	public void add(BoundingBox bb) {
		list.add(bb);
	}
	
}
