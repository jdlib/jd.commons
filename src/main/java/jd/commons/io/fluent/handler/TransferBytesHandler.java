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
import java.io.InputStream;
import java.io.OutputStream;
import jd.commons.check.Check;
import jd.commons.io.fluent.ByteSource;
import jd.commons.io.fluent.ByteTarget;


public class TransferBytesHandler<E extends Exception>
	extends IOHandler<ByteTarget,OutputStream,Void,IOException>
{
	protected final ByteSource source_;
	
	
	public TransferBytesHandler(ByteSource source)
	{
		source_ = Check.notNull(source, "source");
	}
	
	
	@Override
	public Void runSupplier(ByteTarget target) throws IOException
	{
		// create input stream before outputstream
		try (InputStream in = source_.getInputStream(); OutputStream out = target.getOutputStream())
		{
			in.transferTo(out);
		}
		return null;
	}
	

	@Override
	public Void runDirect(OutputStream out) throws IOException
	{
		Check.notNull(out, "out");
		try (InputStream in = source_.getInputStream())
		{
			in.transferTo(out);
			out.flush(); 
		}
		return null;
	}
}
