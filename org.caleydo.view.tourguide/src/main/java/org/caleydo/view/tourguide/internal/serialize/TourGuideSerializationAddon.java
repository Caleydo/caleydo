/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
import org.caleydo.view.tourguide.api.score.Scores;
import org.caleydo.view.tourguide.internal.score.ExternalGroupLabelScore;
import org.caleydo.view.tourguide.internal.score.ExternalIDTypeScore;
import org.caleydo.view.tourguide.internal.score.ExternalLabelScore;

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
		return Arrays.asList(ExternalIDTypeScore.class, ExternalGroupLabelScore.class, ExternalLabelScore.class,
				PersistentScores.class);
	}

	@Override
	public void deserialize(String dirName, Unmarshaller unmarshaller) {

	}
	@Override
	public void deserialize(String dirName, Unmarshaller unmarshaller, SerializationData data) {
		File f = new File(dirName, PERSISTENT_SCORES_XML);
		if (!f.exists())
			return;
		try {
			PersistentScores scores = (PersistentScores) unmarshaller.unmarshal(f);
			scores.map(unmarshaller);
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
