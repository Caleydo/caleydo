package org.caleydo.core.command.view.opengl;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.data.view.camera.ViewFrustumBase;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.parser.parameter.IParameterHandler.ParameterHandlerType;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.pathway.GLCanvasPathway3D;

/**
 * Create single OpenGL pathway view.
 * 
 * @author Marc Streit
 */
public class CmdGlObjectPathway3D
	extends CmdCreateOpenGLCanvasListener
{

	private int iPathwayID = -1;

	/**
	 * Constructor.
	 */
	public CmdGlObjectPathway3D(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.command.base.ACmdCreate_GlCanvasUser#setParameterHandler
	 * (org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		super.setParameterHandler(parameterHandler);

		iPathwayID = StringConversionTool.convertStringToInt(sAttribute4, -1);
	}

	public void setAttributes(final int iUniqueID, final int iPathwayID,
			final ArrayList<Integer> iArSetIDs, final ArrayList<Integer> iArSelectionIDs,
			final ViewFrustumBase.ProjectionMode projectionMode, final float fLeft,
			final float fRight, final float fTop, final float fBottom, final float fNear,
			final float fFar)
	{

		super.setAttributes(projectionMode, fLeft, fRight, fTop, fBottom, fNear, fFar,
				iArSetIDs, iArSelectionIDs);

		this.iArSetIDs = iArSetIDs;
		this.iPathwayID = iPathwayID;
		this.iUniqueId = iUniqueID;
		iParentContainerId = -1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_GlCanvasUser#doCommand()
	 */
	public final void doCommand()
	{

		super.doCommand();

		((GLCanvasPathway3D) glEventListener).setPathwayID(iPathwayID);
	}
}
