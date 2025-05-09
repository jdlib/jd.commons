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


import java.io.Writer;
import java.nio.charset.Charset;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.io.fluent.handler.CountCharsHandler;
import jd.commons.io.fluent.handler.EncodeHandler;
import jd.commons.io.fluent.handler.ErrorFunction;
import jd.commons.io.fluent.handler.ErrorHandler;
import jd.commons.io.fluent.handler.IOHandler;
import jd.commons.io.fluent.handler.WrapHandler;
import jd.commons.io.lib.StringWriter2;
import jd.commons.util.function.XFunction;


/**
 * CharWriteTo allows you to specify a CharTarget or Writer to which character content is written.
 * Additionally it can be modified 
 * <ul>
 * <li>to return the {@link #countChars() number of written chars},
 * <li>to turn IOExceptions into {@link #unchecked() unchecked exceptions}
 * <li>to {@link #silent(Consumer) log exceptions}
 * </ul>
 * @see CharSource#write()
 * @see CharWritable#write()
 * @param<R> the type of the result returned by this CharWriteTo
 * @param<E> the type of exceptions thrown by this CharWriteTo
 */
public class CharWriteTo<R,E extends Exception> implements CharTo<R,E>, AsCharset<ByteWriteTo<R,E>>
{
	/**
	 * The handler.
	 */
	protected final IOHandler<CharTarget,Writer,R,E> handler_;
	
	
	/**
	 * Creates a new CharWriteTo.
	 * @param handler the handler implementing the write operations
	 */
	public CharWriteTo(IOHandler<CharTarget,Writer,R,E> handler)
	{
		handler_ = Check.notNull(handler, "handler");
	}
	
	
	/**
	 * Instructs the CharWrite to encode the character content using the given charset.
	 * @param charset a Charset
	 * @return a new ByteWrite builder to specify where to write the byte content
	 */
	@CheckReturnValue
	@Override
	public ByteWriteTo<R,E> as(Charset charset)
	{
		Check.notNull(charset, "charset");
		return new ByteWriteTo<>(new EncodeHandler<>(handler_, charset));
	}

	
	/**
	 * Writes the char content to a Writer.
	 * The Writer is not closed in this operation.<br>
	 * Implementation note: Terminal implementations should flush the Writer.
	 * (Rationale: Wrapping CharWrite implementations may wrap the original Writer
	 * in a buffering Writer, so flushing the stream is needed to write any
	 * content kept in memory).  
	 * @param writer the Writer
	 * @return the result returned by this CharWriteTo
	 * @throws E if an error occurs 
	 */
	@Override 
	public R to(Writer writer) throws E
	{
		Check.notNull(writer, "writer");
		return handler_.runDirect(writer);
	}

	
	/**
	 * Writes the content to the given CharTarget.
	 * @param target a CharTarget, not null
	 * @return the result returned by this CharWRite
	 * @throws E if an error occurs 
	 */
	@Override 
	public R to(CharTarget target) throws E
	{
		/*
		in theory we would like have a default implementation of this method like:
			try (Writer writer = target.getWriter())
			{
				to(writer);
			}
		see ByteWrite#to(ByteTarget) for the rationale to not do this
		*/
		Check.notNull(target, "target");
		return handler_.runSupplier(target);
	}

	
	/**
	 * Returns the written content as String.
	 * @return the string
	 * @throws E if an error occurs 
	 */
	public String toStr() throws E
	{
		StringWriter2 sw = new StringWriter2();
		to(sw);
		return sw.toString();
	}
		
	
	/**
	 * Returns a String representation of this CharWrite.
	 * @deprecated this method is deprecated in order to warn you in case you wanted to call {@link #toStr()} 
	 * 		to retrieve the CharWriteTo result as string.
	 */
	@Override
	@Deprecated
	public String toString()
	{
		return handler_.toString();
	}
	
	
	/**
	 * @return a new CharWrite which returns the number of chars written.
	 */
	public CharWriteTo<Long,E> countChars()
	{
		return new CharWriteTo<>(new CountCharsHandler<>(handler_));
	}


	/**
	 * @return a new CharWriteTo which catches all exceptions thrown by this CharWriteTo
	 * 		and rethrows it wrapped in a RuntimeException instead.
	 */
	@CheckReturnValue
	public CharWriteTo<R,RuntimeException> unchecked()
	{
		return new CharWriteTo<>(new ErrorHandler<>(handler_, ErrorFunction.throwUnchecked()));
	}


	/**
	 * Returns new CharWriteTo which converts thrown exceptions to a new type.
	 * @param factory a factory which receives a thrown exception. It must either
	 * 		throw an own exception of type F or create an exception of type F
	 * 		(which is then thrown). The second case makes it easy to use 
	 * 		method handles to specify a factory (e.g. {@code IllegalStateException::new}).
	 * @param <F> an exception type
	 * @return the CharWriteTo
	 */
	@CheckReturnValue
	public <F extends Exception> CharWriteTo<R,F> throwing(XFunction<Exception,F,F> factory)
	{
		return new CharWriteTo<>(new ErrorHandler<>(handler_, ErrorFunction.throwing(factory)));
	}


	/**
	 * @return a new CharWrite which catches any exception and returns as result of the 
	 *      CharWrite operation. No checked exception is thrown by the new CharWrite.
	 */
	@CheckReturnValue
	public CharWriteTo<Exception,RuntimeException> silent()
	{
		return silent(null);
	}


	/**
	 * @return a new CharWrite which catches any exception, forwards to the consumer
	 * 		(which for instance may log the exception) and return the exception as result of the 
	 *      ByteWrite operation. No checked exception is thrown by the new CharWrite.
	 */
	@CheckReturnValue
	public CharWriteTo<Exception,RuntimeException> silent(Consumer<Exception> log)
	{
		return new CharWriteTo<>(new ErrorHandler<>(handler_, ErrorFunction.silent(log)));
	}
	
	
	/**
	 * @return a new CharWrite which wraps the Writer of this CharWrite
	 * 		by another Writer.
	 * @param wrapper wraps a Writer
	 */
	@CheckReturnValue
	public CharWriteTo<R,E> wrap(Function<Writer,? extends Writer> wrapper)
	{
		return new CharWriteTo<>(WrapHandler.forCharTarget(handler_, wrapper));
	}
}
