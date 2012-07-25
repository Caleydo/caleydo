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
package org.caleydo.core.io;

import javax.xml.bind.annotation.XmlElement;
import org.caleydo.core.id.IDType;

/**
 * Parsing rules based on regular expressions for converting strings to ids of a
 * specific {@link IDType}. There are two modifications possible for incoming id
 * types:
 * <ul>
 * <li>replacing (parts) of a string that matches the
 * {@link #replacingExpression} by the {@link #replacementString}</li>
 * <li>defining a substring of the incoming string - see
 * {@link #subStringExpression}</li>
 * </ul>
 * 
 * @author Alexander Lex
 * 
 */
public class IDTypeParsingRules {

	/**
	 * <p>
	 * Regular expression specifying a string that is to be replaced with
	 * {@link #replacementString} before storing and resolving the id.
	 * {@link String#replaceAll(String, String)} is used to achieve this.
	 * </p>
	 * <p>
	 * This is executed <b>before</b> a possible operation based on
	 * {@link #subStringExpression}.
	 * </p>
	 * <p>
	 * See http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
	 * for syntax.
	 * </p>
	 * <p>
	 * Defaults to null.
	 * </p>
	 */
	@XmlElement
	private String replacingExpression = null;

	/**
	 * <p>
	 * The string which replaces the string matched by the regular expression in
	 * {@link #replacingExpression}.
	 * </p>
	 * <p>
	 * Defaults to null.
	 * </p>
	 */
	@XmlElement
	private String replacementString = null;

	/**
	 * <p>
	 * Regular expression specifying a string that is used to split the input
	 * string. The {@link String#split(String)} operation is used to execute
	 * this operation. The contract for the resulting String[] is that the first
	 * not-empty string in the array is the desired substring. So if, for
	 * example a leading string "abc-" is to be removed from a string "abc-001",
	 * the expression must match to "abc-" so that the split operation results
	 * in ["","001"]. Using, for example, "\\-" as expression wuld result in
	 * ["abc","001"] and "abc" would be used as the result.
	 * </p>
	 * <p>
	 * Trailing strings can be removed in this manner.
	 * </p>
	 * <p>
	 * This is executed <b>after</b> a possible operation based on
	 * {@link #replacingExpression}.
	 * </p>
	 * <p>
	 * See http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
	 * for syntax.
	 * </p>
	 * <p>
	 * Defaults to null.
	 * </p>
	 */
	private String subStringExpression = null;

	/**
	 * Set a replacement expression. Sets {@link #replacingExpression} and
	 * {@link #replacementString}
	 */
	public void setReplacementExpression(String replacingExpression,
			String replacementString) {
		this.replacingExpression = replacingExpression;
		this.replacementString = replacementString;
	}

	/**
	 * @return the replacingExpression, see {@link #replacingExpression}
	 */
	public String getReplacingExpression() {
		return replacingExpression;
	}

	/**
	 * @return the replacementString, see {@link #replacementString}
	 */
	public String getReplacementString() {
		return replacementString;
	}

	/**
	 * @param subStringExpression
	 *            setter, see {@link #subStringExpression}
	 */
	public void setSubStringExpression(String subStringExpression) {
		this.subStringExpression = subStringExpression;
	}

	/**
	 * @return the subStringExpression, see {@link #subStringExpression}
	 */
	public String getSubStringExpression() {
		return subStringExpression;
	}

}
