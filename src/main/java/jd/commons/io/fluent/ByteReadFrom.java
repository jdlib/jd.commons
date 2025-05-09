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
package jd.commons.io.fluent;


import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.io.fluent.handler.ErrorFunction;
import jd.commons.io.fluent.handler.ErrorHandler;
import jd.commons.io.fluent.handler.IOHandler;
import jd.commons.io.fluent.handler.WrapHandler;
import jd.commons.util.function.XFunction;


/**
 * ByteReadFrom allows to specify a {@link ByteSource} for byte input
 * which is then used to read the content and return a result.
 * @param<R> the type of the computed result
 * @param<E> the exception thrown by ByteReadFrom methods
 */
public class ByteReadFrom<R,E extends Exception> implements ByteFrom<R,E>
{
	protected final IOHandler<ByteSource,InputStream,R,E> handler_;
	
	
	public ByteReadFrom(IOHandler<ByteSource,InputStream,R,E> handler)
	{
		handler_ = Check.notNull(handler, "handler");
	}

	
	@Override
	public R from(ByteSource source) throws E
	{
		Check.notNull(source, "source");
		return handler_.runSupplier(source);
	}
	

	@Override
	public R from(InputStream in) throws E
	{
		Check.notNull(in, "in");
		return handler_.runDirect(in);
	}

	
	/**
	 * @return a new ByteReadFrom which catches any exception and returns it as result of the 
	 *      ByteWrite operation. No checked exception are thrown by the new ByteWrite.
	 */
	@CheckReturnValue
	public ByteReadFrom<Exception,RuntimeException> silent()
	{
		return silent(null);
	}


	/**
	 * @return a new ByteReadFrom which catches any exception, forwards to the consumer
	 * 		(which for instance may log the exception) and return the exception as result of the 
	 *      ByteWrite operation. No checked exception is thrown by the new ByteWrite.
	 * @param log receives the thrown exception
	 */
	@CheckReturnValue
	public ByteReadFrom<Exception,RuntimeException> silent(Consumer<Exception> log)
	{
		return new ByteReadFrom<>(new ErrorHandler<>(handler_, ErrorFunction.silent(log)));
	}


	/**
	 * Returns new ByteReadFrom which converts thrown exceptions to a new type.
	 * @param factory a factory which receives a thrown exception. It must either
	 * 		throw an own exception of type F or create an exception of type F
	 * 		(which is then thrown). The second case makes it easy to use 
	 * 		method handles to specify a factory (e.g. {@code IllegalStateException::new}).
	 * @param <F> an exception type
	 * @return the ByteReadFrom
	 */
	@CheckReturnValue
	public <F extends Exception> ByteReadFrom<R,F> throwing(XFunction<Exception,F,F> factory)
	{
		return new ByteReadFrom<>(new ErrorHandler<>(handler_, ErrorFunction.throwing(factory)));
	}
	
	
	/**
	 * @return a new ByteReadFrom which catches all exceptions thrown by this ByteReadFrom
	 * 		and rethrows it as RuntimeException.
	 */
	@CheckReturnValue
	public ByteReadFrom<R,RuntimeException> unchecked()
	{
		return new ByteReadFrom<>(new ErrorHandler<>(handler_, ErrorFunction.throwUnchecked()));
	}


	/**
	 * @return a new ByteReadFrom which wraps the InputStream of this ByteReadFrom in another 
	 * 		InputStream.
	 * @param wrapper wraps an InputStream
	 */
	@CheckReturnValue
	public ByteReadFrom<R,E> wrap(Function<InputStream,? extends InputStream> wrapper)
	{
		return new ByteReadFrom<>(WrapHandler.forByteSource(handler_, wrapper));
	}
	
	
	/**
	 * @return a string representation of this object.
	 */
	@Override
	public String toString()
	{
		return handler_.toString();
	}
}
