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
package jd.commons.io.lib;


import java.io.IOException;
import java.io.OutputStream;


/**
 * CountingOutputStream wraps another OutputStream and 
 * counts how many bytes were written.
 */
public class CountingOutputStream extends FilterOutputStream2
{
	private long count_;


	/**
	 * Creates a new CountingOutputStream. 
	 * @param out the wrapped stream
	 */
	public CountingOutputStream(OutputStream out)
	{
		super(out);
	}


	/**
	 * @return the number of bytes written.
	 */
	public long count()
	{
		return count_;
	}


	@Override
	public void write(int b) throws IOException
	{
		out_.write(b);
		count_++;
	}

	
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		out_.write(b, off, len);
		count_ += len;
	}
}
