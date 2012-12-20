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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.serialize.ISerializationAddon;
import org.caleydo.core.serialize.SerializationData;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.view.tourguide.internal.score.ExternalIDTypeScore;
import org.caleydo.view.tourguide.internal.score.Scores;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class TourGuideSerializationAddon implements ISerializationAddon {
	private static final String ADDON_KEY = "tourguide";
	private static final String PERSISTENT_SCORES_XML = "persistent_scores.xml";
	private static final Logger log = Logger.create(TourGuideSerializationAddon.class);

	@Override
	public Collection<Class<?>> getJAXBContextClasses() {
		return Arrays.asList(ExternalIDTypeScore.class, PersistentScores.class);
	}

	@Override
	public void deserialize(String dirName, Unmarshaller unmarshaller, SerializationData data) {
		File f = new File(dirName, PERSISTENT_SCORES_XML);
		if (!f.exists())
			return;
		try {
			PersistentScores scores = (PersistentScores) unmarshaller.unmarshal(f);
			data.setAddonData(ADDON_KEY, scores);
		} catch (JAXBException e) {
			log.error("can't deserialize", e);
		}
	}

	@Override
	public void serialize(Collection<? extends IDataDomain> toSave, Marshaller marshaller, String dirName) {
		Iterable<ISerializeableScore> toPersist = Scores.get().getPersistentScores();
		if (Iterables.isEmpty(toPersist))
			return;
		File f = new File(dirName, PERSISTENT_SCORES_XML);
		PersistentScores scores = new PersistentScores(toPersist);
		try {
			marshaller.marshal(scores, f);
		} catch (JAXBException e) {
			log.error("can't serialize", e);
		}
	}

	@Override
	public void load(SerializationData data) {
		PersistentScores scores = (PersistentScores) data.getAddonData(ADDON_KEY);
		if (scores == null)
			return;
		for (ISerializeableScore s : scores)
			Scores.get().addPersistentScoreIfAbsent(s);
	}

}
