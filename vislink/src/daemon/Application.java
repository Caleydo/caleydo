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
	
	/** whether the window is just a temporary container. */
	private boolean temporary; 
	
	/** counts the number of times the application has not responded. */
	private int nonResponsiveCounter; 
	
	/** the maximum number of consequtive non responsive increments before the application gets unregistered. */
	public final static int MAX_NON_RESPONSIVE = 3; 

//	/** id to send */
//	private String sendId;
	
	public Application() {
		name = null;
		windows = new ArrayList<BoundingBox>();
		temporary = false; 
		nonResponsiveCounter = 0; 
	}

	public boolean isTemporary() {
		return temporary;
	}

	public void setTemporary(boolean temporary) {
		this.temporary = temporary;
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

//	public String fetchSendId() {
//		String id = sendId;
//		sendId = null;
//		return id;
//	}
//
//	public void setSendId(String sendId) {
//		this.sendId = sendId;
//	}
	
	public void reportNonResponsive(){
		this.nonResponsiveCounter++; 
		System.out.println("\nNon responsive counter: "+this.nonResponsiveCounter+"("+this.toString()+")\n"); 
	}
	
	public void reportResponsive(){
		this.nonResponsiveCounter = 0; 
		System.out.println("Application "+this.toString()+" responsive again"); 
	}
	
	public boolean isResponsive(){
		return (this.nonResponsiveCounter < MAX_NON_RESPONSIVE); 
	}

	@Override
	public String toString() {
		return "Application [name=" + name + ", date=" + date + ", windows=" + windows + "]";
	}

}
