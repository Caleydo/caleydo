/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io;

import javax.xml.bind.annotation.XmlElement;

import org.caleydo.core.id.IDType;

/**
 * Parsing rules based on regular expressions for converting strings to ids of a specific {@link IDType}. There are
 * three modifications possible for incoming id types, <b>and these operations are executed in this order</b>. Note that
 * the order has consequences on the process.
 * <ul>
 * <li>Converting a string to lowercase, if {@link #toLowerCase} is true.</li>
 * <li>replacing (parts) of a string that matches the {@link #replacingExpression} by the {@link #replacementString}</li>
 * <li>defining a substring of the incoming string - see {@link #subStringExpression}</li>
 * </ul>
 * <p>
 * There are two main ways that IDTypeParsingRules are used:
 * <ol>
 * <li><b>As part of an {@link IDSpecification}.</b> An IDSpecifcation is a specific rule usually associated with a
 * dataset for parsing. If a parsing rule is specified as part of an IDSpecification, it will be used in any case. If
 * {@link #isDefault} in the parsing rule is set to true, the parsing rule will also be stored as the default parsing
 * rule of the ID Type.</li>
 * <li><b>As part of an {@link IDType}.</b> If a parsing rule's {@link #isDefault} is true, it will be stored as the
 * default parsing rule for the <code>IDType</code>.<b>The default parsing rule will only be used if the parsing rule of
 * the <code>IDSpecification</code> is null.</b></li>
 * </ol>
 * </p>
 * <p>
 * Consequently an IDSpecification parsing rule overrides IDType parsing rules. If you want to use no parsing rule for
 * an IDType that has a default parsing rule you have to create an empty parsing rule for the IDSpecification.
 * </p>
 *
 * @author Alexander Lex
 *
 */
public class IDTypeParsingRules {

	/**
	 * Flag determining whether this parsing rule is a default rule for an {@link IDType}, i.e., if true it will be
	 * stored in {@link IDType#setIdTypeParsingRules(IDTypeParsingRules) and used if no dynamic parsing rule is applied.
	 */
	boolean isDefault = false;

	/**
	 * <p>
	 * Flag that determines whether all characters [A-Z] should be converted to their lower-case equivalent. This is
	 * done <b>before</b> all other operations. Defaults to false.
	 * </p>
	 * <p>
	 * Implementation note: {@link String#toLowerCase()} is used, so look at the details there especially for non-ascii
	 * characters.
	 * </p>
	 */
	boolean toLowerCase = false;

	/**
	 * Same as {@link #toLowerCase} but for upper case.
	 */
	boolean toUpperCase = false;

	/**
	 * <p>
	 * Regular expression specifying a (list of) strings that is to be replaced with {@link #replacementString} before
	 * storing and resolving the id. {@link String#replaceAll(String, String)} is used to achieve this.
	 * </p>
	 * <p>
	 * This is executed <b>after</b> a possible conversion to lower case ( {@link #toLowerCase}) and <b>before</b> a
	 * possible operation based on {@link #subStringExpression}.
	 * </p>
	 * <p>
	 * See http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html for syntax.
	 * </p>
	 * <p>
	 * Defaults to null.
	 * </p>
	 */
	@XmlElement
	private String[] replacingExpressions = null;

	/**
	 * <p>
	 * The string which replaces the string matched by the regular expression in {@link #replacingExpression}.
	 * </p>
	 * <p>
	 * Defaults to null.
	 * </p>
	 */
	@XmlElement
	private String replacementString = null;

	/**
	 * <p>
	 * Regular expression specifying a string that is used to split the input string. The {@link String#split(String)}
	 * operation is used to execute this operation. The contract for the resulting String[] is that the first not-empty
	 * string in the array is the desired substring. So if, for example a leading string "abc-" is to be removed from a
	 * string "abc-001", the expression must match to "abc-" so that the split operation results in ["","001"]. Using,
	 * for example, "\\-" as expression would result in ["abc","001"] and "abc" would be used as the result.
	 * </p>
	 * <p>
	 * Trailing strings can be removed in this manner.
	 * </p>
	 * <p>
	 * This is executed <b>after</b> a possible conversion to lower case ( {@link #toLowerCase}) and an operation based
	 * on {@link #replacingExpression}.
	 * </p>
	 * <p>
	 * See http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html for syntax.
	 * </p>
	 * <p>
	 * Defaults to null.
	 * </p>
	 */
	private String subStringExpression = null;

	/**
	 * @param isDefault
	 *            setter, see {@link #isDefault}
	 */
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 * @return the isDefault, see {@link #isDefault}
	 */
	public boolean isDefault() {
		return isDefault;
	}

	/**
	 * @param toLowerCase
	 *            setter, see {@link #toLowerCase}
	 */
	public void setToLowerCase(boolean toLowerCase) {
		if (toLowerCase == true && toUpperCase == true) {
			throw new IllegalArgumentException("Conflicting configuration for upper/lower case conversion.");
		}
		this.toLowerCase = toLowerCase;
	}

	/**
	 * @return the toLowerCase, see {@link #toLowerCase}
	 */
	public boolean isToLowerCase() {
		return toLowerCase;
	}

	/**
	 * @param toUpperCase
	 *            setter, see {@link toUpperCase}
	 */
	public void setToUpperCase(boolean toUpperCase) {
		if (toLowerCase == true && toUpperCase == true) {
			throw new IllegalArgumentException("Conflicting configuration for upper/lower case conversion.");
		}
		this.toUpperCase = toUpperCase;
	}

	/**
	 * @return the toUpperCase, see {@link #toUpperCase}
	 */
	public boolean isToUpperCase() {
		return toUpperCase;
	}

	/**
	 * Set a replacement expression. Sets {@link #replacingExpression} and {@link #replacementString}
	 */
	public void setReplacementExpression(String replacementString, String... replacingExpressions) {
		this.replacingExpressions = replacingExpressions;
		this.replacementString = replacementString;
	}

	/**
	 * @return the replacingExpressions, see {@link #replacingExpressions}
	 */
	public String[] getReplacingExpressions() {
		return replacingExpressions;
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
