/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
