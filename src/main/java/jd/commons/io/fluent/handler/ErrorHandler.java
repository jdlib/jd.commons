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
package jd.commons.io.fluent.handler;


import java.io.Closeable;
import jd.commons.check.Check;


/**
 * An IOHandler which uses a ErrorFunction to catch and handle all thrown exceptions
 * of an inner IOHandler.
 * Depending on the error function it rethrows any caught exception or swallows the exception
 * and returns it as result.
 * @param <AS> the supplier type processed by this handler
 * @param <AD> the direct type processed by this handler
 * @param <RI> the result type returned by the inner handler
 * @param <RO> the result type returned by this handler
 * @param <E> the type of exceptions thrown by this handler
 */
public class ErrorHandler<AS,AD extends Closeable,RI,RO,E extends Exception>
	extends IOHandler<AS,AD,RO,E>
{
	private final IOHandler<AS,AD,RI,?> inner_;
	private final ErrorFunction<RI,RO,E> error_;
	
	
	/**
	 * Creates a new ErrorHandler. 
	 * @param inner an inner IOHandler
	 * @param error the error function
	 */
	public ErrorHandler(IOHandler<AS,AD,RI,?> inner, ErrorFunction<RI,RO,E> error)
	{
		inner_ = Check.notNull(inner, "inner");
		error_ = Check.notNull(error, "error");
	}


	/**
	 * Returns the inner IOHandler.
	 */
	@Override
	public IOHandler<?,?,?,?> getInner()
	{
		return inner_;
	}
	
	
	@Override
	public RO runSupplier(AS arg) throws E
	{
		try
		{
			return error_.handleResult(inner_.runSupplier(arg));
		}
		catch (Exception e)
		{
			return error_.handleException(e);
		}
	}
	
	
	@Override
	public RO runDirect(AD arg) throws E
	{
		try
		{
			return error_.handleResult(inner_.runDirect(arg));
		}
		catch (Exception e)
		{
			return error_.handleException(e);
		}
	}
	
	
	@Override
	protected String describe()
	{
		return error_.toString();
	}
}
