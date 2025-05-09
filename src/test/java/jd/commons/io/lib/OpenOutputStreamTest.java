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


import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;


public class OpenOutputStreamTest
{
	@Test
	public void test() throws IOException
	{
		TestOutputStream testOut = new TestOutputStream();
		OpenOutputStream openOut = new OpenOutputStream(testOut);

		assertEquals(0, testOut.flushed);
		openOut.flush();
		assertEquals(1, testOut.flushed);
		openOut.close();
		assertEquals(2, testOut.flushed);
	}
	
	
	private static class TestOutputStream extends OutputStream
	{
		public int flushed;
		
		
		@Override
		public void write(int b) throws IOException
		{
		}

		
		@Override
		public void flush()
		{
			flushed++;
		}

	
	    @Override
		public void close() throws IOException 
	    {
	    	throw new UnsupportedOperationException();
	    }
	}
}
