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
package jd.commons.util;


import static deepdive.ExpectStatic.*;
import static deepdive.ExpectThat.*;
import java.lang.annotation.Annotation;
import java.nio.file.AccessMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;


public class UtilsTest
{
	@Test
	public void testAddFirst()
	{
		String[] array = {"a", "b", "c"};
		String[] newItems = {"1", "2"};

		expectNull(Utils.addFirst(null));
		expectSame(array, Utils.addFirst(array));
		expectSame(array, Utils.addFirst(array, (String[])null));
		expectSame(newItems, Utils.addFirst(null, newItems));

		String[] result = Utils.addFirst(array, newItems);
		expectThat(result).elems("1", "2", "a", "b", "c");
	}


	@Test
	public void testAddLast()
	{
		String[] array = {"a", "b", "c"};
		String[] newItems = {"1", "2"};

		expectNull(Utils.addLast(null));
		expectSame(array, Utils.addLast(array));
		expectSame(array, Utils.addLast(array, (String[])null));
		expectSame(newItems, Utils.addLast(null, newItems));

		String[] result = Utils.addLast(array, newItems);
		expectThat(result).elems("a", "b", "c", "1", "2");
	}


	@Test
	public void testAfterOr()
	{
		expectNull(Utils.afterOr(null, '=', null));
		expectNull(Utils.afterOr("123", '=', null));
		expectEqual("", 		Utils.afterOr(null, '=', ""));
		expectEqual("123", 	Utils.afterOr("abc=123", '=', null));
		expectEqual("123=xyz", Utils.afterOr("abc=123=xyz", '=', null));
		expectEqual("", 		Utils.afterOr("abc=", '=', null));
	}


	@Test
	public void testAfterLastOr()
	{
		expectNull(Utils.afterLastOr(null, '=', null));
		expectNull(Utils.afterLastOr("123", '=', null));
		expectEqual("3", 	Utils.afterLastOr("1.2.3", '.', null));
		expectEqual("", 	Utils.afterLastOr("1.2.", '.', null));
	}


	@Test
	public void testBeforeOr()
	{
		expectNull(Utils.beforeOr(null, '=', null));
		expectEqual("abc", Utils.beforeOr("abc", '=', "abc"));
		expectEqual("abc", Utils.beforeOr("abc=123", '=', ""));
		expectEqual("", 	Utils.beforeOr("=123", '=', null));
		expectEqual("", 	Utils.beforeOr(null, '=', ""));
	}


	@Test
	public void testBeforeLastOr()
	{
		expectNull(Utils.beforeLastOr(null, '=', null));
		expectEqual("abc", Utils.beforeLastOr("abc", '=', "abc"));
		expectEqual("abc", Utils.beforeLastOr("abc=123", '=', ""));
		expectEqual("", 	Utils.beforeLastOr("=123", '=', null));
		expectEqual("1.2", Utils.beforeLastOr("1.2.3", '.', null));
		expectEqual("", 	Utils.beforeLastOr(null, '=', ""));
	}


	@Test
	public void testCut()
	{
		expectNull(Utils.cutStart(null, "x"));
		expectEqual("",	Utils.cutStart("", "x"));
		expectEqual("",	Utils.cutStart("x", "x"));
		expectEqual("ax",	Utils.cutStart("ax", "x"));

		expectNull(Utils.cutEnd(null, "x"));
		expectEqual("",	Utils.cutEnd("", "x"));
		expectEqual("",	Utils.cutEnd("x", "x"));
		expectEqual("xa",	Utils.cutEnd("xa", "x"));
	}


	@Test
	public void testEnumOf()
	{
		expectNull(Utils.enumOf(AccessMode.class, null));
		expectSame(AccessMode.READ, Utils.enumOf(AccessMode.class, null, AccessMode.READ));
		expectSame(AccessMode.READ, Utils.enumOf(AccessMode.class, "xy", AccessMode.READ));
		expectSame(AccessMode.WRITE, Utils.enumOf(AccessMode.class, "WRITE", AccessMode.READ));
	}


	@Test
	public void testIndexOf()
	{
		expectEqual(-1, Utils.indexOf("a", (String[])null));
		expectEqual(-1, Utils.indexOf("a"));
		expectEqual(-1, Utils.indexOf("a", "b"));
		expectEqual(1, Utils.indexOf("a", "b", "a", "c"));
	}


	@Test
	public void testIsA() throws Exception
	{
		expectFalse(Utils.isA(null, null));
		expectFalse(Utils.isA(null, CharSequence.class));
		expectFalse(Utils.isA("a", null));
		expectFalse(Utils.isA("a", Integer.class));
		expectTrue(Utils.isA("a", CharSequence.class));

		// test annotations
		Annotation testAnno = getClass().getMethod("testIsA").getAnnotations()[0];
		expectTrue(Utils.isA(testAnno, Test.class));
		expectFalse(Utils.isA(testAnno, SuppressWarnings.class));
		expectFalse(Utils.isA(testAnno, null));
		expectFalse(Utils.isA(null, Test.class));
	}


	@Test
	public void testIsBlank() throws Exception
	{
		expectTrue(Utils.isBlank(null));
		expectTrue(Utils.isBlank(""));
		expectTrue(Utils.isBlank(" \t\r\n"));
		expectFalse(Utils.isBlank(" \t\r\na "));
		expectFalse(Utils.isBlank("a "));
	}


	@Test
	public void testIsEmpty()
	{
		expectTrue(Utils.isEmpty((CharSequence)null));
		expectTrue(Utils.isEmpty(""));
		expectFalse(Utils.isEmpty("a"));

		expectTrue(Utils.isEmpty((Object[])null));
		expectTrue(Utils.isEmpty(new Object[0]));
		expectFalse(Utils.isEmpty(new String[] { "a" }));

		expectTrue(Utils.isEmpty((Collection<?>)null));
		expectTrue(Utils.isEmpty(List.of()));
		expectFalse(Utils.isEmpty(List.of("a")));

		expectTrue(Utils.isEmpty((Map<?,?>)null));
		expectTrue(Utils.isEmpty(Map.of()));
		expectFalse(Utils.isEmpty(Map.of("a", "1")));
	}


	@Test
	public void testHave()
	{
		expectEqual("x",	Utils.haveStart(null, "x"));
		expectEqual("x",	Utils.haveStart("", "x"));
		expectEqual("x",	Utils.haveStart("x", "x"));
		expectEqual("xa",	Utils.haveStart("a", "x"));

		expectEqual("x",	Utils.haveEnd(null, "x"));
		expectEqual("x",	Utils.haveEnd("", "x"));
		expectEqual("x",	Utils.haveEnd("x", "x"));
		expectEqual("ax",	Utils.haveEnd("a", "x"));
	}


	@Test
	public void testNewArray()
	{
		String[] s5 = Utils.newArray(String.class, 5);
		expectEqual(5, s5.length);

		String[] s6 = Utils.newArray(s5, 6);
		expectEqual(6, s6.length);
	}


	@Test
	public void testNewHashSet()
	{
		expectThat(Utils.newHashSet("a", "b")).contains().exactly("a", "b");
	}


	@Test
	public void testNorm()
	{
		expectNull(Utils.norm(null));
		expectNull(Utils.norm(""));
		expectNull(Utils.norm(" "));
		expectEqual("a", Utils.norm(" a "));
	}


	@Test
	public void testNotNull()
	{
		expectSame("a", Utils.notNull("a"));
		expectSame("", Utils.notNull(null));
	}


	@Test
	public void testPackageName()
	{
		expectEqual("jd.commons.util", Utils.packageName(Utils.class));
		expectEqual("jd.commons.util", Utils.packageName("jd.commons.util.Utils"));
		expectEqual("", Utils.packageName("Utils"));
		expectEqual("", Utils.packageName(""));
	}


	@Test
	public void testPad()
	{
		expectEqual("a..", Utils.padEnd("a", 3, '.'));
		expectEqual("a.",  Utils.padEnd("a", 2, '.'));
		expectEqual("a  ", Utils.padEnd("a", 3));
		expectEqual("100", Utils.padEnd(1, 3));
		expectEqual("123", Utils.padEnd(123, 2));

		expectEqual("..a", Utils.padStart("a", 3, '.'));
		expectEqual(".a",  Utils.padStart("a", 2, '.'));
		expectEqual("  a", Utils.padStart("a", 3));
		expectEqual("001", Utils.padStart(1, 3));
		expectEqual("123", Utils.padStart(123, 2));
		expectEqual("!1",  Utils.padStart(1, 2, '!'));
	}


	@Test
	public void testRepeat()
	{
		StringBuilder sb = new StringBuilder();
		expectSame(sb, Utils.repeat('0', 3, sb));
		expectEqual("000", sb.toString());

		expectEqual("aa", Utils.repeat('a', 2));
		expectEqual("", Utils.repeat('a', -15));
	}


	@Test
	public void testStartCase()
	{
		expectNull(Utils.startLowerCase(null));
		expectEqual("", 	Utils.startLowerCase(""));
		expectEqual("abc", Utils.startLowerCase("abc"));
		expectEqual("aBC", Utils.startLowerCase("ABC"));

		expectNull(Utils.startUpperCase(null));
		expectEqual("", 	Utils.startUpperCase(""));
		expectEqual("Abc", Utils.startUpperCase("Abc"));
		expectEqual("ABC", Utils.startUpperCase("aBC"));
	}


	@Test
	public void testTrimEnd()
	{
		expectNull(Utils.trimEnd(null));
		expectSame("a", Utils.trimEnd("a"));
		expectEqual(" a b", Utils.trimEnd(" a b \t\r\n"));
	}


	@Test
	public void testTrimStart()
	{
		expectNull(Utils.trimStart(null));
		expectSame("a", Utils.trimStart("a"));
		expectEqual("a b ", Utils.trimStart("\n\t\r a b "));
	}


	@Test
	public void testToArray()
	{
		expectThat(Utils.toArray("a")).elems("a");
	}


	@Test
	public void testStringCollToStringArray()
	{
		expectThat(Utils.toArray((Collection<String>)null)).empty();
		expectThat(Utils.toArray(List.of())).empty();
		expectThat(Utils.toArray(List.of("a", "b"))).elems("a", "b");
	}


	@Test
	public void testCollToArray()
	{
		expectThat(Utils.toArray(null, Integer.class)).isA(Integer[].class).empty();
		expectThat(Utils.toArray(List.of(), Integer.class)).isA(Integer[].class).empty();
		expectThat(Utils.toArray(List.of(1, 2), Integer.class)).elems(1, 2);
	}
}

