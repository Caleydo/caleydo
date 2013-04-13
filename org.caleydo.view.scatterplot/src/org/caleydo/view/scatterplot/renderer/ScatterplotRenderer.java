/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.scatterplot.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateListener;
import org.caleydo.core.data.virtualarray.events.IDimensionVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.IRecordVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateListener;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ElementLayouts;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.scatterplot.EPickingType;
import org.caleydo.view.scatterplot.renderstyle.ScatterplotRenderStyle;

/**
 * @author Alexander Lex
 *
 */
public class ScatterplotRenderer extends ALayoutRenderer implements ISingleTablePerspectiveBasedView, IListenerOwner,
		IRecordVAUpdateHandler, IDimensionVAUpdateHandler {

	private ScatterplotRenderStyle renderStyle;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	private EventBasedSelectionManager recordSelectionManager;

	private ATableBasedDataDomain dataDomain;

	private TablePerspective tablePerspective;

	private Row baseRow;

	private AGLView view;

	private ElementLayout el1;
	private ElementLayout el2;
	private ElementLayout el3;
	private ElementLayout spacing;

	/**
	 *
	 */
	public ScatterplotRenderer(AGLView view, Row baseRow) {
		this.view = view;
		this.baseRow = baseRow;
//		el1 = ElementLayouts.createButton(view, new Button(EPickingType.TEMPLATE.name(), 1, EIconTextures.ARROW_UP));
//		baseRow.add(el1);
//
//		spacing = ElementLayouts.createXSpacer(100);
//		baseRow.add(spacing);
//
//		el2 = ElementLayouts.createButton(view, new Button("TATA", 1, EIconTextures.BROWSER_HOME_IMAGE));
//
//		baseRow.add(el2);

		initialize();
	}

	@Override
	protected void renderContent(GL2 gl) {

		// An id you use to identify which object to render
		int objectID = 1;
		// this is how you get the picking id
		//int pickingID = view.getPickingManager().getPickingID(view.getID(), EPickingType.TEMPLATE.name(), objectID);

		// here you push the id to OpenGL's picking system
		// gl.glPushName(pickingID);
		//
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glColor3f(1, 0, 0);
		//
		// gl.glVertex3f(0.2f, 0.2f, 0);
		// gl.glVertex3f(0.2f, 0.4f, 0);
		// gl.glVertex3f(0.4f, 0.4f, 0);
		// gl.glVertex3f(0.4f, 0.2f, 0);
		// gl.glEnd();
		//
		// gl.glPopName();

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// this is how you handle picking
//		view.addTypePickingListener(new APickingListener() {
//			@Override
//			public void clicked(Pick pick) {
//				layoutManager.recordPreTransitionState();
//				baseRow.clear();
//				baseRow.add(el2);
//				baseRow.add(spacing);
//				el1.setPixelSizeX(40);
//				el1.setPixelSizeY(40);
//				baseRow.add(el1);
//				layoutManager.updateLayout();
//				layoutManager.recordPostTranslationState();
//
//				System.out.println("Registered pick");
//			}
//		}, EPickingType.TEMPLATE.name());
//
//		view.addTypePickingListener(new APickingListener() {
//			@Override
//			public void clicked(Pick pick) {
//				layoutManager.recordPreTransitionState();
//				baseRow.clear();
//
//				el3 = ElementLayouts.createButton(view, new Button("TATA", 1, EIconTextures.BROWSER_REFRESH_IMAGE));
//				el3.setPixelSizeX(50);
//				el3.setPixelSizeY(50);
//
//				el1.setPixelSizeX(16);
//				el1.setPixelSizeY(16);
//				baseRow.add(el1);
//				baseRow.add(spacing);
//				baseRow.add(el2);
//				baseRow.add(spacing);
//				baseRow.add(el3);
//
//				layoutManager.updateLayout();
//				layoutManager.recordPostTranslationState();
//
//				System.out.println("Registered pick");
//			}
//		}, "TATA");

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView serializedView) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelDefault() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLabel(String label, boolean isLabelDefault) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLabel(String label) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProviderName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		// TODO Auto-generated method stub

	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void handleDimensionVAUpdate(String dimensionPerspectiveID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRecordVAUpdate(String perspectiveID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerEventListeners() {
		RecordVAUpdateListener recordVAUpdateListener = new RecordVAUpdateListener();
		recordVAUpdateListener.setHandler(this);
		listeners.register(RecordVAUpdateEvent.class, recordVAUpdateListener);

		DimensionVAUpdateListener dimensionVAUpdateListener = new DimensionVAUpdateListener();
		dimensionVAUpdateListener.setHandler(this);
		listeners.register(DimensionVAUpdateEvent.class, dimensionVAUpdateListener);

	}

	@Override
	public void unregisterEventListeners() {
		listeners.unregisterAll();

	}

	@Override
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	@Override
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		List<TablePerspective> tablePerspectives = new ArrayList<>(1);
		tablePerspectives.add(tablePerspective);
		return tablePerspectives;
	}

	@Override
	public String getViewType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInstanceNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setInstanceNumber(int instanceNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDataView() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<IDataDomain> getDataDomains() {
		// TODO Auto-generated method stub
		return null;
	}

}
