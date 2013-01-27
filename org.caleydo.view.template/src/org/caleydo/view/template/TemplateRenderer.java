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
package org.caleydo.view.template;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
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
import org.caleydo.core.event.EventListeners;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.template.renderstyle.TemplateRenderStyle;

/**
 * @author Alexander Lex
 *
 */
public class TemplateRenderer extends ALayoutRenderer implements ISingleTablePerspectiveBasedView, IListenerOwner,
		IRecordVAUpdateHandler, IDimensionVAUpdateHandler {

	private TemplateRenderStyle renderStyle;

	private EventListeners listeners = new EventListeners();

	private EventBasedSelectionManager recordSelectionManager;

	private ATableBasedDataDomain dataDomain;

	private TablePerspective tablePerspective;

	private AGLView view;

	/**
	 *
	 */
	public TemplateRenderer(AGLView view) {
		this.view = view;
		initialize();
	}

	@Override
	protected void renderContent(GL2 gl) {

		// An id you use to identify which object to render
		int objectID = 1;
		// this is how you get the picking id
		int pickingID = view.getPickingManager().getPickingID(view.getID(), EPickingType.TEMPLATE.name(), objectID);

		// here you push the id to OpenGL's picking system
		gl.glPushName(pickingID);

		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(1, 0, 0);

		gl.glVertex3f(0.2f, 0.2f, 0);
		gl.glVertex3f(0.2f, 0.4f, 0);
		gl.glVertex3f(0.4f, 0.4f, 0);
		gl.glVertex3f(0.4f, 0.2f, 0);
		gl.glEnd();

		gl.glPopName();

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
		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				System.out.println("Registered pick");
			}
		}, EPickingType.TEMPLATE.name());

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

}
