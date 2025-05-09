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


import static jd.commons.util.GetString.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.net.MalformedURLException;
import java.nio.file.AccessMode;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;


public class GetStringTest
{
	@Test 
	public void testAsBoolean()
	{
		assertTrue(of("true").asBoolean());
		assertFalse(of("false").asBoolean());
		assertTrue(of("true").asBooleanOr(false));
		assertFalse(of(null).asBooleanOr(false));
		assertIAE(() -> of("a").asBoolean(), "\"a\" can't be converted to boolean (true/false)");
	}

	
	@Test 
	public void testAsBooleanTF()
	{
		assertTrue(of("yes").asBoolean("yes", "no"));
		assertFalse(of("no").asBoolean("yes", "no"));
		assertIAE(() -> of("a", "test").asBoolean("yes", "no"), "test \"a\" can't be converted to boolean (yes/no)");
	}


	@Test public void testAsByte()
	{
		assertEquals((byte)100, of("100").asByte());
		assertIAE(() -> of("a", "test").asByte(), "test \"a\" can't be converted to byte");
	}

	
	@Test public void testAsChar()
	{
		assertEquals('a', of("a").asChar());
		assertIAE(() -> of("ab").asChar(), "\"ab\" can't be converted to char");
	}


	@Test public void testAsClass()
	{
		assertNull(of(null).asClass(CharSequence.class));
		assertSame(String.class, of("java.lang.String", "type").asClass(CharSequence.class));
		assertIAE(() -> of("java.xxx", "type").asClass(CharSequence.class), "type \"java.xxx\" can't be converted to Class");
	}


	@Test public void testAsDouble()
	{
		assertEquals(123.4, of("123.4").asDouble(), 0.0);
		assertEquals(123.4, of(null).asDoubleOr(123.4), 0.0);
		assertEquals(123.4, of("123.4").asDoubleOr(5), 0.0);
		assertIAE(() -> of("a", "test").asDouble(), "test \"a\" can't be converted to double");
	}


	@Test public void testAsFloat()
	{
		assertEquals(123.4f, of("123.4").asFloat(), 0.0);
		assertIAE(() -> of("a", "test").asFloat(), "test \"a\" can't be converted to float");
	}


	@Test public void testAsEnum()
	{
		assertNull(of(null).asEnum(AccessMode.class));
		assertSame(AccessMode.READ, of(AccessMode.READ.name()).asEnum(AccessMode.class));
		assertIAE(() -> of("xyz").asEnum(AccessMode.class), "No enum constant java.nio.file.AccessMode.xyz");
	}

	
	@Test public void testAsFile()
	{
		assertEquals("123.txt", of("123.txt").asFile().getName());
		assertNull(of(null).asFile());
	}

	
	@Test public void testAsInt()
	{
		assertEquals(123, of("123").asInt());
		assertEquals(123, of("123").asIntOr(1));
		assertEquals(1, of(null).asIntOr(1));
		assertIAE(() -> of("a").asInt(), "\"a\" can't be converted to int");
	}


	@Test public void testAsList()
	{
		assertThat(of(null).asSplit(",")).isEmpty();
		assertThat(of("a,b").asSplit(",")).containsExactly("a", "b");
	}

	
	@Test public void testAsLong()
	{
		assertEquals(1234L, of("1234").asLong());
		assertEquals(1234L, of("1234").asLongOr(1));
		assertEquals(1L, of(null).asLongOr(1));
		assertIAE(() -> of("a").asLong(), "\"a\" can't be converted to long");
	}
	
	
	@Test public void testAsResult()
	{
		assertEquals((short)123, of("123").asResult(Short::parseShort).shortValue());
		assertIAE(() -> of("a").asResult(Short::parseShort), "\"a\" can't be converted");
	}

	
	@Test 
	public void testProps()
	{
		GetString a = of("a");
		assertFalse(a.isNull());
		assertFalse(a.isEmpty());
		assertEquals("a", a.value());
		assertEquals("a", a.valueOr("b"));
		assertSame(a, a.notNull());
		assertSame(a, a.notEmpty());
		assertSame(a, a.replaceNull("x"));

		GetString n = of(null);
		assertTrue(n.isNull());
		assertTrue(n.isEmpty());
		assertEquals("b", n.valueOr("b"));
		assertIAE(() -> n.notNull(), "null not allowed");
		assertIAE(() -> n.notEmpty(), "null not allowed");
		assertIAE(() -> of("").notEmpty(), "\"\" not allowed");
		assertEquals("x", n.replaceNull("x").value());
	}
	
	
	@Test
	public void testHelperDisplay()
	{
		assertEquals("null", Helper.toDisplay(null));
		assertEquals("\"a\"", Helper.toDisplay("a"));
		assertEquals("\"a\\t\\b\\f\\r\\n\\\"\\\\\"", Helper.toDisplay("a\t\b\f\r\n\"\\"));
		String s = Utils.repeat('.', 60);
		assertEquals('"' + Utils.repeat('.', 50) + '"' + "+<10 more>", Helper.toDisplay(s));
	}
	
	
	@Test
	public void testSimple()
	{
		assertEquals("null", of(null).toString());
		assertNull(of("a").what());
		assertEquals("some", of("a", "some").what());
	}

	
	@Test public void testAsURI()
	{
		assertEquals("a", of("a").asURI().toString());
		assertNull(of(null).asURI());
	}

	
	@Test public void testAsURL()
	{
		assertEquals("http://a.com", of("http://a.com").asURL().toString());
		assertNull(of(null).asURL());
		assertThatThrownBy(() -> of("a").asURL())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("no protocol: a")
			.cause().isInstanceOf(MalformedURLException.class);
	}
	
	
	private static void assertIAE(ThrowingCallable callable, String msg)
	{
		assertThatThrownBy(callable).isInstanceOf(IllegalArgumentException.class).hasMessage(msg);
	}
}
