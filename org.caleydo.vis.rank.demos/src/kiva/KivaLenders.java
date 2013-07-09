/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package kiva;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import demo.RankTableDemo;
import demo.RankTableDemo.IModelBuilder;
import demo.ReflectionData;
import demo.ReflectionFloatData;

/**
 * @author Alexander Lex
 *
 *         File Schema:
 *
 *         <code>
lenders:uid: [String]
lenders:lender_id: [String]
lenders:name: [String]
lenders:image:id: [Integer]
lenders:image:template_id: [Integer]
lenders:member_since: [Date]
lenders:whereabouts: [String]
lenders:country_code: [String]
lenders:personal_url: [String]
lenders:occupation: [String]
lenders:occupational_info: [String]
lenders:inviter_id: [String]
lenders:invitee_count: [Integer]
lenders:loan_count: [Integer]
lenders:loan_because: [String]</code>
 *
 */
public class KivaLenders implements IModelBuilder {

	private static final int CLIPPING_SIZE = 500000;

	@Override
	public void apply(RankTableModel table) throws Exception {
		List<Lenders> rows = readData();
		table.addData(rows);

		table.add(new RankRankColumnModel());

		Map<String, String> name = new LinkedHashMap<>((int) (CLIPPING_SIZE * 1.7));
		Map<String, String> countryCode = new LinkedHashMap<>((int) (CLIPPING_SIZE * 1.7));
		Map<String, String> memberSince = new LinkedHashMap<>((int) (CLIPPING_SIZE * 1.7));
		for (Lenders row : rows) {
			name.put(row.uid, row.uid);
			countryCode.put(row.countryCode, row.countryCode);
			memberSince.put(row.memberSince, row.memberSince);
		}

		table.add(new CategoricalRankColumnModel<String>(GLRenderers.drawText("Username", VAlign.CENTER),
				new ReflectionData<>(field("uid"), String.class), name));

		table.add(new CategoricalRankColumnModel<String>(GLRenderers.drawText("Member Since", VAlign.CENTER),
				new ReflectionData<>(field("memberSince"), String.class), memberSince));

		table.add(new CategoricalRankColumnModel<String>(GLRenderers.drawText("Country Code", VAlign.CENTER),
				new ReflectionData<>(field("countryCode"), String.class), countryCode));

		table.add(col("invitee_count", "Invitee Count", "#C994C7", "#E7E1EF"));
		table.add(col("loan_count", "Loan Count", "#FDBB84", "#FEE8C8"));

	}

	@Override
	public Iterable<? extends ARankColumnModel> createAutoSnapshotColumns(RankTableModel table, ARankColumnModel model) {
		Collection<ARankColumnModel> ms = new ArrayList<>(2);
		ms.add(new RankRankColumnModel());
		ARankColumnModel desc = find(table, "Task");
		if (desc != null)
			ms.add(desc.clone().setCollapsed(true));
		return ms;
	}

	private static ARankColumnModel find(RankTableModel table, String name) {
		for (ARankColumnModel model : table.getColumns()) {
			if (model.getTitle().equals(name))
				return model;
		}
		return null;
	}

	protected static List<Lenders> readData() {

		String source = "/home/media/xdata/SummerCamp/Kiva/lenders.csv";

		List<Lenders> rows = new ArrayList<>();
		try (BufferedReader r = GeneralManager.get().getResourceLoader().getResource(source);) {
			String line;
			r.readLine();

			while ((line = r.readLine()) != null) {

				String[] l = line.split("\t");

				int inviteeCount = Integer.parseInt(l[12]);
				int loanCount = Integer.parseInt(l[13]);
				if (inviteeCount < 5 && loanCount < 5)
					continue;

				Lenders row = new Lenders();

				row.uid = l[0];
				row.countryCode = l[6];// countryCode;
				row.memberSince = l[5].split("-")[0];
				row.invitee_count = inviteeCount;
				row.loan_count = loanCount;
				rows.add(row);

			}

		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, "KIVA loader", "Failed to load dataset " + source, e));
		}
		return rows;
	}

	private static Field field(String name) throws NoSuchFieldException, SecurityException {
		return Lenders.class.getDeclaredField(name);
	}

	private FloatRankColumnModel col(String field, String label, String color, String bgColor)
			throws NoSuchFieldException, SecurityException {
		ReflectionFloatData data = new ReflectionFloatData(field(field));
		FloatRankColumnModel f = new FloatRankColumnModel(data, GLRenderers.drawText(label, VAlign.CENTER), new Color(
				color), new Color(bgColor), mapping(), FloatInferrers.MEDIAN);
		f.setWidth(150);
		return f;
	}

	protected PiecewiseMapping mapping() {
		PiecewiseMapping m = new PiecewiseMapping(0, 100);
		return m;
	}

	static class Lenders extends ARow {
		public String uid;
		// public String lenderID;
		// public String name;
		public String countryCode;
		public String memberSince;
		public float invitee_count;
		public float loan_count;


		@Override
		public String toString() {
			return uid;
		}
	}

	public static void main(String[] args) {
		// dump();
		GLSandBox.main(args, RankTableDemo.class, "Kiva Lenders", new KivaLenders());
	}
}
