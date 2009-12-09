package daemon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlRootElement
public class Application {
	
	/** application name */
	private String name;
	
	/** application id assigned by {@link ApplicationManager} */
	private int id;
	
	/** creation timestamp */
	private Date date;
	
	/** bounding boxes of contained windows */
	private List<BoundingBox> windows;

	/** id to send */
	private String sendId;
	
	public Application() {
		name = null;
		windows = new ArrayList<BoundingBox>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name="applicationId")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@XmlElementWrapper
	public List<BoundingBox> getWindows() {
		return windows;
	}

	public void setWindows(List<BoundingBox> windows) {
		this.windows = windows;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String fetchSendId() {
		String id = sendId;
		sendId = null;
		return id;
	}

	public void setSendId(String sendId) {
		this.sendId = sendId;
	}

	@Override
	public String toString() {
		return "Application [name=" + name + ", date=" + date + ", windows=" + windows + "]";
	}

}
