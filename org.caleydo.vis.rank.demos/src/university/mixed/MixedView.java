/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package university.mixed;

import org.caleydo.core.gui.command.AOpenViewHandler;

import demo.ARcpRankTableDemoView;
import demo.RankTableDemo.IModelBuilder;

/**
 * @author Samuel Gratzl
 *
 */
public class MixedView extends ARcpRankTableDemoView {
	private static final String ID = "rankvis.demo.university.wur";
	@Override
	public IModelBuilder createModel() {
		return new Mixed();
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

	public static class Handler extends AOpenViewHandler {
		public Handler() {
			super(ID);
		}
	}


}
