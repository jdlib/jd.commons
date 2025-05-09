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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.function.Function;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;


/**
 * ByteTarget can provide an OutputStream to receive binary content.
 * See {@link IO#Bytes} for factory methods to create a ByteTarget 
 * for various targets.
 */
public interface ByteTarget extends AsCharset<CharTarget>
{
	/**
	 * @return an OutputStream.
	 * This should only be called once, if this method is invoked a second time the result is undefined.
	 * @throws IOException if an I/O error occurs 
	 */
	public OutputStream getOutputStream() throws IOException;
	
	
	/**
	 * @return a PrintStream based on the OutputStream of this ByteTarget.
	 * @throws IOException if an I/O error occurs 
	 */
	public default PrintStream getPrintStream() throws IOException
	{
		return getPrintStream(false);
	}
	
	
	/**
	 * @return a PrintStream based on the OutputStream of this ByteTarget.
	 * @param autoFlush the auto flush flag of the PrintStream
	 * @throws IOException if an I/O error occurs 
	 */
	public default PrintStream getPrintStream(boolean autoFlush) throws IOException
	{
		return new PrintStream(getOutputStream(), autoFlush);
	}

	
	/**
	 * Returns a {@link CharTarget} which is based on the OutputStream
	 * provided by this ByteTarget, encoded using the given charset.
	 * @param charset a Charset, not null
	 * @return the new CharTarget
	 */
	@CheckReturnValue
	@Override
	public default CharTarget as(Charset charset)
	{
		Check.notNull(charset, "charset");
		return () -> new OutputStreamWriter(getOutputStream(), charset);
	}

	
	/**
	 * @return a ByteTarget which wraps the OutputStream of this ByteTarget
	 * 		by another OutputStream.
	 * @param wrapper takes a OutputStream and wraps by another Reader
	 */
	public default ByteTarget wrap(Function<OutputStream,? extends OutputStream> wrapper)
	{
		Check.notNull(wrapper, "wrapper");
		return () -> wrapper.apply(getOutputStream());
	}
}
