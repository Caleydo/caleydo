package org.caleydo.core.command.view;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.util.StringTokenizer;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.view.ViewManager;
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
	protected String dataDomainType;

	protected IViewFrustum viewFrustum;

	protected Vec3f cameraOrigin;
	protected Rotf cameraRotation;

	protected Integer parentCanvasID = -1;

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
				sPositionGLRotation, ParameterHandlerType.VEC4F,
				ECommandType.TAG_POS_GL_ROTATION.getDefault());
		}

		cameraOrigin = parameterHandler.getValueVec3f(ECommandType.TAG_POS_GL_ORIGIN.getXmlKey());

		/* convert Vec4f to roation Rotf */
		Vec4f vec4fRotation = parameterHandler.getValueVec4f(ECommandType.TAG_POS_GL_ROTATION.getXmlKey());

		cameraRotation.set(new Vec3f(vec4fRotation.x(), vec4fRotation.y(), vec4fRotation.z()),
			(float) Math.toRadians(vec4fRotation.w()));

		StringTokenizer frustumToken =
			new StringTokenizer(attrib3, GeneralManager.sDelimiter_Parser_DataItems);

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

	/**
	 * Set attributes for view. Main information is the projection mode, the frustum, and the parent canvas ID
	 * 
	 * @param eProjectionMode
	 * @param fLeft
	 * @param fRight
	 * @param fBottom
	 * @param fTop
	 * @param fNear
	 * @param fFar
	 * @param iParentCanvasID
	 */
	public void setAttributes(final EProjectionMode eProjectionMode, final float fLeft, final float fRight,
		final float fBottom, final float fTop, final float fNear, final float fFar, final int iParentCanvasID) {
		viewFrustum = new ViewFrustum(eProjectionMode, fLeft, fRight, fBottom, fTop, fNear, fFar);

		this.parentCanvasID = iParentCanvasID;
	}

	/**
	 * Set attributes of the view, including camera parameters.
	 * 
	 * @param eProjectionMode
	 * @param fLeft
	 * @param fRight
	 * @param fBottom
	 * @param fTop
	 * @param fNear
	 * @param fFar
	 * @param iParentCanvasID
	 * @param fCamOriginX
	 * @param fCamOriginY
	 * @param fCamOriginZ
	 * @param fCamRotationX
	 * @param fCamRotationY
	 * @param fCamRotationZ
	 * @param fCamRotationAngle
	 */
	public void setAttributes(final EProjectionMode eProjectionMode, final float fLeft, final float fRight,
		final float fBottom, final float fTop, final float fNear, final float fFar, final int parentCanvasID,
		final float fCamOriginX, final float fCamOriginY, final float fCamOriginZ, final float fCamRotationX,
		final float fCamRotationY, final float fCamRotationZ, final float fCamRotationAngle) {

		setAttributes(eProjectionMode, fLeft, fRight, fBottom, fTop, fNear, fFar, parentCanvasID);

		cameraOrigin.set(fCamOriginX, fCamOriginY, fCamOriginZ);
		cameraRotation.set(new Vec3f(fCamRotationX, fCamRotationY, fCamRotationZ),
			(float) Math.toRadians(fCamRotationAngle));
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
		dataDomainType = serView.getDataDomainType();
	}

	public void setParentCanvasID(int parentCanvasID) {
		this.parentCanvasID = parentCanvasID;
	}

	public void setViewID(String viewID) {
		this.viewID = viewID;
	}

	public void setViewFrustum(ViewFrustum viewFrustum) {
		this.viewFrustum = viewFrustum;
	}

	public void setDataDomainType(String dataDomainType) {
		this.dataDomainType = dataDomainType;
	}

	@Override
	public void doCommand() {

		ViewManager glCanvasManager = generalManager.getViewGLCanvasManager();

		GLCaleydoCanvas glCanvas = generalManager.getViewGLCanvasManager().getCanvas(parentCanvasID);

		createdObject = glCanvasManager.createGLView(viewID, glCanvas, label, viewFrustum);

		if (externalID != -1) {
			generalManager.getIDManager().mapInternalToExternalID(createdObject.getID(), externalID);
		}

		createdObject.getViewCamera().setCameraPosition(cameraOrigin);
		createdObject.getViewCamera().setCameraRotation(cameraRotation);

		if (createdObject instanceof IDataDomainBasedView<?>) {
			if (dataDomainType == null)
				throw new IllegalStateException(
					"No dataDomainType was set in CmdCreateView, while trying to create " + viewID);
			IDataDomain dataDomain = DataDomainManager.getInstance().getDataDomain(dataDomainType);
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView =
				(IDataDomainBasedView<IDataDomain>) createdObject;
			dataDomainBasedView.setDataDomain(dataDomain);
		}
		
		createdObject.initialize();
	}

	@Override
	public void undoCommand() {
	}

}
