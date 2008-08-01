package org.caleydo.core.application.helper.cacher.biocarta;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.phleisch.app.itsucks.constants.ApplicationConstants;
import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.filter.download.impl.DownloadJobFilter;
import de.phleisch.app.itsucks.filter.download.impl.RegExpJobFilter;
import de.phleisch.app.itsucks.filter.download.impl.RegExpJobFilter.RegExpFilterAction;
import de.phleisch.app.itsucks.filter.download.impl.RegExpJobFilter.RegExpFilterRule;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

public class BioCartaPathwayCacher {

	private static Log mLog = LogFactory.getLog(BioCartaPathwayCacher.class);

	public static void main(String[] pArgs) throws Exception {

		//load spring application context 
		ClassPathXmlApplicationContext context = 
			new ClassPathXmlApplicationContext(
					ApplicationConstants.CORE_SPRING_CONFIG_FILE);
		
		//load dispatcher from spring
		Dispatcher dispatcher = (Dispatcher) context.getBean("Dispatcher");

		//configure an download job filter 
		DownloadJobFilter downloadFilter = new DownloadJobFilter();
		downloadFilter.setAllowedHostNames(new String[] {"cgap.*"});
		downloadFilter.setMaxRecursionDepth(2);
		downloadFilter.setSaveToDisk(new String[] {".*BioCarta/h_.*", ".*h_.*gif"});

		//add the filter to the dispatcher
		dispatcher.addJobFilter(downloadFilter);	
		
		RegExpJobFilter regExpFilter = new RegExpJobFilter();
		RegExpFilterRule regExpFilterRule = new RegExpJobFilter.RegExpFilterRule(".*Gene.*|.*m_.*|.*Kegg.*,.*Tissues.*");
		
		RegExpFilterAction regExpFilterAction = new RegExpJobFilter.RegExpFilterAction();
		regExpFilterAction.setAccept(false);
		
		regExpFilterRule.setMatchAction(regExpFilterAction);
		
		regExpFilter.addFilterRule(regExpFilterRule);
		
		dispatcher.addJobFilter(regExpFilter);
		
		//create an job factory
		DownloadJobFactory jobFactory = (DownloadJobFactory) 
			context.getBean("JobFactory");
		
		String sOutputFileName = System.getProperty("user.home") + 
			System.getProperty("file.separator") + "/.caleydo";
		
		//create an initial job
		UrlDownloadJob job = jobFactory.createDownloadJob();
		job.setUrl(new URL("http://cgap.nci.nih.gov/Pathways/BioCarta_Pathways"));
		job.setSavePath(new File(sOutputFileName));
		job.setIgnoreFilter(true);
		dispatcher.addJob(job);
		
		mLog.info("Start demo dispatcher");
		
		//start the dispatcher
		dispatcher.processJobs();
		
		mLog.info("Demo dispatcher finished");
		
		//dump all found urls
		Collection<Job> content = 
			dispatcher.getJobManager().getJobList().getContent();
		
		for (Job finishedJob : content) {
			mLog.info(finishedJob);
		}
	}
}
