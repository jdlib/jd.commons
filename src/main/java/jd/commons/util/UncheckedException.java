/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2025 the original author or authors.
 */
package jd.commons.util;


import jd.commons.check.Check;


/**
 * UncheckedException is a RuntimeException which wraps 
 * a checked exception, i.e. a Exception which is not a RuntimeException.
 * One could use other RuntimeException classes for the same purpose,
 * e.g. {@link IllegalStateException} but UncheckedException uniquely expresses
 * the fact the orginal exception was a checked exception. 
 */
public class UncheckedException extends RuntimeException
{
	/**
	 * Turns an Exception into a unchecked exception, i.e. a RuntimeException.
	 * @param e an exception, not null
	 * @return If the exception is a RuntimeException it is returned. Else a new UncheckedException is
	 *		created and returned for the checked exception. 
	 */
	public static RuntimeException create(Exception e)
	{
		Check.notNull(e, "e");
		return e instanceof RuntimeException ? (RuntimeException)e : new UncheckedException(e);
	}

	
	/**
	 * Given an exception this method rethrows it as either a RuntimeException or
	 * an exception of the provided exception type.
	 * It
	 * <ul>
	 * <li>throws the original exception if it is derived from the provided type
	 * <li>throws the exception cause if the exception is a {@link UncheckedException} and the cause is derived from the provided type
	 * <li>as RuntimeException if the exception is a RuntimeException,
	 * <li>else as an Exception of the provided type if it is derived from that type
	 * </ul>  
	 * @param <E> an exception type
	 * @param <T> a pseudo return type
	 * @return this function always throws an exception and never returns a value
	 */
	@SuppressWarnings("unchecked")
	public static <T,E extends Exception> T rethrow(Exception e, Class<E> exceptionType) throws E
	{
		Check.notNull(e, "e");
		Check.notNull(exceptionType, "exceptionType");
		if (exceptionType.isInstance(e))
			throw (E)e;
		else if (e instanceof UncheckedException && exceptionType.isInstance(e.getCause()))
			throw (E)e.getCause();
		else 
			throw create(e);
	}

		
	private UncheckedException(Exception e)
	{
		super(e);
	}
	
	
	@Override
	public Exception getCause()
	{
		return (Exception)super.getCause();
	}
}
