package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.view.opengl.canvas.remote.GLCanvasRemoteRendering3D;

/**
 * Command for setting flags in org.caleydo.core from the RCP interface.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdExternalFlagSetter
	extends ACmdExternalAttributes
{

	private boolean bFlag = false;

	private EExternalFlagSetterType externalFlagSetterType;

	private int iViewId;

	/**
	 * Constructor.
	 */
	public CmdExternalFlagSetter(final CommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{

		commandManager.runDoCommand(this);

		Object viewObject = generalManager.getViewGLCanvasManager().getItem(iViewId);

		if (viewObject instanceof GLCanvasRemoteRendering3D)
		{
			switch (externalFlagSetterType)
			{
				case PATHWAY_ENABLE_GENE_MAPPING:
					((GLCanvasRemoteRendering3D) viewObject).enableGeneMapping(bFlag);
					break;
				case PATHWAY_ENABLE_NEIGHBORHOOD:
					((GLCanvasRemoteRendering3D) viewObject).enableNeighborhood(bFlag);
					break;
				case PATHWAY_ENABLE_TEXTURES:
					((GLCanvasRemoteRendering3D) viewObject).enablePathwayTextures(bFlag);
					break;

				default:
					throw new CaleydoRuntimeException("unsupported EExternalFlagSetterType",
							CaleydoRuntimeExceptionType.DATAHANDLING);
			}
		}
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}

	public void setAttributes(final int iViewId, final boolean bFlag,
			final EExternalFlagSetterType externalFlagSetterType)
	{
		this.bFlag = bFlag;
		this.externalFlagSetterType = externalFlagSetterType;
		this.iViewId = iViewId;
	}
}
