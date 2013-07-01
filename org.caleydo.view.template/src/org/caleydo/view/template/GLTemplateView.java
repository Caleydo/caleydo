/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.template;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementDecorator;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElementView;

/**
 * basic view based on {@link GLElement} with a {@link ASingleTablePerspectiveElementView}
 * 
 * @author Samuel Gratzl
 * 
 */
public class GLTemplateView extends ASingleTablePerspectiveElementView {
	public static final String VIEW_TYPE = "org.caleydo.view.template";
	public static final String VIEW_NAME = "View Template";

	public GLTemplateView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedTemplateView serializedForm = new SerializedTemplateView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	protected void applyTablePerspective(AGLElementDecorator root, TablePerspective tablePerspective) {
		if (tablePerspective == null)
			root.setContent(null);
		else
			root.setContent(new TemplateElement(tablePerspective));
	}


}
