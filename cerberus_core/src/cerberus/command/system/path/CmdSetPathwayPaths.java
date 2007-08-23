/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.system.path;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.IPathwayManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Command sets relevant file paths in PathwayMaanger.
 * 
 * @author Marc Streit
 */
public class CmdSetPathwayPaths 
extends ACmdCreate_IdTargetLabelAttrDetail {
	
	private String sPathwayXMLPath = "";
	
	private String sPathwayImagePath = "";
	
	private String sPathwayImageMapPath = "";
	
	/**
	 * Constructor.
	 */
	public CmdSetPathwayPaths( final IGeneralManager refGeneralManager,
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
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		IPathwayManager pathwayManager = 
			refGeneralManager.getSingelton().getPathwayManager();
		
		pathwayManager.setPathwayXMLPath(sPathwayXMLPath);
		pathwayManager.setPathwayImagePath(sPathwayImagePath);
		pathwayManager.setPathwayImageMapPath(sPathwayImageMapPath);
		
		refCommandManager.runDoCommand(this);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {

		
		refCommandManager.runUndoCommand(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.command.base.ACmdCreate_IdTargetLabelAttr#setParameterHandler(cerberus.xml.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "can not handle null object!";		
		
		super.setParameterHandler(refParameterHandler);
		
		sPathwayXMLPath = this.sAttribute1;
		sPathwayImagePath = this.sAttribute2;
		sPathwayImageMapPath = this.sAttribute3;
	}
	
	/**
	 * Sets the pathway file paths from software side.
	 * This method is needed when the command is triggered 
	 * inside the system during runtime.
	 */
	public void setAttributes(String sPathwayXMLPath,
			String sPathwayImagePath,
			String sPathwayImageMapPath) {
		
		this.sPathwayXMLPath = sPathwayXMLPath;
		this.sPathwayImagePath = sPathwayImagePath;
		this.sPathwayImageMapPath = sPathwayImageMapPath;			
	}
}
