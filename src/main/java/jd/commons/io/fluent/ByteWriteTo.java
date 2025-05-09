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


import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.io.fluent.handler.CountBytesHandler;
import jd.commons.io.fluent.handler.ErrorFunction;
import jd.commons.io.fluent.handler.ErrorHandler;
import jd.commons.io.fluent.handler.IOHandler;
import jd.commons.io.fluent.handler.WrapHandler;
import jd.commons.util.function.XFunction;


/**
 * ByteWriteTo allows to specify a ByteTarget or OutputStream to which binary content is written.
 * Additionally it can be modified 
 * <ul>
 * <li>to return the {@link #countBytes() number of written bytes},
 * <li>to turn IOExceptions into {@link #unchecked() unchecked exceptions}
 * <li>to {@link #silent(Consumer) log exceptions}
 * </ul>
 * @see ByteWritable#write()
 * @see ByteSource#write()
 * @param<R> the type of the result returned
 * @param<E> the type of exceptions thrown by the ByteWriteTo
 */
public class ByteWriteTo<R,E extends Exception> implements ByteTo<R,E>
{
	/**
	 * The handler.
	 */
	protected final IOHandler<ByteTarget,OutputStream,R,E> handler_;
	
	
	/**
	 * Creates a new ByteWriteTo.
	 * @param handler the handler implementing the write operations
	 */
	public ByteWriteTo(IOHandler<ByteTarget,OutputStream,R,E> handler)
	{
		handler_ = Check.notNull(handler, "handler");
	}
	
	
	/**
	 * Writes the bytes to the OutputStream of the given target.
	 * @param target the target
	 * @return the computed result
	 * @throws E if an error occurs 
	 */
	/*
	in theory we would like have a default implementation of this method like:
		try (OutputStream out = target.getOutputStream())
		{
			to(out);
		}
	but then the output will be opened before the input:
		e.g. Bytes.from(fileIn).asUtf8().write().as(UTF_16).to(fileOut);
	if opening fileIn fails with FileNotFoundException
	then still fileOut would have been created first (see ByteSourceByteWrite.to(OutputStream))
	therefore ByteWriteTo keeps two abstract methods which need to be implemented
	*/
	@Override
	public R to(ByteTarget target) throws E
	{
		Check.notNull(target, "target");
		return handler_.runSupplier(target);
	}

	
	/**
	 * Writes the bytes to a OutputStream.
	 * The OutputStream is/should not be closed in this operation.
	 * Implementation note: Terminal implementations should flush the OutputStream.
	 * (Rationale: Wrapping ByteWriteTo implementations may wrap the original OutputStream
	 * in a buffering OutputStream, so flushing the stream is needed to write any
	 * content kept in memory).  
	 * @param out the OutputStream
	 * @return the number of bytes written
	 * @throws E if an error occurs 
	 */
	@Override
	public R to(OutputStream out) throws E
	{
		return handler_.runDirect(out);
	}


	/**
	 * Writes the bytes to a byte array.
	 * @return the array
	 * @throws E if an error occurs 
	 */
	public byte[] toByteArray() throws E
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		to(out);
		return out.toByteArray();
	}
	
	
	/**
	 * @return a new ByteWriteTo which returns the number of bytes written as Long.
	 */
	@CheckReturnValue
	public ByteWriteTo<Long,E> countBytes()
	{
		return new ByteWriteTo<>(new CountBytesHandler<>(handler_));
	}


	/**
	 * @return a new ByteWriteTo which wraps the OutputStream of this write in another 
	 * 		OutputStream.
	 * @param wrapper wraps an OutputStream
	 */
	@CheckReturnValue
	public ByteWriteTo<R,E> wrap(Function<OutputStream,? extends OutputStream> wrapper)
	{
		return new ByteWriteTo<>(WrapHandler.forByteTarget(handler_, wrapper));
	}

	
	/**
	 * @return a new ByteWriteTo which catches all exceptions thrown by this ByteWriteTo
	 * 		and rethrows it as RuntimeException.
	 */
	@CheckReturnValue
	public ByteWriteTo<R,RuntimeException> unchecked()
	{
		return new ByteWriteTo<>(new ErrorHandler<>(handler_, ErrorFunction.throwUnchecked()));
	}


	/**
	 * Returns new ByteWriteTo which converts thrown exceptions to a new type.
	 * @param factory a factory which receives a thrown exception. It must either
	 * 		throw an own exception of type F or create an exception of type F
	 * 		(which is then thrown). The second case makes it easy to use 
	 * 		method handles to specify a factory (e.g. {@code IllegalStateException::new}).
	 * @param <F> an exception type
	 * @return the ByteWriteTo
	 */
	@CheckReturnValue
	public <F extends Exception> ByteWriteTo<R,F> throwing(XFunction<Exception,F,F> factory)
	{
		return new ByteWriteTo<>(new ErrorHandler<>(handler_, ErrorFunction.throwing(factory)));
	}


	/**
	 * @return a new ByteWriteTo which catches any exception and returns it as result of the 
	 *      ByteWriteTo operation. No checked exception are thrown by the new ByteWrite.
	 */
	@CheckReturnValue
	public ByteWriteTo<Exception,RuntimeException> silent()
	{
		return silent(null);
	}


	/**
	 * @return a new ByteWrite which catches any exception, forwards to the consumer
	 * 		(which for instance may log the exception) and return the exception as result of the 
	 *      ByteWrite operation. No checked exception is thrown by the new ByteWrite.
	 * @param logger receives any thrown exception
	 */
	@CheckReturnValue
	public ByteWriteTo<Exception,RuntimeException> silent(Consumer<Exception> logger)
	{
		return new ByteWriteTo<>(new ErrorHandler<>(handler_, ErrorFunction.silent(logger)));
	}
	
	
	@Override
	public String toString()
	{
		return handler_.toString();
	}
}
