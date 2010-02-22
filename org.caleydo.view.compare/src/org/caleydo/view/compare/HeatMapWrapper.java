package org.caleydo.view.compare;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.heatmap.GLHeatMap;

import com.sun.opengl.util.texture.Texture;

public class HeatMapWrapper {

	private GLHeatMap heatMap;
	private ISet set;
	private IGeneralManager generalManager;
	private ContentVirtualArray contentVA;
	private StorageVirtualArray storageVA;


	// private Vec3f position;
	// private float width;
	// private float height;

	public HeatMapWrapper() {
		generalManager = GeneralManager.get();

	}

	private void createHeatMap(IUseCase useCase,
			IGLRemoteRenderingView parentView, EDataDomain dataDomain) {

		CmdCreateView cmdView = (CmdCreateView) generalManager
				.getCommandManager().createCommandByType(
						ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(GLHeatMap.VIEW_ID);

		cmdView.setAttributes(dataDomain, EProjectionMode.ORTHOGRAPHIC, 0, 50,
				0, 50, -20, 20, -1);

		cmdView.doCommand();

		heatMap = (GLHeatMap) cmdView.getCreatedObject();
		heatMap.setUseCase(useCase);
		heatMap.setRemoteRenderingGLView(parentView);

		heatMap.setDataDomain(dataDomain);
		heatMap.setContentVAType(ContentVAType.CONTENT_EMBEDDED_HM);
		heatMap.initData();
		heatMap.setDetailLevel(EDetailLevel.MEDIUM);

	}

	public void setSet(ISet set) {
		this.set = set;
		// FIXME: use va from set
		ISet useCaseSet = generalManager.getUseCase(EDataDomain.GENETIC_DATA)
				.getSet();
		contentVA = set.getContentVA(ContentVAType.CONTENT);
		storageVA = set.getStorageVA(StorageVAType.STORAGE);
		heatMap.setSet(set);

		setEmbeddedHeatMapData();
		heatMap.useFishEye(false);
		heatMap.setDisplayListDirty();
	}

	public void init(GL gl, AGLView glParentView,
			GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager,
			IUseCase useCase, IGLRemoteRenderingView parentView,
			EDataDomain dataDomain) {
		createHeatMap(useCase, parentView, dataDomain);
		heatMap.initRemote(gl, glParentView, glMouseListener, infoAreaManager);
		heatMap.useFishEye(false);
	}

	private void setEmbeddedHeatMapData() {

		// TODO: Is this really necessary?
		heatMap.resetView();
		ContentVADelta delta = new ContentVADelta(
				ContentVAType.CONTENT_EMBEDDED_HM, EIDType.EXPRESSION_INDEX);

		for (int i = 0; i < contentVA.size(); i++) {
			if (i >= contentVA.size())
				break;

			int contentIndex = contentVA.get(i);
			delta.add(VADeltaItem.append(contentIndex));
		}
//		for (int i = 10; i < contentVA.size(); i++) {
//			int contentIndex = contentVA.get(i);
//			delta.add(VADeltaItem.removeElement(contentIndex));
//		}

		heatMap.handleContentVAUpdate(delta, "");

	}

	public void draw(GL gl, float positionX, float positionY, float width,
			float height) {

		gl.glTranslatef(positionX, positionY, 0);
		heatMap.getViewFrustum().setLeft(positionX);
		heatMap.getViewFrustum().setBottom(positionY);
		heatMap.getViewFrustum().setRight(positionX + width);
		heatMap.getViewFrustum().setTop(positionY + height);
		heatMap.displayRemote(gl);
		gl.glTranslatef(-positionX, -positionY, 0);
	}

	public void processEvents() {
		heatMap.processEvents();
	}

	public void setDisplayListDirty() {
		heatMap.setDisplayListDirty();
	}

}
