/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package university.wur;

import org.caleydo.core.gui.command.AOpenViewHandler;

import demo.ARcpRankTableDemoView;
import demo.RankTableDemo.IModelBuilder;

/**
 * @author Samuel Gratzl
 *
 */
public class WorldUniversityRankingView extends ARcpRankTableDemoView {
	private static final String ID = "rankvis.demo.university.wur";
	@Override
	public IModelBuilder createModel() {
		return new WorldUniversityRanking();
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
