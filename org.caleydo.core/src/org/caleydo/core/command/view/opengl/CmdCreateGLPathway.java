package org.caleydo.core.command.view.opengl;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.pathway.SerializedPathwayView;

/**
 * Create single OpenGL pathway view.
 * 
 * @author Marc Streit
 */
public class CmdCreateGLPathway
	extends CmdCreateGLEventListener {

	private int iPathwayID = -1;

	/**
	 * Constructor.
	 */
	public CmdCreateGLPathway(final ECommandType cmdType) {
		super(cmdType);
		this.iExternalID = iUniqueID;
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		iPathwayID = Integer.valueOf(sAttribute4).intValue();
	}

	public void setAttributes(final int iPathwayID, final ISet set,
		final EProjectionMode eProjectionMode, final float fLeft, final float fRight, final float fTop,
		final float fBottom, final float fNear, final float fFar) {
		super.setAttributes(eProjectionMode, fLeft, fRight, fBottom, fTop, fNear, fFar, set, -1);

		this.iPathwayID = iPathwayID;
		this.iExternalID = iUniqueID;
		iParentContainerId = -1;
	}

	@Override
	public void setAttributesFromSerializedForm(ASerializedView serView) {
		super.setAttributesFromSerializedForm(serView);
		SerializedPathwayView serPathway = (SerializedPathwayView) serView;
		setPathwayID(serPathway.getPathwayID());
	}

	public void setPathwayID(int pathwayID) {
		this.iPathwayID = pathwayID;
	}
	
	@Override
	public final void doCommand() {
		super.doCommand();

		((GLPathway) createdObject).setPathway(iPathwayID);
	}
}
