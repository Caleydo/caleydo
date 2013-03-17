/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package university.mixed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;

import university.arwu.AcademicUniversityYear;
import university.top100under50.Top100Under50Year;
import university.top100under50.Top100Under50Year.Row;
import university.wur.WorldUniversityYear;

import com.google.common.base.Function;

import demo.RankTableDemo;
import demo.RankTableDemo.IModelBuilder;

/**
 * @author Samuel Gratzl
 *
 */
public class Mixed implements IModelBuilder {
	@Override
	public void apply(RankTableModel table) throws Exception {
		Map<String, String> metaData = new HashMap<>();
		{
			Set<String> all = new HashSet<>();
			//Map<String, Row> top100under50 = Top100Under50Year.readData(2012);
			//all.addAll(top100under50.keySet());
			Map<String, WorldUniversityYear[]> wbu = WorldUniversityYear.readData(2012);
			all.addAll(wbu.keySet());
			Map<String, Pair<String, AcademicUniversityYear[]>> arwu = AcademicUniversityYear.readData(2012);
			all.addAll(arwu.keySet());
			
			List<UniversityRow> rows = new ArrayList<>(all.size());
			for (String university : all) {
				UniversityRow r = new UniversityRow(university);
				if (wbu.containsKey(university))
					r.wbu = wbu.get(university)[0];
				if (arwu.containsKey(university)) {
					r.arwu = arwu.get(university).getSecond()[0];
					r.country = arwu.get(university).getFirst();
				}
//				if (top100under50.containsKey(university)) {
//					r.top100under50 = top100under50.get(university).years[0];
//					if (r.country == null)
//						r.country = top100under50.get(university).country;
//				}
				if (r.country != null)
					metaData.put(r.country, r.country);
				rows.add(r);
			}
			table.addData(rows);
		}

		table.add(new RankRankColumnModel());
		table.add(new StringRankColumnModel(GLRenderers.drawText("Institution", VAlign.CENTER),
				StringRankColumnModel.DEFAULT));
		table.add(new CategoricalRankColumnModel<>(GLRenderers.drawText("Institution", VAlign.CENTER),
				new Function<IRow, String>() {
					@Override
					public String apply(IRow in) {
						UniversityRow r = (UniversityRow) in;
						return r.country;
					}
				}, metaData));

		WorldUniversityYear.addYear(table, "World University Ranking", new Function<IRow, WorldUniversityYear>() {
					@Override
			public WorldUniversityYear apply(IRow in) {
						UniversityRow r = (UniversityRow) in;
						return r.wbu;
					}
				});

		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		AcademicUniversityYear.addYear(table, "Academic Ranking of World Universities",
				new Function<IRow, AcademicUniversityYear>() {
					@Override
					public AcademicUniversityYear apply(IRow in) {
						UniversityRow r = (UniversityRow) in;
						return r.arwu;
					}
				});

//		table.add(new OrderColumn());
//		table.add(new RankRankColumnModel());
//
//		Top100Under50Year.addYear(table, "Top 100 Under 50", new Function<IRow, Top100Under50Year>() {
//			@Override
//			public Top100Under50Year apply(IRow in) {
//				UniversityRow r = (UniversityRow) in;
//				return r.top100under50;
//			}
//		});
	}

	static class UniversityRow extends ARow {

		public String schoolname;
		public String country;

		//public Top100Under50Year top100under50;
		public WorldUniversityYear wbu;
		public AcademicUniversityYear arwu;

		public UniversityRow(String school) {
			this.schoolname = school;
		}

		@Override
		public String toString() {
			return schoolname;
		}
	}

	public static void main(String[] args) {
		GLSandBox.main(args, RankTableDemo.class, "University Rankings 2012",
				new Mixed());
	}
}
