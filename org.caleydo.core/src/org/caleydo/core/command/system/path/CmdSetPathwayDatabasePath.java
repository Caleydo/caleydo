package org.caleydo.core.command.system.path;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.specialized.genetic.IPathwayManager;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Command sets relevant file paths in PathwayMaanger.
 * 
 * @author Marc Streit
 */
public class CmdSetPathwayDatabasePath
	extends ACmdExternalAttributes {

	private EPathwayDatabaseType type;

	private String sXMLPath = "";

	private String sImagePath = "";

	private String sImageMapPath = "";

	/**
	 * Constructor.
	 */
	public CmdSetPathwayDatabasePath(final ECommandType cmdType) {
		super(cmdType);
	}

	/**
	 * Set pathway file paths in PathwayManager. Relevant paths are: - XML sources - Image maps - Background
	 * overlay images/textures
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() {

		IPathwayManager pathwayManager = generalManager.getPathwayManager();

		pathwayManager.createPathwayDatabase(type, sXMLPath, sImagePath, sImageMapPath);

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {

		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		assert parameterHandler != null : "can not handle null object!";

		super.setParameterHandler(parameterHandler);

		type = EPathwayDatabaseType.valueOf(this.sDetail);

		sXMLPath = this.sAttribute1;
		sImagePath = this.sAttribute2;
		sImageMapPath = this.sAttribute3;
	}

	/**
	 * Sets the pathway file paths from software side. This method is needed when the command is triggered
	 * inside the system during runtime.
	 */
	public void setAttributes(final EPathwayDatabaseType type, final String sPathwayXMLPath,
		final String sPathwayImagePath, final String sPathwayImageMapPath) {

		this.type = type;
		this.sXMLPath = sPathwayXMLPath;
		this.sImagePath = sPathwayImagePath;
		this.sImageMapPath = sPathwayImageMapPath;
	}
}
