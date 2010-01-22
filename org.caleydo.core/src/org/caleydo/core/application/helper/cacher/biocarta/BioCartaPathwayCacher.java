package org.caleydo.core.application.helper.cacher.biocarta;

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
 * Fetch tool for BioCarta HTML and image files.
 * 
 * @author Marc Streit
 */
public class BioCartaPathwayCacher
	extends APathwayCacher {

	private static final String FETCH_URL = "http://cgap.nci.nih.gov/Pathways/BioCarta_Pathways";

	/**
	 * Constructor.
	 */
	public BioCartaPathwayCacher(final Display display, final ProgressBar progressBar,
		final CmdFetchPathwayData triggeringCommand, final EOrganism eOrganism) {

		this.display = display;
		this.progressBar = progressBar;
		this.triggeringCommand = triggeringCommand;
		this.eOrganism = eOrganism;

		sFetchURL = FETCH_URL;

		if (eOrganism == EOrganism.HOMO_SAPIENS) {
			iExpectedDownloads = 870;
		}
		else if (eOrganism == EOrganism.MUS_MUSCULUS) {
			iExpectedDownloads = 830;
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
		Dispatcher dispatcher = (Dispatcher) context.getBean("Dispatcher");

		// configure an download job filter
		DownloadJobFilter downloadFilter = new DownloadJobFilter();
		downloadFilter.setAllowedHostNames(new String[] { "cgap.*" });
		downloadFilter.setMaxRecursionDepth(2);

		String sOrganismIdentifier = "";
		String sOrganismFilter = "";
		if (eOrganism == EOrganism.HOMO_SAPIENS) {
			sOrganismIdentifier = "h";
			sOrganismFilter = "m";
		}
		else if (eOrganism == EOrganism.MUS_MUSCULUS) {
			sOrganismIdentifier = "m";
			sOrganismFilter = "h";
		}
		else
			throw new IllegalStateException("Cannot fetch pathways from organism " + eOrganism);

		downloadFilter.setSaveToDisk(new String[] { ".*BioCarta/" + sOrganismIdentifier + "_.*",
				".*" + sOrganismIdentifier + "_.*gif" });

		// add the filter to the dispatcher
		dispatcher.addJobFilter(downloadFilter);

		RegExpJobFilter regExpFilter = new RegExpJobFilter();
		RegExpFilterRule regExpFilterRule =
			new RegExpJobFilter.RegExpFilterRule(".*Gene.*|.*" + sOrganismFilter
				+ "_.*|.*Kegg.*,.*Tissues.*|.*SAGE.*");

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
			// progressBar.setSelection(100);
			triggeringCommand.setFinishedBioCartaCacher();
		}
	}

	@Override
	protected void triggerPathwayListGeneration() {
		// Trigger pathway list generation
		PathwayListGenerator pathwayListLoader = new PathwayListGenerator();

		try {

			if (eOrganism == EOrganism.HOMO_SAPIENS)
				pathwayListLoader.run(PathwayListGenerator.INPUT_FOLDER_PATH_BIOCARTA,
					PathwayListGenerator.INPUT_IMAGE_PATH_BIOCARTA,
					PathwayListGenerator.OUTPUT_FILE_NAME_BIOCARTA_HOMO_SAPIENS);
			else if (eOrganism == EOrganism.MUS_MUSCULUS)
				pathwayListLoader.run(PathwayListGenerator.INPUT_FOLDER_PATH_BIOCARTA,
					PathwayListGenerator.INPUT_IMAGE_PATH_BIOCARTA,
					PathwayListGenerator.OUTPUT_FILE_NAME_BIOCARTA_MUS_MUSCULUS);
			else
				throw new IllegalStateException("Cannot fetch pathways from organism " + eOrganism);

		}
		catch (FileNotFoundException fnfe) {
			throw new RuntimeException("Cannot generate BioCarta pathway list.");
		}
	}
}
