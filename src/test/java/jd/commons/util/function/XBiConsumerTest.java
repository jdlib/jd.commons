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
package jd.commons.util.function;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import jd.commons.util.UncheckedException;


public class XBiConsumerTest
{
	@Test
	public void test() throws SQLException
	{
		final StringBuilder b = new StringBuilder();
		XBiConsumer<String,String,SQLException> c  = (s1,s2) -> b.append(s1).append(s2);
		c.accept("a", "b");
		assertEquals("ab", b.toString());
		
		XBiConsumer<String,String,SQLException> c2 = c.andThen(c);
		b.setLength(0);
		c2.accept("a", "b");
		assertEquals("abab", b.toString());
	}


	@Test
	public void testUnchecked() throws SQLException
	{
		XBiConsumer<String,String,IOException> c1 = (s1,s2) -> {};
		XBiConsumer<String,String,IOException> c2 = (s1,s2) -> { throw new IOException(); };
		
		c1.unchecked().accept("a", "b");
		assertThatThrownBy(() -> c2.unchecked().accept("a", "b"))
			.isInstanceOf(UncheckedException.class);
	}
}
