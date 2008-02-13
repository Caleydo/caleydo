/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.command.system.path;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.data.IPathwayManager;
import org.geneview.core.manager.data.pathway.EPathwayDatabaseType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;

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
	public CmdSetPathwayDatabasePath( final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
	}
	
	/**
	 * Set pathway file paths in PathwayManager.
	 * Relevant paths are:
	 *  - XML sources
	 *  - Image maps
	 *  - Background overlay images/textures
	 * 
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		IPathwayManager pathwayManager = 
			generalManager.getSingelton().getPathwayManager();
		
		pathwayManager.createPathwayDatabase(type, sXMLPath, sImagePath, sImageMapPath);

		refCommandManager.runDoCommand(this);
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {

		
		refCommandManager.runUndoCommand(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.base.ACmdCreate_IdTargetLabelAttr#setParameterHandler(org.geneview.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "can not handle null object!";		
		
		super.setParameterHandler(refParameterHandler);
		
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
