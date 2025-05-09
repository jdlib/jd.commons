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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;
import jd.commons.check.Check;
import jd.commons.io.fluent.handler.ErrorFunction;
import jd.commons.io.fluent.handler.TransferCharsHandler;


/**
 * CharSource is a service which can provide a Reader.
 * See {@link IO#Chars} for factory methods to create a CharSource for various sources.
 */
public interface CharSource extends CharWritable
{
	/**
	 * @return a Reader. 
	 * This should only be called once, if this method is invoked a second time the result is undefined.
	 * @throws IOException if an I/O error occurs 
	 */
	public Reader getReader() throws IOException;

	
	/**
	 * @return a BufferedReader.
	 * @throws IOException if an I/O error occurs 
	 */
	public default BufferedReader getBufferedReader() throws IOException
	{
		return IOHelper.bufferedReader(getReader());
	}

	
	/**
	 * @return a CharSource which wraps the Reader of this CharSource
	 * 		by another Reader.
	 * @param wrapper takes a Reader and wraps by another Reader
	 */
	public default CharSource wrap(Function<Reader,? extends Reader> wrapper)
	{
		Check.notNull(wrapper, "wrapper");
		return () -> wrapper.apply(getReader());
	}
	
	
	/**
	 * @return a CharRead object which lets you define what to read from this ByteSource.
	 * 		This initial CharRead throws IOExceptions.
	 */
	public default CharReadData<IOException> read()
	{
		return new CharReadData<>(this, ErrorFunction.throwUncheckedOrIOE());
	}
	
	
	/**
	 * @return a CharWrite object which lets you define to what target to write.
	 * 		This initial CharWrite does not return a result
	 * 		and throws IOExceptions.
	 */
	@Override
	public default CharWriteTo<Void,IOException> write()
	{
		return new CharWriteTo<>(new TransferCharsHandler<>(this));
	}
}


