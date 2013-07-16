/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package demo;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.DateRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;

import com.google.common.base.Function;

import demo.RankTableDemo.IModelBuilder;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleDateTest implements IModelBuilder {
	@Override
	public void apply(RankTableModel table) throws Exception {
		table.add(new RankRankColumnModel());

		table.add(new DateRankColumnModel(GLRenderers.drawText("Date"), new Function<IRow, Date>() {
					@Override
			public Date apply(IRow in) {
						return ((SimpleRow) in).value;
					}
		}));

		long offset = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
		long end = Timestamp.valueOf("2018-01-01 00:00:00").getTime();
		long diff = end - offset + 1;
		List<IRow> rows = new ArrayList<>(20);
		for (int i = 0; i < 20; ++i)
			rows.add(new SimpleRow(new Timestamp(offset + (long) (Math.random() * diff))));
		table.addData(rows);
	}

	@Override
	public Iterable<? extends ARankColumnModel> createAutoSnapshotColumns(RankTableModel table, ARankColumnModel model) {
		return Collections.singleton(new RankRankColumnModel());
	}

	public static Field field(String f) throws NoSuchFieldException {
		return SimpleRow.class.getDeclaredField(f);
	}

	static class SimpleRow extends ARow {
		private final Date value;

		public SimpleRow(Date value) {
			this.value = value;
		}
	}

	public static void main(String[] args) {
		GLSandBox.main(args, RankTableDemo.class, "SimpelDateTest", new SimpleDateTest());
	}
}
