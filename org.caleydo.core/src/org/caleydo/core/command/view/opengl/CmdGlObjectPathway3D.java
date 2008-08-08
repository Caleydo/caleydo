package org.caleydo.core.command.view.opengl;

import java.util.ArrayList;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.data.view.camera.ViewFrustumBase;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.pathway.GLCanvasPathway3D;

/**
 * Create single OpenGL pathway view.
 * 
 * @author Marc Streit
 */
public class CmdGlObjectPathway3D
	extends CmdCreateGLEventListener
{

	private int iPathwayID = -1;

	/**
	 * Constructor.
	 */
	public CmdGlObjectPathway3D(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.view.opengl.CmdCreateOpenGLCanvasListener#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		iPathwayID = StringConversionTool.convertStringToInt(sAttribute4, -1);
	}

	public void setAttributes(final int iPathwayID,
			final ArrayList<Integer> iArSetIDs, final ArrayList<Integer> iArSelectionIDs,
			final ViewFrustumBase.ProjectionMode projectionMode, final float fLeft,
			final float fRight, final float fTop, final float fBottom, final float fNear,
			final float fFar)
	{

		super.setAttributes(projectionMode, fLeft, fRight, fTop, fBottom, fNear, fFar,
				iArSetIDs, iArSelectionIDs);

		this.iAlSetIDs = iArSetIDs;
		this.iPathwayID = iPathwayID;
		this.iExternalID = iUniqueID;
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
