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
import java.io.OutputStream;
import jd.commons.check.Check;
import jd.commons.io.lib.OpenInputStream;
import jd.commons.io.lib.OpenOutputStream;


/**
 * ByteFactory provides methods to construct {@link ByteSource} and {@link ByteTarget} instances.
 */
public class ByteFactory implements 
	ByteFrom<ByteSource,RuntimeException>,
	ByteTo<ByteTarget,RuntimeException>
{
	/**
	 * @return the provided ByteSource.
	 * @param source a ByteSource, not null
	 */
	@Override 
	public ByteSource from(ByteSource source)
	{
		return Check.notNull(source, "source");
	}
	
	
	/**
	 * @return a ByteSource which returns the InputStream, wrapped in a {@link OpenInputStream}
	 * 		in order to ensure that the InputStream is not closed by ByteSource operations.
	 * @param in an InputStream, not null
	 */
	@Override 
	public ByteSource from(InputStream in)
	{
		return from(in, true);
	}
	
	
	/**
	 * Creates a ByteSource for the InputStream and forwards to {@link ByteFrom#from(ByteSource)}.
	 * The InputStream can be used in subsequent ByteSource operations.
	 * The keepOpen flag controls if the InputStream can be closed
	 * in these operations (false) or if should stay open (true). 
	 * @param in an InputStream, not null
	 * @param keepOpen if true the InputStream will be wrapped in a {@link OpenInputStream}
	 * 		to prevent that it is closed
	 * @return the result of the operation implemented by this ByteFrom class
	 */
	public ByteSource from(InputStream in, boolean keepOpen)
	{
		Check.notNull(in, "in");
		InputStream finalIn = keepOpen ? new OpenInputStream(in) : in;
		return from(() -> finalIn);
	}
	
	
	/**
	 * @return the provided ByteTarget.
	 * @param target a ByteTarget, not null
	 */
	@Override
	public ByteTarget to(ByteTarget target)
	{
		return Check.notNull(target, "target");
	}
	
	
	/**
	 * @return a ByteTarget which returns the OutputStream, wrapped in a {@link OpenOutputStream}
	 * 		in order to ensure that the OutputStream is not closed by ByteTarget operations.
	 * @param out an OutputStream, not null
	 */
	@Override
	public ByteTarget to(OutputStream out)
	{
		return to(out, true /*keep open*/);
	}
	
	
	/**
	 * Creates a ByteTarget which returns the given OutputStream.
	 * @param out an OutputStream, not null
	 * @param keepOpen if true the OutputStream will be wrapped in a {@link OpenOutputStream}
	 * 		to prevent that it is closed
	 * @return the ByteTarget
	 */
	public ByteTarget to(OutputStream out, boolean keepOpen)
	{
		Check.notNull(out, "out");
		OutputStream finalOut = !keepOpen || (out instanceof OpenOutputStream) ? out : new OpenOutputStream(out);
		return to(() -> finalOut);
	}
}
