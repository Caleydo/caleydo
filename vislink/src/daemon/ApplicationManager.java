package daemon;

import java.util.HashMap;

public class ApplicationManager {

	private HashMap<String, Application> applications;

	private int idCounter = 0;
	
	public ApplicationManager() {
		applications = new HashMap<String, Application>();
	}
	
	public HashMap<String, Application> getApplications() {
		return applications;
	}

	public void setApplications(HashMap<String, Application> applications) {
		this.applications = applications;
	}
	
	public void registerApplication(Application app) {
		app.setId(idCounter++);
		applications.put(app.getName(), app);
	}
	
}
