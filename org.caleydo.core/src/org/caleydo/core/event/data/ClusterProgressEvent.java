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
package org.caleydo.core.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

@XmlRootElement
@XmlType
public class ClusterProgressEvent
	extends AEvent {

	private int percentCompleted = -1;
	private boolean forSimilaritiesBar;

	public ClusterProgressEvent() {
		// nothing to initialize here
	}

	public ClusterProgressEvent(int percentCompleted, boolean forSimilaritiesBar) {
		Logger.log(new Status(IStatus.INFO, "Clustering", "Complete: " + percentCompleted));
		this.percentCompleted = percentCompleted;
		this.forSimilaritiesBar = forSimilaritiesBar;
	}

	public boolean isForSimilaritiesBar() {
		return forSimilaritiesBar;
	}

	public int getPercentCompleted() {
		return percentCompleted;
	}

	@Override
	public boolean checkIntegrity() {
		if (percentCompleted < 0 || percentCompleted > 100)
			throw new IllegalStateException("percentCompleted was outside of the valid range (0 - 100): "
				+ percentCompleted);

		return true;
	}

	public void setPercentCompleted(int percentCompleted) {
		this.percentCompleted = percentCompleted;
	}

	public void setForSimilaritiesBar(boolean forSimilaritiesBar) {
		this.forSimilaritiesBar = forSimilaritiesBar;
	}

}
