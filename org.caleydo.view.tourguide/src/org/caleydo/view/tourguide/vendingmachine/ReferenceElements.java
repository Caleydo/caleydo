package org.caleydo.view.tourguide.vendingmachine;

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createColor;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createLabel;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;

public class ReferenceElements {
	public static final int DATADOMAIN_TYPE_WIDTH = 16;
	public static final int STRATIFACTION_WIDTH = 120;
	public static final int GROUP_WIDTH = 80;

	public static ElementLayout create(TablePerspective stratification, Group group, AGLView view) {
		return create(stratification.getDataDomain().getColor(), stratification.getRecordPerspective(), group, view);
	}

	public static ElementLayout create(IColor color, ILabelProvider stratification, ILabelProvider group, AGLView view) {
		Row elem = new Row();
		elem.setGrabY(true);
		elem.setXDynamic(true);
		elem.add(createColor(color, DATADOMAIN_TYPE_WIDTH));
		elem.add(createXSpacer(3));
		if (group != null) {
			elem.add(createLabel(view, stratification, STRATIFACTION_WIDTH));
			elem.add(createXSpacer(3));
			elem.add(createLabel(view, group, GROUP_WIDTH));
		} else {
			elem.add(createLabel(view, stratification, STRATIFACTION_WIDTH + GROUP_WIDTH));
		}
		return elem;
	}
}