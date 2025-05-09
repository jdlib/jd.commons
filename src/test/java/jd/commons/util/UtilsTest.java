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


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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

		assertNull(Utils.addFirst(null));
		assertSame(array, Utils.addFirst(array));
		assertSame(array, Utils.addFirst(array, (String[])null));
		assertSame(newItems, Utils.addFirst(null, newItems));
		
		String[] result = Utils.addFirst(array, newItems);
		assertThat(result).containsExactly("1", "2", "a", "b", "c");
	}


	@Test
	public void testAddLast() 
	{
		String[] array = {"a", "b", "c"};
		String[] newItems = {"1", "2"};

		assertNull(Utils.addLast(null));
		assertSame(array, Utils.addLast(array));
		assertSame(array, Utils.addLast(array, (String[])null));
		assertSame(newItems, Utils.addLast(null, newItems));
		
		String[] result = Utils.addLast(array, newItems);
		assertThat(result).containsExactly("a", "b", "c", "1", "2");
	}


	@Test 
	public void testAfterOr()
	{
		assertNull(Utils.afterOr(null, '=', null));
		assertNull(Utils.afterOr("123", '=', null));
		assertEquals("", 		Utils.afterOr(null, '=', ""));
		assertEquals("123", 	Utils.afterOr("abc=123", '=', null));
		assertEquals("123=xyz", Utils.afterOr("abc=123=xyz", '=', null));
		assertEquals("", 		Utils.afterOr("abc=", '=', null));
	}


	@Test 
	public void testAfterLastOr()
	{
		assertNull(Utils.afterLastOr(null, '=', null));
		assertNull(Utils.afterLastOr("123", '=', null));
		assertEquals("3", 	Utils.afterLastOr("1.2.3", '.', null));
		assertEquals("", 	Utils.afterLastOr("1.2.", '.', null));
	}

	
	@Test 
	public void testBeforeOr()
	{
		assertNull(Utils.beforeOr(null, '=', null));
		assertEquals("abc", Utils.beforeOr("abc", '=', "abc"));
		assertEquals("abc", Utils.beforeOr("abc=123", '=', ""));
		assertEquals("", 	Utils.beforeOr("=123", '=', null));
		assertEquals("", 	Utils.beforeOr(null, '=', ""));
	}

	
	@Test 
	public void testBeforeLastOr()
	{
		assertNull(Utils.beforeLastOr(null, '=', null));
		assertEquals("abc", Utils.beforeLastOr("abc", '=', "abc"));
		assertEquals("abc", Utils.beforeLastOr("abc=123", '=', ""));
		assertEquals("", 	Utils.beforeLastOr("=123", '=', null));
		assertEquals("1.2", Utils.beforeLastOr("1.2.3", '.', null));
		assertEquals("", 	Utils.beforeLastOr(null, '=', ""));
	}

	
	@Test 
	public void testCut()
	{
		assertNull(Utils.cutStart(null, "x"));
		assertEquals("",	Utils.cutStart("", "x"));
		assertEquals("",	Utils.cutStart("x", "x"));
		assertEquals("ax",	Utils.cutStart("ax", "x"));

		assertNull(Utils.cutEnd(null, "x"));
		assertEquals("",	Utils.cutEnd("", "x"));
		assertEquals("",	Utils.cutEnd("x", "x"));
		assertEquals("xa",	Utils.cutEnd("xa", "x"));
	}
	
	
	@Test
	public void testEnumOf()
	{
		assertNull(Utils.enumOf(AccessMode.class, null));
		assertSame(AccessMode.READ, Utils.enumOf(AccessMode.class, null, AccessMode.READ));
		assertSame(AccessMode.READ, Utils.enumOf(AccessMode.class, "xy", AccessMode.READ));
		assertSame(AccessMode.WRITE, Utils.enumOf(AccessMode.class, "WRITE", AccessMode.READ));
	}
	
	
	@Test
	public void testIndexOf()
	{
		assertEquals(-1, Utils.indexOf("a", (String[])null));
		assertEquals(-1, Utils.indexOf("a"));
		assertEquals(-1, Utils.indexOf("a", "b"));
		assertEquals(1, Utils.indexOf("a", "b", "a", "c"));
	}

	
	@Test
	public void testIsA() throws Exception
	{
		assertFalse(Utils.isA(null, null));
		assertFalse(Utils.isA(null, CharSequence.class));
		assertFalse(Utils.isA("a", null));
		assertFalse(Utils.isA("a", Integer.class));
		assertTrue(Utils.isA("a", CharSequence.class));
		
		// test annotations
		Annotation testAnno = getClass().getMethod("testIsA").getAnnotations()[0];
		assertTrue(Utils.isA(testAnno, Test.class));
		assertFalse(Utils.isA(testAnno, SuppressWarnings.class));
		assertFalse(Utils.isA(testAnno, null));
		assertFalse(Utils.isA(null, Test.class));
	}
	

	@Test
	public void testIsBlank() throws Exception
	{
		assertTrue(Utils.isBlank(null));
		assertTrue(Utils.isBlank(""));
		assertTrue(Utils.isBlank(" \t\r\n"));
		assertFalse(Utils.isBlank(" \t\r\na "));
		assertFalse(Utils.isBlank("a "));
	}
	
	
	@Test
	public void testIsEmpty()
	{
		assertTrue(Utils.isEmpty((CharSequence)null));
		assertTrue(Utils.isEmpty(""));
		assertFalse(Utils.isEmpty("a"));

		assertTrue(Utils.isEmpty((Object[])null));
		assertTrue(Utils.isEmpty(new Object[0]));
		assertFalse(Utils.isEmpty(new String[] { "a" }));

		assertTrue(Utils.isEmpty((Collection<?>)null));
		assertTrue(Utils.isEmpty(List.of()));
		assertFalse(Utils.isEmpty(List.of("a")));

		assertTrue(Utils.isEmpty((Map<?,?>)null));
		assertTrue(Utils.isEmpty(Map.of()));
		assertFalse(Utils.isEmpty(Map.of("a", "1")));
	}
	
	
	@Test 
	public void testHave()
	{
		assertEquals("x",	Utils.haveStart(null, "x"));
		assertEquals("x",	Utils.haveStart("", "x"));
		assertEquals("x",	Utils.haveStart("x", "x"));
		assertEquals("xa",	Utils.haveStart("a", "x"));

		assertEquals("x",	Utils.haveEnd(null, "x"));
		assertEquals("x",	Utils.haveEnd("", "x"));
		assertEquals("x",	Utils.haveEnd("x", "x"));
		assertEquals("ax",	Utils.haveEnd("a", "x"));
	}


	@Test 
	public void testNewArray()
	{
		String[] s5 = Utils.newArray(String.class, 5);
		assertEquals(5, s5.length);

		String[] s6 = Utils.newArray(s5, 6);
		assertEquals(6, s6.length);
	}

	
	@Test 
	public void testNewHashSet()
	{
		assertThat(Utils.newHashSet("a", "b")).containsExactlyInAnyOrder("a", "b");
	}
	

	@Test 
	public void testNorm()
	{
		assertNull(Utils.norm(null));
		assertNull(Utils.norm(""));
		assertNull(Utils.norm(" "));
		assertEquals("a", Utils.norm(" a "));
	}
	
	
	@Test
	public void testNotNull()
	{
		assertSame("a", Utils.notNull("a"));
		assertSame("", Utils.notNull(null));
	}
	
	
	@Test
	public void testPackageName() 
	{
		assertEquals("jd.commons.util", Utils.packageName(Utils.class));
		assertEquals("jd.commons.util", Utils.packageName("jd.commons.util.Utils"));
		assertEquals("", Utils.packageName("Utils"));
		assertEquals("", Utils.packageName(""));
	}
	
	
	@Test 
	public void testPad()
	{
		assertEquals("a..", Utils.padEnd("a", 3, '.'));
		assertEquals("a.",  Utils.padEnd("a", 2, '.'));
		assertEquals("a  ", Utils.padEnd("a", 3));
		assertEquals("100", Utils.padEnd(1, 3));
		assertEquals("123", Utils.padEnd(123, 2));

		assertEquals("..a", Utils.padStart("a", 3, '.'));
		assertEquals(".a",  Utils.padStart("a", 2, '.'));
		assertEquals("  a", Utils.padStart("a", 3));
		assertEquals("001", Utils.padStart(1, 3));
		assertEquals("123", Utils.padStart(123, 2));
		assertEquals("!1",  Utils.padStart(1, 2, '!'));
	}
	
	
	@Test 
	public void testRepeat()
	{
		StringBuilder sb = new StringBuilder();
		assertSame(sb, Utils.repeat('0', 3, sb));
		assertEquals("000", sb.toString());
		
		assertEquals("aa", Utils.repeat('a', 2));
		assertEquals("", Utils.repeat('a', -15));
	}
	
	
	@Test 
	public void testStartCase()
	{
		assertNull(Utils.startLowerCase(null));
		assertEquals("", 	Utils.startLowerCase(""));
		assertEquals("abc", Utils.startLowerCase("abc"));
		assertEquals("aBC", Utils.startLowerCase("ABC"));

		assertNull(Utils.startUpperCase(null));
		assertEquals("", 	Utils.startUpperCase(""));
		assertEquals("Abc", Utils.startUpperCase("Abc"));
		assertEquals("ABC", Utils.startUpperCase("aBC"));
	}

	
	@Test 
	public void testTrimEnd()
	{
		assertNull(Utils.trimEnd(null));
		assertSame("a", Utils.trimEnd("a"));
		assertEquals(" a b", Utils.trimEnd(" a b \t\r\n"));
	}

	
	@Test 
	public void testTrimStart()
	{
		assertNull(Utils.trimStart(null));
		assertSame("a", Utils.trimStart("a"));
		assertEquals("a b ", Utils.trimStart("\n\t\r a b "));
	}


	@Test
	public void testToArray()
	{
		assertThat(Utils.toArray("a")).containsExactly("a");
	}


	@Test
	public void testStringCollToStringArray()
	{
		assertThat(Utils.toArray((Collection<String>)null)).isEmpty();
		assertThat(Utils.toArray(List.of())).isEmpty();
		assertThat(Utils.toArray(List.of("a", "b"))).containsExactly("a", "b");
	}


	@Test
	public void testCollToArray()
	{
		assertThat(Utils.toArray(null, Integer.class)).isInstanceOf(Integer[].class).isEmpty();
		assertThat(Utils.toArray(List.of(), Integer.class)).isInstanceOf(Integer[].class).isEmpty();
		assertThat(Utils.toArray(List.of(1, 2), Integer.class)).containsExactly(1, 2);
	}
}

