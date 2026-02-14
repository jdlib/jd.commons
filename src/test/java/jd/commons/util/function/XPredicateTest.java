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
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import jd.commons.util.UncheckedException;


public class XPredicateTest
{
	@Test
	public void test() throws SQLException
	{
		XPredicate<String,SQLException> pTrue  = s -> true;
		XPredicate<String,SQLException> pFalse = s -> false;

		assertTrue(pTrue.and(pTrue).test("x"));
		assertFalse(pTrue.and(pFalse).test("x"));
		assertFalse(pFalse.and(pTrue).test("x"));
		assertTrue(pTrue.or(pFalse).test("x"));
		assertTrue(pFalse.or(pTrue).test("x"));
		assertFalse(pFalse.or(pFalse).test("x"));
		assertFalse(pTrue.negate().test("x"));
		assertTrue(pFalse.negate().test("x"));
	}


	@Test
	public void testUnchecked() throws SQLException
	{
		XPredicate<String,SQLException> p1 = s -> s.length() > 0;
		XPredicate<String,SQLException> p2 = s -> { throw new SQLException(); };

		assertTrue(p1.unchecked().test("s"));
		assertThatThrownBy(() -> p2.unchecked().test("s")).isInstanceOf(UncheckedException.class);
	}


	@Test
	public void testTRUEFALSE() throws Exception
	{
		XPredicate<String,Exception> f = XPredicate.FALSE();
		XPredicate<String,Exception> t = XPredicate.TRUE();

		assertFalse(f.test("x"));
		assertSame(t, f.negate());
		assertSame(t, f.or(t));
		assertSame(f, f.and(t));

		assertTrue(t.test("x"));
		assertSame(f, t.negate());
		assertSame(t, t.or(t));
		assertSame(f, t.and(f));
	}
}
