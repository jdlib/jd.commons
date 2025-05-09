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


import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import jd.commons.check.Check;
import jd.commons.io.fluent.ByteWriteTo;
import jd.commons.util.UncheckedException;
import jd.commons.util.function.XFunction;


/**
 * ErrorFunction handles exceptions thrown during IO operations.
 * It either
 * <ul>
 * <li>takes a thrown exception, converts it to an exception of another type and throws that exception.
 * 	   By default IO operations either throw IOException or RuntimeExceptions. 
 *     Most operations can be made {@link ByteWriteTo#unchecked() unchecked}, i.e. only runtime 
 *     exceptions are thrown, and any checked exception is wrapped into a IllegalStateException.
 * <li>silences an exception, i.e. it takes a thrown exception, optionally logs it, and just
 * 		returns the exception as result of the IO operation.
 * </ul>
 * @param<RI> the type of results passed to this error handler 
 * @param<RO> the result type of this error handler 
 * @param<E> the type of the checked exception thrown by this ExceptionRethrow or a type
 * 		derived from RuntimeException if this ExceptionRethrow only throws RuntimeExceptions 
 */
public abstract class ErrorFunction<RI,RO,E extends Exception>
{
	/**
	 * Creates a ErrorFunction which optionally forwards the exception to a {@code Consumer}
	 * (e.g. a logging service) and returns the exception.
	 * @param log a consumer which gets passed any exception (for instance to log it). Can be null.
	 * @return the silent ErrorFunction
	 */
	public static <R> ErrorFunction<R,Exception,RuntimeException> silent(Consumer<Exception> log)
	{
		return new SilentErrorFunction<>(log);
	}
	
	
	/**
	 * Creates a ErrorFunction from a factory function which when given a caugth exepction
	 * either creates a new exception or itself throws an exception.<br>
	 * The first case allows to easily specify a ErrorFunction using method lambdas, 
	 * e.g. {@code ErrorFunction.throwing(SQLException::new)}. 
	 * @param <E> the exception type 
	 * @param factory a factory.
	 * @return the ErrorFunction
	 */
	public static <RI,E extends Exception> ErrorFunction<RI,RI,E> throwing(XFunction<Exception,E,E> factory)
	{
		return new ThrowingErrorFunction<>(factory);
	}
	
	
	/**
	 * @return a ErrorFunction which takes an exception and rethrows it as {@link UncheckedIOException}.
	 * @param<R> allows to cast the returned ErrorFunction
	 */
	@SuppressWarnings("unchecked")
	public static <R> ErrorFunction<R,R,RuntimeException> throwUnchecked()
	{
		return (ErrorFunction<R,R,RuntimeException>)THROW_UNCHECKED;
	}
	

	/**
	 * @return a ErrorFunction which takes an exception and rethrows it as unchecked exception or IOException.
	 * @param<R> allows to cast the returned ErrorFunction
	 */
	@SuppressWarnings("unchecked")
	public static <R> ErrorFunction<R,R,IOException> throwUncheckedOrIOE()
	{
		return (ErrorFunction<R,R,IOException>)THROW_UNCHECKED_OR_IOE;
	}

	
	private static final ErrorFunction<?,?,RuntimeException> THROW_UNCHECKED = throwing(UncheckedException::create);
	private static final ErrorFunction<?,?,IOException> THROW_UNCHECKED_OR_IOE = throwing(ErrorFunction::throwIOEorRT);

	
	private static IOException throwIOEorRT(Exception e) throws IOException
	{
		if (e instanceof RuntimeException)
			throw (RuntimeException)e;
		else if (e instanceof IOException)
			throw (IOException)e;
		else
			throw new IOException(e);
	};
	

	/**
	 * @return a ErrorFunction which swallows any exception and always returns null.
	 * For testing.
	 * @param<R> allows to cast the returned ErrorFunction
	 */
	@SuppressWarnings("unchecked")
	public static <R> ErrorFunction<R,R,RuntimeException> swallow()
	{
		return (ErrorFunction<R,R,RuntimeException>)SWALLOW;
	}

	
	private static final ErrorFunction<?,?,RuntimeException> SWALLOW = new ErrorFunction<>()
	{
		@Override
		public Object handleResult(Object result)
		{
			return null;
		}

		@Override
		public Object handleException(Exception e) throws RuntimeException
		{
			return null;
		}

		@Override
		public String toString()
		{
			return "Swallow";
		}
	};
	
	
	/**
	 * @return accepts a result and converts it to the target resulz.
	 * @param result the result
	 */
	public abstract RO handleResult(RI result);


	/**
	 * @return accepts an exception throw and either rethrows an exception
	 * 		or returns a resulz
	 * @param e the thrown exception
	 */
	public abstract RO handleException(Exception e) throws E;


	@Override
	public abstract String toString();
}


class ThrowingErrorFunction<R,E extends Exception> extends ErrorFunction<R,R,E> 
{
	private final XFunction<Exception,E,E> factory_;
	
	
	public ThrowingErrorFunction(XFunction<Exception,E,E> factory)
	{
		factory_ = Check.notNull(factory, "factory");
	}
	
	
	@Override
	public R handleResult(R result)
	{
		return result;
	}
	
	
	@Override
	public R handleException(Exception e) throws E
	{
		E next = factory_.apply(e);
		if (next != null)
			throw next;
		else
			throw new IllegalStateException("did not return an exception: " + factory_, e);
	}


	@Override
	public String toString()
	{
		return "Throwing";
	}
}


class SilentErrorFunction<R> extends ErrorFunction<R,Exception,RuntimeException> 
{
	private final Consumer<Exception> log_;
	
	
	public SilentErrorFunction(Consumer<Exception> log)
	{
		log_ = log;
	}
	
	
	@Override
	public Exception handleResult(R result)
	{
		return null;
	}
	
	
	@Override
	public Exception handleException(Exception e)
	{
		if (log_ != null)
			log_.accept(e);
		return e;
	}


	@Override
	public String toString()
	{
		return "Silent";
	}
}