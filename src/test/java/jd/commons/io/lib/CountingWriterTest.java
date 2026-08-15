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


import static deepdive.ExpectStatic.*;
import org.junit.jupiter.api.Test;


public class CountingWriterTest
{
	@Test
	public void test() throws Exception
	{
		try (CountingWriter w = new CountingWriter(new StringWriter2()))
		{
			long expected = 0;
			expectEqual(expected, w.count());

			w.write(17);
			expected = expectCount(expected, 1, w);

			w.write("abcd", 1, 2);
			expected = expectCount(expected, 2, w);
		}
	}


	private long expectCount(long current, long delta, CountingWriter w)
	{
		long expected = current + delta;
		expectEqual(expected, w.count());
		return expected;
	}
}
