package daemon;

import java.util.HashMap;

public class ApplicationManager {

	private HashMap<String, Application> applications;

	
	
	public ApplicationManager() {
		applications = new HashMap<String, Application>();
	}
	
	public HashMap<String, Application> getApplications() {
		return applications;
	}

	public void setApplications(HashMap<String, Application> applications) {
		this.applications = applications;
	}
	
}
