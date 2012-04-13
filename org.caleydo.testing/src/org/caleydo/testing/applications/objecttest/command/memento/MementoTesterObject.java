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
package org.caleydo.testing.applications.objecttest.command.memento;

import org.caleydo.core.command.memento.IMemento;
import org.caleydo.core.command.memento.IMementoCreator;

/**
 * Class for testing IMemento.
 * 
 * @author Michael Kalkusch
 */
public final class MementoTesterObject
{

	private IMementoCreator creator;

	private int iTestRuns;

	/**
	 * Constructor.
	 * 
	 * @param iTestRuns
	 */
	public MementoTesterObject(final int iTestRuns)
	{
		this.iTestRuns = iTestRuns;
	}

	public void setMementoCreator(IMementoCreator creatorObject, final int iTestMementos)
	{
		creator = creatorObject;
		if (iTestRuns > 0)
		{
			iTestRuns = iTestMementos;
		}
	}

	public boolean testGetSetMemento()
	{

		IMemento[] testMementos = new IMemento[iTestRuns];

		for (int i = 0; i < iTestRuns; i++)
		{
			testMementos[i] = creator.createMemento();
		}

		for (int i = (iTestRuns - 1); i > -1; i--)
		{
			creator.setMemento(testMementos[i]);
		}

		return true;
	}
}
