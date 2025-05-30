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
import org.junit.jupiter.api.Test;


public class AppendableWriterTest
{
	@Test
	public void test() throws Exception
	{
		StringBuilder s = new StringBuilder();
		try (AppendableWriter w = new AppendableWriter(s))
		{
			assertSame(s, w.getAppendable());
			
			w.append('a').append("bc").append(".de", 1, 3);
			w.write('f');
			w.write("gh");
			w.write(".ij.", 1, 2);
			
			assertEquals("abcdefghij", w.toString());
		}
	}
}
