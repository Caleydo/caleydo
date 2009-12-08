package vis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class HelloWorldController extends AbstractController {

	private String message;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		TestBean tb = getApplicationContext().getBean("testBean", TestBean.class);
		System.out.println(tb);
		
		return new ModelAndView("welcomePage", "welcomeMessage", message);
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
