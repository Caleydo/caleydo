package org.caleydo.core.command.view.opengl;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.util.StringTokenizer;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.parser.parameter.IParameterHandler.ParameterHandlerType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;

/**
 * Command creates OpenGL views.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class CmdCreateView
	extends ACmdCreational<AGLView> {

	protected String viewID;

	protected IViewFrustum viewFrustum;

	protected Vec3f cameraOrigin;
	protected Rotf cameraRotation;

	/**
	 * Constructor.
	 */
	public CmdCreateView(final ECommandType cmdType) {
		super(cmdType);

		cameraRotation = new Rotf();
		cameraOrigin = new Vec3f(0, 0, 0);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		String sPositionGLOrigin =
			parameterHandler.getValueString(ECommandType.TAG_POS_GL_ORIGIN.getXmlKey());

		String sPositionGLRotation =
			parameterHandler.getValueString(ECommandType.TAG_POS_GL_ROTATION.getXmlKey());

		/* convert values.. */
		if (sPositionGLOrigin != null) {
			parameterHandler.setValueAndTypeAndDefault(ECommandType.TAG_POS_GL_ORIGIN.getXmlKey(),
				sPositionGLOrigin, ParameterHandlerType.VEC3F, ECommandType.TAG_POS_GL_ORIGIN.getDefault());
		}

		if (sPositionGLRotation != null) {
			parameterHandler.setValueAndTypeAndDefault(ECommandType.TAG_POS_GL_ROTATION.getXmlKey(),
				sPositionGLRotation, ParameterHandlerType.VEC4F, ECommandType.TAG_POS_GL_ROTATION
					.getDefault());
		}

		cameraOrigin = parameterHandler.getValueVec3f(ECommandType.TAG_POS_GL_ORIGIN.getXmlKey());

		/* convert Vec4f to roation Rotf */
		Vec4f vec4fRotation = parameterHandler.getValueVec4f(ECommandType.TAG_POS_GL_ROTATION.getXmlKey());

		cameraRotation.set(new Vec3f(vec4fRotation.x(), vec4fRotation.y(), vec4fRotation.z()), (float) Math
			.toRadians(vec4fRotation.w()));

		StringTokenizer frustumToken =
			new StringTokenizer(sAttribute3, IGeneralManager.sDelimiter_Parser_DataItems);

		// try
		// {
		// Parse projection mode (PERSPECTIVE / ORTHOGRAPHIC)
		String sProjectionMode = "";

		if (frustumToken.hasMoreTokens()) {
			sProjectionMode = frustumToken.nextToken();
		}

		if (!sProjectionMode.equals(EProjectionMode.ORTHOGRAPHIC.name())
			&& !sProjectionMode.equals(EProjectionMode.PERSPECTIVE.name()))
			return;

		float fLeft = -1;
		float fRight = -1;
		float fBottom = -1;
		float fTop = -1;
		float fNear = -1;
		float fFar = -1;

		fLeft = new Float(frustumToken.nextToken());
		fRight = new Float(frustumToken.nextToken());
		fBottom = new Float(frustumToken.nextToken());
		fTop = new Float(frustumToken.nextToken());
		fNear = new Float(frustumToken.nextToken());
		fFar = new Float(frustumToken.nextToken());

		viewFrustum =
			new ViewFrustum(EProjectionMode.valueOf(sProjectionMode), fLeft, fRight, fBottom, fTop, fNear,
				fFar);
	}

	

	public void setAttributes(final EProjectionMode eProjectionMode,
		final float fLeft, final float fRight, final float fBottom, final float fTop, final float fNear,
		final float fFar, final int iParentCanvasID) {
		viewFrustum = new ViewFrustum(eProjectionMode, fLeft, fRight, fBottom, fTop, fNear, fFar);

		this.iParentContainerId = iParentCanvasID;
	}

	public void setAttributes(final EProjectionMode eProjectionMode,
		final float fLeft, final float fRight, final float fBottom, final float fTop, final float fNear,
		final float fFar, final int iParentCanvasID, final float fCamOriginX, final float fCamOriginY,
		final float fCamOriginZ, final float fCamRotationX, final float fCamRotationY,
		final float fCamRotationZ, final float fCamRotationAngle) {

		setAttributes( eProjectionMode, fLeft, fRight, fBottom, fTop, fNear, fFar,
			iParentCanvasID);

		cameraOrigin.set(fCamOriginX, fCamOriginY, fCamOriginZ);
		cameraRotation.set(new Vec3f(fCamRotationX, fCamRotationY, fCamRotationZ), (float) Math
			.toRadians(fCamRotationAngle));
	}

	/**
	 * Sets the attributes of this command according to the given serialized form. Inherited classes should
	 * override this method to set the view depended attributes.
	 * 
	 * @param serView
	 *            serialized form of the view to create
	 */
	public void setAttributesFromSerializedForm(ASerializedView serView) {
		setViewFrustum(serView.getViewFrustum());
	}

	public void setParentCanvasID(int parentCanvasID) {
		this.iParentContainerId = parentCanvasID;
	}

	public void setViewID(String viewID) {
		this.viewID = viewID;
	}

	public void setViewFrustum(ViewFrustum viewFrustum) {
		this.viewFrustum = viewFrustum;
	}

	@Override
	public void doCommand() {

		IViewManager glCanvasManager = generalManager.getViewGLCanvasManager();

		if (iExternalID != -1 && iParentContainerId != -1) {
			iParentContainerId = generalManager.getIDManager().getInternalFromExternalID(iParentContainerId);
		}

		GLCaleydoCanvas glCanvas = generalManager.getViewGLCanvasManager().getCanvas(iParentContainerId);

		createdObject = glCanvasManager.createGLView(viewID, glCanvas, sLabel, viewFrustum);

		if (iExternalID != -1) {
			generalManager.getIDManager().mapInternalToExternalID(createdObject.getID(), iExternalID);
		}

		createdObject.getViewCamera().setCameraPosition(cameraOrigin);
		createdObject.getViewCamera().setCameraRotation(cameraRotation);

//		IDataDomain dataDomain = DataDomainManager.getInstance().getDataDomain(dataDomainType);
//
//		// Note: for AStoragebasedViews the set for the view is assigned in the setDataDomain method.
//		createdObject.setDataDomain(dataDomain);

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}

}
