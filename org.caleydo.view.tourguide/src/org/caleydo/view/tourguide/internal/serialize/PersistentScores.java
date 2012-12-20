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
package org.caleydo.view.tourguide.internal.serialize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.view.tourguide.api.score.ISerializeableScore;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PersistentScores implements Iterable<ISerializeableScore> {
	@XmlElementWrapper
	@XmlAnyElement
	private Collection<ISerializeableScore> scores = new ArrayList<>();

	public PersistentScores() {

	}

	public PersistentScores(Iterable<ISerializeableScore> scores) {
		this.scores = Lists.newArrayList(scores);
	}

	public Collection<ISerializeableScore> getScores() {
		return scores;
	}

	@Override
	public Iterator<ISerializeableScore> iterator() {
		return scores == null ? Collections.<ISerializeableScore> emptyIterator() : scores.iterator();
	}

	public void add(ISerializeableScore score) {
		this.scores.add(score);
	}
}
