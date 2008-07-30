/*
 * Created on Jul 19, 2003
 */
package org.caleydo.core.util.exception;

import java.lang.RuntimeException;

/**
 * Base class for all RuntimeException.
 * 
 * @author Michael Kalkusch
 */
public class CaleydoRuntimeException
	extends RuntimeException
{

	final static long serialVersionUID = 7000;

	protected final CaleydoRuntimeExceptionType type;

	/**
	 * 
	 */
	public CaleydoRuntimeException()
	{

		super();

		type = CaleydoRuntimeExceptionType.RUNTIME;
	}

	/**
	 * @param s
	 */
	public CaleydoRuntimeException(String s)
	{

		super(s);

		type = CaleydoRuntimeExceptionType.RUNTIME;
	}

	/**
	 * @param s
	 */
	public CaleydoRuntimeException(String s, final CaleydoRuntimeExceptionType type)
	{

		super(s);
		this.type = type;
	}

	/**
	 * @param s
	 * @param ex
	 */
	public CaleydoRuntimeException(String s, Throwable ex)
	{

		super(s, ex);

		type = CaleydoRuntimeExceptionType.RUNTIME;
	}

	/**
	 * @param s
	 * @param ex
	 */
	public CaleydoRuntimeException(String s, Throwable ex,
			final CaleydoRuntimeExceptionType type)
	{

		super(s, ex);

		this.type = type;
	}

	/**
	 * Expose error type
	 * 
	 * @return the type
	 */
	public final CaleydoRuntimeExceptionType getType()
	{

		return type;
	}

}
