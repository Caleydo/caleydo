package org.caleydo.core.application.helper.cacher.biocarta;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.caleydo.core.manager.IGeneralManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.phleisch.app.itsucks.constants.ApplicationConstants;
import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.filter.download.impl.DownloadJobFilter;
import de.phleisch.app.itsucks.filter.download.impl.RegExpJobFilter;
import de.phleisch.app.itsucks.filter.download.impl.RegExpJobFilter.RegExpFilterAction;
import de.phleisch.app.itsucks.filter.download.impl.RegExpJobFilter.RegExpFilterRule;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

/**
 * Fetch tool for BioCarta HTML and image files.
 * 
 * @author Marc Streit
 */
public class BioCartaGeneCacher
	extends Thread {
	int iDownloadCount = 0;

	@Override
	public void run() {
		super.run();

		// load spring application context
		ClassPathXmlApplicationContext context =
			new ClassPathXmlApplicationContext(ApplicationConstants.CORE_SPRING_CONFIG_FILE);

		// load dispatcher from spring
		Dispatcher dispatcher = (Dispatcher) context.getBean("Dispatcher");

		// configure an download job filter
		DownloadJobFilter downloadFilter = new DownloadJobFilter();
		downloadFilter.setAllowedHostNames(new String[] { "cgap.*" });
		downloadFilter.setMaxRecursionDepth(2);
		downloadFilter.setSaveToDisk(new String[] { ".*GeneInfo.*" });

		// add the filter to the dispatcher
		dispatcher.addJobFilter(downloadFilter);

		RegExpJobFilter regExpFilter = new RegExpJobFilter();
		RegExpFilterRule regExpFilterRule =
			new RegExpJobFilter.RegExpFilterRule(".*h_.*|.*Kegg.*,.*Tissues.*|.*SAGE.*");

		RegExpFilterAction regExpFilterAction = new RegExpJobFilter.RegExpFilterAction();
		regExpFilterAction.setAccept(false);

		regExpFilterRule.setMatchAction(regExpFilterAction);

		regExpFilter.addFilterRule(regExpFilterRule);

		dispatcher.addJobFilter(regExpFilter);

		// create an job factory
		DownloadJobFactory jobFactory = (DownloadJobFactory) context.getBean("JobFactory");

		String sOutputFileName =
			System.getProperty("user.home") + System.getProperty("file.separator")
				+ IGeneralManager.CALEYDO_FOLDER;

		// create an initial job
		UrlDownloadJob job = jobFactory.createDownloadJob();

		try {
			job.setUrl(new URL("http://cgap.nci.nih.gov/Pathways/BioCarta_Pathways"));
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

		job.setSavePath(new File(sOutputFileName));
		job.setIgnoreFilter(true);
		dispatcher.addJob(job);

		dispatcher.processJobs();
	}

	public static void main(String[] args) {
		new BioCartaGeneCacher().run();
	}
}
