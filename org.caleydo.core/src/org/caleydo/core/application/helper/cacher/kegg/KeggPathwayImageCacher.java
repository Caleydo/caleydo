package org.caleydo.core.application.helper.cacher.kegg;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import org.caleydo.core.application.helper.PathwayListGenerator;
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
 * Fetch tool for KEGG images files.
 * 
 * @author Marc Streit
 */
public class KeggPathwayImageCacher
	extends APathwayCacher {

	private static final String FETCH_URL_HOMO_SAPIENS =
		"http://www.genome.jp/kegg-bin/show_organism?menu_type=pathway_maps&org=hsa";
	private static final String FETCH_URL_MUS_MUSCULUS =
		"http://www.genome.jp/kegg-bin/show_organism?menu_type=pathway_maps&org=mmu";

	/**
	 * Constructor.
	 */
	public KeggPathwayImageCacher(final Display display, final ProgressBar progressBar,
		final CmdFetchPathwayData triggeringCommand, final EOrganism eOrganism) {

		this.display = display;
		this.progressBar = progressBar;
		this.triggeringCommand = triggeringCommand;
		this.eOrganism = eOrganism;

		if (eOrganism == EOrganism.HOMO_SAPIENS) {
			sFetchURL = FETCH_URL_HOMO_SAPIENS;
			iExpectedDownloads = 612;
		}
		else if (eOrganism == EOrganism.MUS_MUSCULUS) {
			sFetchURL = FETCH_URL_MUS_MUSCULUS;
			iExpectedDownloads = 590;
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
		downloadFilter.setAllowedHostNames(new String[] { "www.genome.ad.jp*", "www.genome.jp*" });
		downloadFilter.setMaxRecursionDepth(2);
		downloadFilter.setSaveToDisk(new String[] { ".*png" });

		// add the filter to the dispatcher
		dispatcher.addJobFilter(downloadFilter);

		RegExpJobFilter regExpFilter = new RegExpJobFilter();
		RegExpFilterRule regExpFilterRule =
			new RegExpJobFilter.RegExpFilterRule(
				".*KGMLViewer.*|.*PathwayViewer.*|.*xmlview.*|.*html"
					+ "|.*atlas|.*css|.*menu.*|.*feedback.*|.*docs.|.*menu.*|.*compound.*|.*hsa\\+.*|.*mmu\\+.*|.*Fig.*|.*glycan.*|.*up\\+.*|.*misc.*|.*hsa:.*|.*mmu:.*|.*www_bget\\?G.*|.*www_bget\\?C.*|.*document.*|javascrip.*");

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
		job.setSavePath(new File(sOutputFileName));
		job.setIgnoreFilter(true);
		dispatcher.addJob(job);

		processJobs(dispatcher);

		triggerPathwayListGeneration();

		if (triggeringCommand != null) {
//			progressBar.setSelection(100);
			triggeringCommand.setFinishedKeggImageCacher();
		}
	}

	@Override
	protected void triggerPathwayListGeneration() {
		// Trigger pathway list generation
		PathwayListGenerator pathwayListLoader = new PathwayListGenerator();

		try {

			if (eOrganism == EOrganism.HOMO_SAPIENS)
				pathwayListLoader.run(PathwayListGenerator.INPUT_FOLDER_PATH_KEGG_HOMO_SAPIENS,
					PathwayListGenerator.INPUT_IMAGE_PATH_KEGG_HOMO_SAPIENS,
					PathwayListGenerator.OUTPUT_FILE_NAME_KEGG_HOMO_SAPIENS);
			else if (eOrganism == EOrganism.MUS_MUSCULUS)
				pathwayListLoader.run(PathwayListGenerator.INPUT_FOLDER_PATH_KEGG_MUS_MUSCULUS,
					PathwayListGenerator.INPUT_IMAGE_PATH_KEGG_MUS_MUSCULUS,
					PathwayListGenerator.OUTPUT_FILE_NAME_KEGG_MUS_MUSCULUS);
			else
				throw new IllegalStateException("Cannot fetch pathways from organism " + eOrganism);

		}
		catch (FileNotFoundException fnfe) {
			throw new RuntimeException("Cannot generate KEGG pathway list.");
		}
	}
}