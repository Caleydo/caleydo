package org.caleydo.core.command.view.opengl;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.media.opengl.GLEventListener;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParent;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.camera.ViewFrustumBase;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.parser.parameter.IParameterHandler.ParameterHandlerType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;

/**
 * Command creates OpenGL views.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdCreateOpenGLCanvasListener
	extends ACmdCreate_IdTargetLabelParent
{

	protected CommandType viewType;

	protected transient GLEventListener glEventListener;

	protected IViewFrustum viewFrustum;

	protected Vec3f cameraOrigin;

	protected Rotf cameraRotation;

	protected ArrayList<Integer> iArSetIDs;

	protected ArrayList<Integer> iArSelectionIDs;

	/**
	 * Constructor.
	 */
	public CmdCreateOpenGLCanvasListener(final CommandType cmdType)
	{
		super(cmdType);

		cameraRotation = new Rotf();
		cameraOrigin = new Vec3f(0, 0, 0);

		viewType = cmdType;

		iArSetIDs = new ArrayList<Integer>();
		iArSelectionIDs = new ArrayList<Integer>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParent#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		extractDataIDs();

		String sPositionGLOrigin = parameterHandler
				.getValueString(CommandType.TAG_POS_GL_ORIGIN.getXmlKey());

		String sPositionGLRotation = parameterHandler
				.getValueString(CommandType.TAG_POS_GL_ROTATION.getXmlKey());

		/* convert values.. */
		if (sPositionGLOrigin != null)
		{
			parameterHandler.setValueAndTypeAndDefault(CommandType.TAG_POS_GL_ORIGIN
					.getXmlKey(), sPositionGLOrigin, ParameterHandlerType.VEC3F,
					CommandType.TAG_POS_GL_ORIGIN.getDefault());
		}

		if (sPositionGLRotation != null)
		{
			parameterHandler.setValueAndTypeAndDefault(CommandType.TAG_POS_GL_ROTATION
					.getXmlKey(), sPositionGLRotation, ParameterHandlerType.VEC4F,
					CommandType.TAG_POS_GL_ROTATION.getDefault());
		}

		cameraOrigin = parameterHandler.getValueVec3f(CommandType.TAG_POS_GL_ORIGIN
				.getXmlKey());

		/* convert Vec4f to roation Rotf */
		Vec4f vec4fRotation = parameterHandler
				.getValueVec4f(CommandType.TAG_POS_GL_ROTATION.getXmlKey());

		cameraRotation.set(new Vec3f(vec4fRotation.x(), vec4fRotation.y(), vec4fRotation.z()),
				(float) Math.toRadians(vec4fRotation.w()));

		StringTokenizer frustumToken = new StringTokenizer(sAttribute3,
				IGeneralManager.sDelimiter_Parser_DataItems);

		try
		{
			// Parse projection mode (PERSPECTIVE / ORTHOGRAPHIC)
			String sProjectionMode = "";

			if (frustumToken.hasMoreTokens())
				sProjectionMode = frustumToken.nextToken();

			if (!sProjectionMode.equals(ViewFrustumBase.ProjectionMode.ORTHOGRAPHIC.name())
					&& !sProjectionMode.equals(ViewFrustumBase.ProjectionMode.PERSPECTIVE
							.name()))
			{
				return;
			}

			float fLeft = -1;
			float fRight = -1;
			float fBottom = -1;
			float fTop = -1;
			float fNear = -1;
			float fFar = -1;

			fLeft = StringConversionTool.convertStringToFloat(frustumToken.nextToken(), -1);

			fRight = StringConversionTool.convertStringToFloat(frustumToken.nextToken(), -1);

			fBottom = StringConversionTool.convertStringToFloat(frustumToken.nextToken(), -1);

			fTop = StringConversionTool.convertStringToFloat(frustumToken.nextToken(), -1);

			fNear = StringConversionTool.convertStringToFloat(frustumToken.nextToken(), -1);

			fFar = StringConversionTool.convertStringToFloat(frustumToken.nextToken(), -1);

			viewFrustum = new ViewFrustumBase(ViewFrustumBase.ProjectionMode
					.valueOf(sProjectionMode), fLeft, fRight, fBottom, fTop, fNear, fFar);

		}
		catch (Exception e)
		{

			generalManager.getLogger().log(Level.SEVERE,
					"Error in extraction view frustum from XML argument.", e);
		}
	}

	/**
	 * Extract set and selection IDs from detail string. Example:
	 * "SET_ID_1 SET_ID_2@SELECTION_ID_1 SELECTION_ID_2"
	 */
	private void extractDataIDs()
	{

		// Read Set and Selection IDs
		StringTokenizer divideSetAndSelectionIDs = new StringTokenizer(sDetail,
				IGeneralManager.sDelimiter_Paser_DataItemBlock);

		// Fill set IDs
		if (divideSetAndSelectionIDs.hasMoreTokens())
		{
			StringTokenizer divideIDs = new StringTokenizer(divideSetAndSelectionIDs
					.nextToken(), IGeneralManager.sDelimiter_Parser_DataItems);

			while (divideIDs.hasMoreTokens())
			{
				iArSetIDs.add(StringConversionTool.convertStringToInt(divideIDs.nextToken(),
						-1));
			}
		}

		// Fill selection IDs
		if (divideSetAndSelectionIDs.hasMoreTokens())
		{
			StringTokenizer divideIDs = new StringTokenizer(divideSetAndSelectionIDs
					.nextToken(), IGeneralManager.sDelimiter_Parser_DataItems);

			while (divideIDs.hasMoreTokens())
			{
				iArSelectionIDs.add(StringConversionTool.convertStringToInt(divideIDs
						.nextToken(), -1));
			}
		}
	}

	public void setAttributes(final ViewFrustumBase.ProjectionMode projectionMode,
			final float fLeft, final float fRight, final float fTop, final float fBottom,
			final float fNear, final float fFar, final ArrayList<Integer> iArSetIDs,
			final ArrayList<Integer> iArSelectionIDs)
	{

		viewFrustum = new ViewFrustumBase(projectionMode, fLeft, fRight, fBottom, fTop, fNear,
				fFar);

		this.iArSetIDs = iArSetIDs;
		this.iArSelectionIDs = iArSelectionIDs;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand()
	{

		IViewGLCanvasManager glCanvasManager = generalManager.getViewGLCanvasManager();

		glEventListener = glCanvasManager.createGLCanvas(viewType, iExternalID,
				iParentContainerId, sLabel, viewFrustum);

		((AGLCanvasUser) glEventListener).getViewCamera().setCameraPosition(cameraOrigin);
		((AGLCanvasUser) glEventListener).getViewCamera().setCameraRotation(cameraRotation);

		// Set sets in views
		AGLCanvasUser glCanvas = ((AGLCanvasUser) glEventListener);
		for (Integer iSetID : iArSetIDs)
		{
			glCanvas.addSet(iSetID);
		}

		// Set selections in views
		for (Integer iSelectionID : iArSelectionIDs)
		{
			glCanvas.addSelection(iSelectionID);
		}

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
}
