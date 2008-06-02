package org.caleydo.core.command.system.path;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IPathwayManager;
import org.caleydo.core.manager.data.pathway.EPathwayDatabaseType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command sets relevant file paths in PathwayMaanger.
 * 
 * @author Marc Streit
 */
public class CmdSetPathwayDatabasePath 
extends ACmdCreate_IdTargetLabelAttrDetail {
	
	private EPathwayDatabaseType type;
	private String sXMLPath = "";
	private String sImagePath = "";
	private String sImageMapPath = "";
	
	/**
	 * Constructor.
	 */
	public CmdSetPathwayDatabasePath( final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) {
		
		super(generalManager,
				commandManager,
				commandQueueSaxType);
	}
	
	/**
	 * Set pathway file paths in PathwayManager.
	 * Relevant paths are:
	 *  - XML sources
	 *  - Image maps
	 *  - Background overlay images/textures
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		IPathwayManager pathwayManager = 
			generalManager.getPathwayManager();
		
		pathwayManager.createPathwayDatabase(type, sXMLPath, sImagePath, sImageMapPath);

		commandManager.runDoCommand(this);
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {

		
		commandManager.runUndoCommand(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttr#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler( final IParameterHandler parameterHandler ) {
		
		assert parameterHandler != null: "can not handle null object!";		
		
		super.setParameterHandler(parameterHandler);
		
		type = EPathwayDatabaseType.valueOf(this.sDetail);
		
		sXMLPath = this.sAttribute1;
		sImagePath = this.sAttribute2;
		sImageMapPath = this.sAttribute3;
	}
	
	/**
	 * Sets the pathway file paths from software side.
	 * This method is needed when the command is triggered 
	 * inside the system during runtime.
	 */
	public void setAttributes(final EPathwayDatabaseType type,
			final String sPathwayXMLPath,
			final String sPathwayImagePath,
			final String sPathwayImageMapPath) {
		
		this.type = type;
		this.sXMLPath = sPathwayXMLPath;
		this.sImagePath = sPathwayImagePath;
		this.sImageMapPath = sPathwayImageMapPath;			
	}
}
