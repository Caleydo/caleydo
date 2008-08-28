package org.caleydo.core.command.system;

import org.caleydo.core.application.helper.cacher.biocarta.BioCartaPathwayCacher;
import org.caleydo.core.application.helper.cacher.kegg.KeggPathwayCacher;
import org.caleydo.core.application.helper.cacher.kegg.KeggPathwayImageCacher;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
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
	extends ACmdExternalAttributes
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
	public CmdFetchPathwayData(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		BioCartaPathwayCacher bioCartaPathwayCacher = new BioCartaPathwayCacher(display,
				progressBarBioCartaPathwayCacher, this);
		bioCartaPathwayCacher.start();

		KeggPathwayCacher keggPathwayCacher = new KeggPathwayCacher(display,
				progressBarKeggPathwayCacher, this);
		keggPathwayCacher.start();

		KeggPathwayImageCacher keggPathwayImageCacher = new KeggPathwayImageCacher(display,
				progressBarKeggPathwayImageCacher, this);
		keggPathwayImageCacher.start();

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}

	@Override
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
