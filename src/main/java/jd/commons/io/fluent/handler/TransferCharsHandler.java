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
import java.io.Reader;
import java.io.Writer;
import jd.commons.check.Check;
import jd.commons.io.fluent.CharSource;
import jd.commons.io.fluent.CharTarget;


public class TransferCharsHandler<E extends Exception>
	extends IOHandler<CharTarget,Writer,Void,IOException>
{
	protected final CharSource source_;
	
	
	public TransferCharsHandler(CharSource source)
	{
		source_ = Check.notNull(source, "source");
	}
	
	
	@Override
	public Void runSupplier(CharTarget target) throws IOException
	{
		// create reader before writer
		try (Reader reader = source_.getReader(); Writer out = target.getWriter())
		{
			reader.transferTo(out);
		}
		return null;
	}
	

	@Override
	public Void runDirect(Writer writer) throws IOException
	{
		try (Reader reader = source_.getReader())
		{
			reader.transferTo(writer);
			// since we don't close the writer and it might be buffering in memory
			// we need to flush 
			writer.flush(); 
		}
		return null;
	}
}
