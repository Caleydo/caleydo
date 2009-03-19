package org.caleydo.core.application.helper.cacher.kegg;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import org.caleydo.core.application.helper.PathwayListGenerator;
import org.caleydo.core.application.helper.cacher.APathwayCacher;
import org.caleydo.core.command.system.CmdFetchPathwayData;
import org.caleydo.core.manager.IGeneralManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
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
 * Fetch tool for KEGG images files.
 * 
 * @author Marc Streit
 */
public class KeggPathwayImageCacher
	extends APathwayCacher {
	/**
	 * Constructor.
	 */
	public KeggPathwayImageCacher(final Display display, final ProgressBar progressBar,
		final CmdFetchPathwayData triggeringCommand) {
		this.display = display;
		this.progressBar = progressBar;
		this.triggeringCommand = triggeringCommand;

		iExpectedDownloads = 610;
	}

	@Override
	public void run() {
		super.run();

		// load spring application context
		ClassPathXmlApplicationContext context =
			new ClassPathXmlApplicationContext(ApplicationConstants.CORE_SPRING_CONFIG_FILE);

		// load dispatcher from spring
		final Dispatcher dispatcher = (Dispatcher) context.getBean("Dispatcher");

		// configure an download job filter
		DownloadJobFilter downloadFilter = new DownloadJobFilter();
		downloadFilter.setAllowedHostNames(new String[] { "www.genome.ad.jp*" });
		downloadFilter.setMaxRecursionDepth(2);
		downloadFilter.setSaveToDisk(new String[] { ".*gif" });

		// add the filter to the dispatcher
		dispatcher.addJobFilter(downloadFilter);

		RegExpJobFilter regExpFilter = new RegExpJobFilter();
		RegExpFilterRule regExpFilterRule =
			new RegExpJobFilter.RegExpFilterRule(
				".*KGMLViewer.*|.*PathwayViewer.*|.*xmlview.*|.*html"
					+ "|.*atlas|.*css|.*menu.*|.*feedback.*|.*docs.|.*menu.*|.*compound.*|.*hsa\\+.*|.*Fig.*|.*glycan.*|.*up\\+.*|.*misc.*|.*document.*|javascrip.*");

		RegExpFilterAction regExpFilterAction = new RegExpJobFilter.RegExpFilterAction();
		regExpFilterAction.setAccept(false);

		regExpFilterRule.setMatchAction(regExpFilterAction);
		regExpFilter.addFilterRule(regExpFilterRule);

		dispatcher.addJobFilter(regExpFilter);

		DownloadJobFactory jobFactory = (DownloadJobFactory) context.getBean("JobFactory");

		String sOutputFileName = IGeneralManager.CALEYDO_HOME_PATH;

		UrlDownloadJob job = jobFactory.createDownloadJob();

		try {
			// job.setUrl(new
			// URL("http://www.genome.ad.jp/dbget-bin/get_pathway?org_name=hsa&mapno=00500"));
			job.setUrl(new URL(
				"http://www.genome.ad.jp/kegg-bin/show_organism?menu_type=pathway_maps&org=hsa"));
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		job.setSavePath(new File(sOutputFileName));
		job.setIgnoreFilter(true);
		dispatcher.addJob(job);

		processJobs(dispatcher);

		triggerPathwayListGeneration();

		if (triggeringCommand != null) {
			triggeringCommand.setFinishedKeggImageCacher();
		}
	}

	@Override
	protected void triggerPathwayListGeneration() {
		// Trigger pathway list generation
		PathwayListGenerator pathwayListLoader = new PathwayListGenerator();

		try {
			pathwayListLoader.run(PathwayListGenerator.INPUT_FOLDER_PATH_KEGG,
				PathwayListGenerator.INPUT_IMAGE_PATH_KEGG, PathwayListGenerator.OUTPUT_FILE_NAME_KEGG);
		}
		catch (FileNotFoundException fnfe) {
			throw new RuntimeException("Cannot generate KEGG pathway list.");
		}
	}
}