package org.caleydo.view.datawindows;

/**
 * ComplexNumber.java - An object for representing and manipulating complex
 * numbers.
 * 
 * @author Grant William Braught
 * @author Dickinson College
 * @version 11/19/2001
 */
class ComplexNumber {

	// Private data fields for the complex number:
	// realPart + imagPart * i
	private double realPart;
	private double imagPart;

	/**
	 * Construct a new ComplexNumber that has the value 0 + 0i.
	 */
	public ComplexNumber() {
	}

	/**
	 * Construct a new ComplexNumber with the value realPart + 0i.
	 * 
	 * @param realPart
	 *            initial value for the real part of the new ComplexNumber.
	 */
	public ComplexNumber(double realPart) {
		this.realPart = realPart;
	}

	/**
	 * Construct a new ComplexNumber with the value realPart + imagPart*i.
	 * 
	 * @param realPart
	 *            initial value for the real part of the new ComplexNumber.
	 * @param imagPart
	 *            initial value for the imaginary part of the new ComplexNumber.
	 */
	public ComplexNumber(double realPart, double imagPart) {
		this.realPart = realPart;
		this.imagPart = imagPart;
	}

	/**
	 * Set the value of this ComplexNumber by specifying new values for its real
	 * and imaginary parts.
	 * 
	 * @param realPart
	 *            new value for the real part of this ComplexNumber.
	 * @param imagPart
	 *            new value for the imaginary part of this ComplexNumber.
	 */
	public void setValue(double realPart, double imagPart) {
		this.realPart = realPart;
		this.imagPart = imagPart;
	}

	/**
	 * Set the value of this ComplexNumber to be the same as the value of
	 * another ComplexNumber.
	 * 
	 * @param theNum
	 *            the ComplexNumber from which the real and imaginary parts are
	 *            to be copied into this ComplexNumber.
	 */
	public void setValue(ComplexNumber theNum) {
		realPart = theNum.realPart;
		imagPart = theNum.imagPart;
	}

	/**
	 * Test whether the this ComplexNumber and the ComplexNumber theNum are
	 * equal. Two ComplexNumbers are equal if their real parts are equal and
	 * their imaginary parts are equal.
	 * 
	 * @param theNum
	 *            the ComplexNumber to which this ComplexNumber is to be
	 *            compared for equality.
	 * @return true if the real and imaginary parts of this ComplexNumber are
	 *         equal to the real and imaginary parts of theNum, and false
	 *         otherwise.
	 */
	public boolean equals(ComplexNumber theNum) {
		return ((realPart == theNum.realPart) && (imagPart == theNum.imagPart));
	}

	/**
	 * Return a reference to a new ComplexNumber with value equal to the sum of
	 * this ComplexNumber and the ComplexNumber theNum.
	 * 
	 * @param theNum
	 *            a ComplexNumber to be added to this ComplexNumber.
	 * @return a reference to a new ComplexNumber equal to the sum of this
	 *         ComplexNumber and theNum.
	 */
	public ComplexNumber add(ComplexNumber theNum) {
		return new ComplexNumber(realPart + theNum.realPart, imagPart + theNum.imagPart);
	}

	/**
	 * Return a reference to a new ComplexNumber with value equal to the
	 * difference between this ComplexNumber and the ComplexNumber theNum.
	 * 
	 * @param theNum
	 *            a ComplexNumber to be subtracted from this ComplexNumber.
	 * @return a reference to a new ComplexNumber equal to the difference
	 *         between this ComplexNumber and theNum.
	 */
	public ComplexNumber subtract(ComplexNumber theNum) {
		return new ComplexNumber(realPart - theNum.realPart, imagPart - theNum.imagPart);
	}

	// added:
	public ComplexNumber multiply(ComplexNumber theNum) {
		return new ComplexNumber(realPart * theNum.realPart - imagPart * theNum.imagPart,
				realPart * theNum.imagPart + imagPart * theNum.realPart);

	}

	// added:
	public ComplexNumber divide(ComplexNumber theNum) {
		return new ComplexNumber(
				(realPart * theNum.realPart + imagPart * theNum.imagPart)
						/ (theNum.realPart * theNum.realPart + theNum.imagPart
								* theNum.imagPart),
				(imagPart * theNum.realPart - realPart * theNum.imagPart)
						/ (theNum.realPart * theNum.realPart + theNum.imagPart
								* theNum.imagPart));
	}

	/**
	 * Get the real part of this ComplexNumber.
	 * 
	 * @return the real part of this ComplexNumber.
	 */
	public double getRealPart() {
		return realPart;
	}

	/**
	 * Get the imaginary part of this ComplexNumber.
	 * 
	 * @return the imaginary part of this ComplexNumber.
	 */
	public double getImaginaryPart() {
		return imagPart;
	}

	/**
	 * Convert this ComplexNumber into a String representation. The string
	 * representation is realPart + imagPart i.
	 * 
	 * @return a String representation of this ComplexNumber.
	 */
	@Override
	public String toString() {

		// If the imagPart is >= 0 print out a + b i
		if (imagPart >= 0) {
			return realPart + " + " + imagPart + " i";
		}
		// If the imagPart is < 0 print out a - b i
		else {
			return realPart + " - " + (-imagPart) + " i";
		}
	}

	/**
	 * Compute the square root of num and return a reference to a new
	 * ComplexNumber that contains the result. Note that num may be negative and
	 * thus a ComplexNumber may be necessary to represent the square root.
	 * 
	 * @param num
	 *            the value of which the square root is to be found.
	 * @return a reference to a new ComplexNumber equal to the square root of
	 *         num.
	 */
	public static ComplexNumber sqrt(double num) {
		if (num >= 0) {
			return new ComplexNumber(Math.sqrt(num), 0);
		} else {
			return new ComplexNumber(0, Math.sqrt(Math.abs(num)));
		}
	}
}
