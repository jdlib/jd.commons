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
import java.io.StringReader;
import org.junit.jupiter.api.Test;


public class StringWriter2Test
{
	@Test
	public void testAppend()
	{
		try (StringWriter2 sw = new StringWriter2(null))
		{
			sw.append('a');
			sw.append("bc");
			sw.append(".de.", 1, 3);
			assertEquals("abcde", sw.toString());
		}
	}

	
	@Test
	public void testReadAllToSb() throws Exception
	{
		StringReader sr = new StringReader("abc"); 
		StringBuilder sb = StringWriter2.readAll(sr, null);
		assertEquals("abc", sb.toString());
	}
	
	
	@Test
	public void testWrite()
	{
		try (StringWriter2 sw = new StringWriter2(5))
		{
			assertNotNull(sw.getBuilder());
			sw.write('a');
			sw.write("bc");
			sw.write(".de.", 1, 2);
			assertEquals("abcde", sw.toString());
		}
	}
}
