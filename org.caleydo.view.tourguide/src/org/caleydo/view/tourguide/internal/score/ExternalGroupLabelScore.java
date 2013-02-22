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
package org.caleydo.view.tourguide.internal.score;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.view.tourguide.internal.external.GroupLabelParseSpecification;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;
import org.caleydo.view.tourguide.v3.model.IRow;

import com.google.common.base.Objects;

/**
 * external score which contains a score per group of a stratification, identified by its label
 *
 * @author Samuel Gratzl
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ExternalGroupLabelScore extends AExternalScore implements ISerializeableScore {
	private String perspectiveKey;
	private Map<String, Float> scores = new HashMap<>();

	public ExternalGroupLabelScore() {
		super();
	}

	public ExternalGroupLabelScore(String label, GroupLabelParseSpecification spec, Map<String, Float> scores) {
		super(label, spec);
		this.perspectiveKey = spec.getPerspectiveKey();
		this.scores.putAll(scores);
	}
	
	@Override
	public float applyPrimitive(IRow eleme) {
		PerspectiveRow elem = (PerspectiveRow) eleme;
		if (elem.getGroup() == null)
			return Float.NaN;

		TablePerspective strat = elem.getPerspective();

		if (!perspectiveKey.equals(strat.getDimensionPerspective().getPerspectiveID())
				&& !perspectiveKey.equals(strat.getRecordPerspective().getPerspectiveID()))
			return Float.NaN;

		Float v = this.scores.get(elem.getGroup().getLabel());
		if (v == null)
			return Float.NaN;
		return v.floatValue();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getLabel(), perspectiveKey);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalGroupLabelScore other = (ExternalGroupLabelScore) obj;
		if (Objects.equal(perspectiveKey, other.perspectiveKey))
			return false;
		if (Objects.equal(getLabel(), other.getLabel()))
			return false;
		return true;
	}
}
