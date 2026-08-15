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


import static deepdive.ExpectStatic.*;
import static deepdive.ExpectThat.*;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import jd.commons.util.UncheckedException;
import jd.commons.util.Utils;


public class XFunctionTest
{
	@Test
	public void test() throws SQLException
	{
		expectSame("s", XFunction.identity().apply("s"));

		XFunction<String,String,SQLException> pSize  = s -> String.valueOf(s.length());
		expectEqual("1", pSize.apply("s"));

		XFunction<String,String,SQLException> pSize2 = pSize.andThen(pSize);
		expectEqual("1", pSize2.apply("s"));
		expectEqual("2", pSize2.apply(Utils.repeat('a', 33)));
	}


	@Test
	public void testUnchecked() throws Exception
	{
		XFunction<String,String,IOException> f1 = s -> s + s;
		XFunction<String,String,IOException> f2 = s -> { throw new IOException(); };

		expectEqual("aa", f1.unchecked().apply("a"));
		expectError(() -> f2.unchecked().apply("a"))
			.isA(UncheckedException.class);
	}


	@Test
	public void testOf() throws Exception
	{
		XFunction<String,String,IOException> f1 = XFunction.of(String::trim);
		XFunction<String,String,IOException> f2 = s -> { throw new IOException(); };

		expectEqual("a", f1.unchecked().apply(" a "));
		expectError(() -> f2.unchecked().apply("a"))
			.isA(UncheckedException.class);
	}
}
