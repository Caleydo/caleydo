package daemon;

import java.util.HashMap;

public class ApplicationManager {

	private HashMap<String, Application> applications;

	private HashMap<Integer, Application> applicationsById;
	
	private int idCounter = 0;

	public ApplicationManager() {
		applications = new HashMap<String, Application>();
		applicationsById = new HashMap<Integer, Application>();
	}
	
	public HashMap<String, Application> getApplications() {
		return applications;
	}

	public void setApplications(HashMap<String, Application> applications) {
		this.applications = applications;
	}
	
	public HashMap<Integer, Application> getApplicationsById() {
		return applicationsById;
	}

	public void setApplicationsById(HashMap<Integer, Application> applicationsById) {
		this.applicationsById = applicationsById;
	}

	public void registerApplication(Application app) {
		app.setId(idCounter++);
		applications.put(app.getName(), app);
		applicationsById.put(app.getId(), app);
	}
	
	public void clearApplications() {
		this.applications.clear(); 
		this.applicationsById.clear(); 
	}
	
}
