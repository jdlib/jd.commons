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


public class XBiPredicateTest
{
	@Test
	public void testAndOr() throws Exception
	{
		XBiPredicate<String,String,SQLException> pTrue  = (s1,s2) -> true;
		XBiPredicate<String,String,SQLException> pFalse = (s1,s2) -> false;
		
		assertTrue(pTrue.and(pTrue).test("x", "y"));
		assertFalse(pTrue.and(pFalse).test("x", "y"));
		assertFalse(pFalse.and(pTrue).test("x", "y"));
		assertFalse(pFalse.and(pFalse).test("x", "y"));
		
		assertTrue(pTrue.or(pFalse).test("x", "y"));
		assertTrue(pFalse.or(pTrue).test("x", "y"));
		assertFalse(pFalse.or(pFalse).test("x", "y"));
		
		assertFalse(pTrue.negate().test("x", "y"));
		assertTrue(pFalse.negate().test("x", "y"));
	}


	@Test
	public void testUnchecked() throws SQLException
	{
		XBiPredicate<String,String,IOException> p1 = (s1,s2) -> true;
		XBiPredicate<String,String,IOException> p2 = (s1,s2) -> { throw new IOException(s1); };
		
		assertTrue(() -> p1.unchecked().test("1", "2"));
		assertThatThrownBy(() -> p2.unchecked().test("1", "2"))
			.isInstanceOf(UncheckedException.class);
	}
}
