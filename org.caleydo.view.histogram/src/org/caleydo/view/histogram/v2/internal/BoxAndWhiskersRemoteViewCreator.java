/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.ARemoteGLElementCreator;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext.Builder;
import org.caleydo.view.histogram.v2.BoxAndWhiskersElement;

/**
 * factory class for {@link BoxAndWhiskersElement}
 *
 * @author Samuel Gratzl
 *
 */
public class BoxAndWhiskersRemoteViewCreator extends ARemoteGLElementCreator {

	public BoxAndWhiskersRemoteViewCreator() {
	}

	/**
	 * @param tablePerspectives
	 * @return
	 */
	@Override
	protected GLElement create(List<TablePerspective> tablePerspectives) {
		GLElementFactoryContext context = createContext(tablePerspectives);

		BoxAndWhiskersElementFactory f = new BoxAndWhiskersElementFactory();
		if (!f.canCreate(context))
			return new GLElement();
		return f.create(context);
	}


	private static GLElementFactoryContext createContext(List<TablePerspective> tablePerspectives) {
		Builder b = GLElementFactoryContext.builder();
		b.withData(tablePerspectives).put(EDetailLevel.class, EDetailLevel.HIGH);
		b.put("splitGroups", EDimension.RECORD);
		b.set("showScale");
		GLElementFactoryContext context = b.build();
		return context;
	}

}
