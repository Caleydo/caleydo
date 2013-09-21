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
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.w3c.dom.Node;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PersistentScores implements Iterable<ISerializeableScore> {
	private static final Logger log = Logger.create(PersistentScores.class);

	@XmlElementWrapper
	@XmlAnyElement
	private List<ISerializeableScore> scores = new ArrayList<>();

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

	/**
	 * @param unmarshaller
	 */
	public void map(Unmarshaller unmarshaller) {
		for (ListIterator<ISerializeableScore> it = scores.listIterator(); it.hasNext();) {
			Object r = it.next();
			if (r instanceof Node) {
				try {
					r = unmarshaller.unmarshal((Node) r);
				} catch (JAXBException e) {
					log.error("can't convert external score: " + r, e);
					r = null;
				}
			}
			if (r instanceof ISerializeableScore)
				it.set((ISerializeableScore) r);
			else
				it.remove();
		}
	}
}
