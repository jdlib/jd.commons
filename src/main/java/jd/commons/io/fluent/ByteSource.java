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


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.function.Function;
import jd.commons.check.Check;
import jd.commons.io.fluent.handler.ErrorFunction;
import jd.commons.io.fluent.handler.TransferBytesHandler;


/**
 * A ByteSource can supply an {@link InputStream}.<br>
 * Default methods defined by this interface 
 * <ul>
 * <li>{@link #read()} the content provided by the InputStream.
 * <li>{@link #write()} the content to a {@link ByteTarget}
 * <li>decode the ByteSource and turn it into a {@link CharSource}
 * </ul>
 * See {@link IO#Bytes} for factory methods to create a ByteSource for 
 * various sources.
 */
public interface ByteSource extends ByteWritable, AsCharset<CharSource>
{
	/**
	 * Returns an InputStream. This should only be called once, 
	 * if this method is invoked a second time the result is undefined.
	 * @return the InputStream
	 * @throws IOException if an I/O error occurs 
	 */
	public InputStream getInputStream() throws IOException;

	
	/**
	 * @return a ByteSource which wraps the InputStream of this ByteSource
	 * 		by another InputStream.
	 * @param wrapper takes a InputStream and wraps by another InputStream
	 */
	public default ByteSource wrap(Function<InputStream,? extends InputStream> wrapper)
	{
		Check.notNull(wrapper, "wrapper");
		return () -> wrapper.apply(getInputStream());
	}

	
	/**
	 * Returns a {@link CharSource} which is based on the InputStream
	 * provided by this ByteSource, decoded using the given charset.
	 * @param charset a Charset, not null
	 * @return the new CharSource
	 */
	@Override
	public default CharSource as(Charset charset)
	{
		Check.notNull(charset, "charset");
		return () -> new InputStreamReader(getInputStream(), charset);
	}


	/**
	 * @return a ByteReadData object which allows you to read content of this ByteSource.
	 */
	public default ByteReadData<IOException> read()
	{
		return new ByteReadData<>(this, ErrorFunction.throwUncheckedOrIOE());
	}

	
	/**
	 * @return a ByteWrite object to specify a target to which
	 * 		the content of this ByteSource should be written.
	 * 		This initial ByteWrite does not return a result
	 * 		and throws IOExceptions
	 */
	@Override
	public default ByteWriteTo<Void,IOException> write()
	{
		return new ByteWriteTo<>(new TransferBytesHandler<>(this));
	}
}
