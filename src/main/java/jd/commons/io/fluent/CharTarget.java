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
import java.io.PrintWriter;
import java.io.Writer;
import java.util.function.Function;
import jd.commons.check.Check;


/**
 * CharTarget can provide a Writer to receive character content.
 * See {@link IO#Chars} for factory methods to create a CharTarget for various targets.
 */
public interface CharTarget
{
	/**
	 * @return a Writer.
	 * This should only be called once, if this method is invoked a second time the result is undefined.
	 * @throws IOException if an I/O error occurs 
	 */
	public Writer getWriter() throws IOException;


	/**
	 * @return a PrintWriter.
	 * @throws IOException if an I/O error occurs 
	 */
	public default PrintWriter getPrintWriter() throws IOException
	{
		return getPrintWriter(false);
	}


	/**
	 * @return a PrintWriter.
	 * @param autoFlush the auto flush flag of the PrintStream
	 * @throws IOException if an I/O error occurs 
	 */
	public default PrintWriter getPrintWriter(boolean autoFlush) throws IOException
	{
		return new PrintWriter(getWriter(), autoFlush);
	}


	/**
	 * @return a CharTarget which wraps the Writer of this CharTarget
	 * 		by another Writer.
	 * @param wrapper takes a Writer and wraps by another Writer
	 */
	public default CharTarget wrap(Function<Writer,? extends Writer> wrapper)
	{
		Check.notNull(wrapper, "wrapper");
		return () -> wrapper.apply(getWriter());
	}
}


