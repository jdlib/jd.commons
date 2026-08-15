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
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;


public class CountingOutputStreamTest
{
	@Test
	public void test() throws Exception
	{
		try (CountingOutputStream out = new CountingOutputStream(new ByteArrayOutputStream()))
		{
			long expected = 0;
			expectEqual(expected, out.count());

			out.write(17);
			expected = expectCount(expected, 1, out);

			out.write(new byte[4]);
			expected = expectCount(expected, 4, out);

			out.write(new byte[15], 1, 2);
			expected = expectCount(expected, 2, out);
		}
	}


	private long expectCount(long current, long delta, CountingOutputStream out)
	{
		long expected = current + delta;
		expectEqual(expected, out.count());
		return expected;
	}
}
