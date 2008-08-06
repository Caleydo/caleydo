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
import org.eclipse.jface.wizard.WizardPage;
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
	private Display display = null;
	private ProgressBar progressBarKeggPathwayCacher = null;
	private ProgressBar progressBarKeggPathwayImageCacher = null;
	private ProgressBar progressBarBioCartaPathwayCacher = null;
	private WizardPage parentWizardPage = null;
	
	private boolean isKeggCacherFinished = false;
	private boolean isKeggImageCacherFinished = false;
	private boolean isBioCartaCacherFinished = false;
	
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
		BioCartaPathwayCacher bioCartaPathwayCacher = new BioCartaPathwayCacher(
				generalManager, display,
				progressBarBioCartaPathwayCacher, this);
		bioCartaPathwayCacher.start();

		KeggPathwayCacher keggPathwayCacher = new KeggPathwayCacher(
				generalManager, display,
				progressBarKeggPathwayCacher, this);
		keggPathwayCacher.start();

		KeggPathwayImageCacher keggPathwayImageCacher = new KeggPathwayImageCacher(
				generalManager, display,
				progressBarKeggPathwayImageCacher, this);
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
			final ProgressBar progressBarBioCartaPathwayCacher,
			final WizardPage parentWizardPage)
	{
		this.display = display;
		this.progressBarKeggPathwayCacher = progressBarKeggPathwayCacher;
		this.progressBarKeggPathwayImageCacher = progressBarKeggPathwayImageCacher;
		this.progressBarBioCartaPathwayCacher = progressBarBioCartaPathwayCacher;
		this.parentWizardPage = parentWizardPage;
	}
	
	public void setFinishedKeggCacher() 
	{
		isKeggCacherFinished = true;
		notifyWizard();
	}
	
	public void setFinishedKeggImageCacher() 
	{
		isKeggImageCacherFinished = true;
		notifyWizard();
	}
	
	public void setFinishedBioCartaCacher() 
	{
		isBioCartaCacherFinished = true;
		notifyWizard();
	}
	
	public void notifyWizard() 
	{
		if (parentWizardPage == null)
			return;
		
		if (isKeggCacherFinished && isKeggImageCacherFinished && isBioCartaCacherFinished)
		{
			display.asyncExec(new Runnable()
			{
				public void run()
				{
					parentWizardPage.setPageComplete(true);		
				}
			});
		}
	}
}
