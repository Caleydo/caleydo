package org.caleydo.core.application.helper.cacher.kegg;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.caleydo.core.application.helper.cacher.APathwayCacher;
import org.caleydo.core.command.system.CmdFetchPathwayData;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
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
 * Fetch tool for KEGG XML files.
 * 
 * @author Marc Streit
 */
public class KeggPathwayCacher
	extends APathwayCacher {

	private static final String FETCH_URL_HOMO_SAPIENS = "http://www.genome.jp/kegg/xml/hsa/index.html";
	private static final String FETCH_URL_MUS_MUSCULUS = "http://www.genome.jp/kegg/xml/mmu/index.html";

	/**
	 * Constructor.
	 */
	public KeggPathwayCacher(final Display display, final ProgressBar progressBar,
		final CmdFetchPathwayData triggeringCommand, final EOrganism eOrganism) {

		this.display = display;
		this.progressBar = progressBar;
		this.triggeringCommand = triggeringCommand;
		this.eOrganism = eOrganism;

		if (eOrganism == EOrganism.HOMO_SAPIENS) {
			sFetchURL = FETCH_URL_HOMO_SAPIENS;
			iExpectedDownloads = 244;
		}
		else if (eOrganism == EOrganism.MUS_MUSCULUS) {
			sFetchURL = FETCH_URL_MUS_MUSCULUS;
			iExpectedDownloads = 235;
		}
		else
			throw new IllegalStateException("Cannot fetch pathways from organism " + eOrganism);
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
		downloadFilter.setAllowedHostNames(new String[] { "www.genome.jp.*" });
		downloadFilter.setMaxRecursionDepth(3);
		downloadFilter.setSaveToDisk(new String[] { ".*xml|.*gif" });

		// add the filter to the dispatcher
		dispatcher.addJobFilter(downloadFilter);

		RegExpJobFilter regExpFilter = new RegExpJobFilter();
		RegExpFilterRule regExpFilterRule =
			new RegExpJobFilter.RegExpFilterRule(
				".*KGMLViewer.*|.*PathwayViewer.*|.*xmlview.*|.*dbget.*|.*html"
					+ "|.*atlas|.*css|.*menu.*|.*feedback.*|.*docs.|.*br.*|.*menu.*|.*Fig.*");

		RegExpFilterAction regExpFilterAction = new RegExpJobFilter.RegExpFilterAction();
		regExpFilterAction.setAccept(false);

		regExpFilterRule.setMatchAction(regExpFilterAction);

		regExpFilter.addFilterRule(regExpFilterRule);

		dispatcher.addJobFilter(regExpFilter);

		DownloadJobFactory jobFactory = (DownloadJobFactory) context.getBean("JobFactory");

		String sOutputFileName = IGeneralManager.CALEYDO_HOME_PATH;

		UrlDownloadJob job = jobFactory.createDownloadJob();

		try {
			job.setUrl(new URL(sFetchURL));
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// "http://www.genome.jp/kegg/ pathway/hsa00380.gif
		job.setSavePath(new File(sOutputFileName));
		job.setIgnoreFilter(true);
		dispatcher.addJob(job);

		processJobs(dispatcher);

		// triggerPathwayListGeneration();

		if (triggeringCommand != null) {
			// progressBar.setSelection(100);
			triggeringCommand.setFinishedKeggCacher();
		}
	}

	@Override
	protected void triggerPathwayListGeneration() {
		throw new IllegalStateException("Pathway list generation is not supported!");
	}
}