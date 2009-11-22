package vis;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import daemon.Application;
import daemon.ApplicationManager;

public class ApplicationListController extends AbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ApplicationManager applicationManager = getApplicationContext().getBean("applicationManager", ApplicationManager.class);
		
		Collection<Application> apps = applicationManager.getApplications().values();
		return new ModelAndView("applicationList", "applicationList", apps);
	}

}
