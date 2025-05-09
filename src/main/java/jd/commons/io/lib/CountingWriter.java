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


import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;


/**
 * CountingWriter wraps another Writer and 
 * counts how many chars were written.
 */
public class CountingWriter extends FilterWriter
{
	private long count_;

	
	public CountingWriter(Writer out)
	{
		super(out);
	}
	
	
	/**
	 * @return the number of chars written.
	 */
	public long count()
	{
		return count_;
	}
	
	
	protected void addCount(long amount)
	{
		count_ += amount;
	}


    @Override
	public void write(int c) throws IOException 
    {
        out.write(c);
        addCount(1);
    }

    
    @Override
	public void write(char cbuf[], int off, int len) throws IOException 
    {
        out.write(cbuf, off, len);
        addCount(len);
    }
    

    @Override
	public void write(String str, int off, int len) throws IOException 
    {
        out.write(str, off, len);
        addCount(len);
    }
}
