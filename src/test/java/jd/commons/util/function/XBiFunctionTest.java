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


public class XBiFunctionTest
{
	@Test
	public void test() throws SQLException
	{
		XBiFunction<String,String,String,SQLException> f  = (s1,s2) -> s1 + s2;
		assertEquals("st", f.apply("s", "t"));
		
		XBiFunction<String,String,String,SQLException> f2 = f.andThen(s -> s + '!');
		assertEquals("st!", f2.apply("s", "t"));
	}


	@Test
	public void testUnchecked() throws SQLException
	{
		XBiFunction<String,String,String,IOException> c1 = (s1,s2) -> s1 + s2;
		XBiFunction<String,String,String,IOException> c2 = (s1,s2) -> { throw new IOException(); };
		
		assertEquals("ab", c1.unchecked().apply("a", "b"));
		assertThatThrownBy(() -> c2.unchecked().apply("a", "b"))
			.isInstanceOf(UncheckedException.class);
	}
}
