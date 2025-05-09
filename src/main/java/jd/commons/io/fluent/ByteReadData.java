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
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.io.fluent.handler.ErrorFunction;
import jd.commons.util.UncheckedException;
import jd.commons.util.function.XFunction;


/**
 * ByteReadData allows to specify what to read from a InputStream.
 * @see ByteSource#read()
 * @param<E> the exception thrown by ByteReadData methods
 */
public class ByteReadData<E extends Exception> //implements AsCharset<CharReadData<E>>
{
	protected final ByteSource source_;
	protected final ErrorFunction<Void,Void,E> error_;
	
	
	/**
	 * Creates a ByteReadData instance which reads from a ByteSource and throws
	 * the specified type of exceptions
	 * @param source a ByteSource, not null
	 * @param error handles exceptions, not null
	 */
	public ByteReadData(ByteSource source, ErrorFunction<Void,Void,E> error)
	{
		source_  = Check.notNull(source, "source");
		error_   = Check.notNull(error, "error");
	}
	
	
	/**
	 * Reads and returns the whole content as byte array.
	 * @return the byte array
	 * @throws E if an error occurs 
	 * @see InputStream#readAllBytes()
	 */
	public byte[] all() throws E
	{
		return apply(InputStream::readAllBytes);
	}


//	@Override
//	public CharReadData<E> as(Charset charset)
//	{
//		return new CharReadData<>(source_.as(charset), error_);
//	}
//
//
	/**
	 * Reads and returns the first len bytes of the binary content.
	 * @param len the number of bytes to read
	 * @return the byte array
	 * @throws E if an error occurs 
	 * @see InputStream#readNBytes(int)
	 */
	public byte[] first(int len) throws E
	{
		Check.value(len, "len").greaterEq(0);
		return apply(in -> in.readNBytes(len));
	}
	

	/**
	 * Opens an InputStream to the content, forwards to the function
	 * and returns the function result. 
	 * @return the result
	 * @param <T> the result type
	 * @param fn a function 
	 * @throws E if an error occurs
	 */
	public <T> T apply(XFunction<InputStream,T,?> fn) throws E
	{
		Check.notNull(fn, "fn");
		try (InputStream in = source_.getInputStream())
		{
			return fn.apply(in);
		}
		catch (Exception e)
		{
			error_.handleException(e);
			return null;
		}
	}


	/**
	 * @return a new ByteReadData which catches all exceptions thrown by this ByteReadData
	 * 		and rethrows it as RuntimeException.
	 * @see UncheckedException#create(Exception)
	 */
	@CheckReturnValue
	public ByteReadData<RuntimeException> unchecked()
	{
		return new ByteReadData<>(source_, ErrorFunction.throwUnchecked());
	}


	/**
	 * Returns new ByteReadData which converts thrown exceptions to a new type.
	 * @param factory a factory which receives a thrown exception. It must either
	 * 		throw an own exception of type F or create an exception of type F
	 * 		(which is then thrown). The second case makes it easy to use 
	 * 		method handles to specify a factory (e.g. {@code IllegalStateException::new}).
	 * @param <F> an exception type
	 * @return the ByteReadData
	 */
	@CheckReturnValue
	public <F extends Exception> ByteReadData<F> throwing(XFunction<Exception,F,F> factory)
	{
		return new ByteReadData<>(source_, ErrorFunction.throwing(factory));
	}
}
