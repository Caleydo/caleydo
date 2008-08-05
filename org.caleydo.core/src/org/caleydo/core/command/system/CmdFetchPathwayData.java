package org.caleydo.core.command.system;

import org.caleydo.core.application.helper.cacher.biocarta.BioCartaPathwayCacher;
import org.caleydo.core.application.helper.cacher.kegg.KeggPathwayCacher;
import org.caleydo.core.application.helper.cacher.kegg.KeggPathwayImageCacher;
import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * Command triggers helper tools that fetch data from pathway databases.
 * 
 * @author Marc Streit
 */
public class CmdFetchPathwayData
	extends ACmdCreate_IdTargetLabelAttrDetail
{
	Display display = null;
	ProgressBar progressBarKeggPathwayCacher = null;
	ProgressBar progressBarKeggPathwayImageCacher = null;
	ProgressBar progressBarBioCartaPathwayCacher = null;
	
	/**
	 * Constructor.
	 */
	public CmdFetchPathwayData(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{
		super(generalManager, commandManager, commandQueueSaxType);

		setCommandQueueSaxType(CommandQueueSaxType.LOAD_DATA_FILE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		BioCartaPathwayCacher bioCartaPathwayCacher = new BioCartaPathwayCacher(display,
				progressBarBioCartaPathwayCacher);
		bioCartaPathwayCacher.start();

		KeggPathwayCacher keggPathwayCacher = new KeggPathwayCacher(display,
				progressBarKeggPathwayCacher);
		keggPathwayCacher.start();

		KeggPathwayImageCacher keggPathwayImageCacher = new KeggPathwayImageCacher(display,
				progressBarKeggPathwayImageCacher);
		keggPathwayImageCacher.start();

		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttr#
	 * setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);
	}
	
	public void setAttributes(final Display display, 
			final ProgressBar progressBarKeggPathwayCacher,
			final ProgressBar progressBarKeggPathwayImageCacher,
			final ProgressBar progressBarBioCartaPathwayCacher)
	{
		this.display = display;
		this.progressBarKeggPathwayCacher = progressBarKeggPathwayCacher;
		this.progressBarKeggPathwayImageCacher = progressBarKeggPathwayImageCacher;
		this.progressBarBioCartaPathwayCacher = progressBarBioCartaPathwayCacher;
	}
}
