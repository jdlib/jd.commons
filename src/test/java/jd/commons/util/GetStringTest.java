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
import static jd.commons.util.GetString.*;
import java.net.MalformedURLException;
import java.nio.file.AccessMode;
import org.junit.jupiter.api.Test;
import deepdive.function.CheckedRunnable;


public class GetStringTest
{
	@Test
	public void testAsBoolean()
	{
		expectTrue(of("true").asBoolean());
		expectFalse(of("false").asBoolean());
		expectTrue(of("true").asBooleanOr(false));
		expectFalse(of(null).asBooleanOr(false));
		expectIAE(() -> of("a").asBoolean(), "\"a\" can't be converted to boolean (true/false)");
	}


	@Test
	public void testAsBooleanYesNo()
	{
		expectTrue(of("yes").asBoolean("yes", "no"));
		expectFalse(of("no").asBoolean("yes", "no"));
		expectIAE(() -> of("a", "test").asBoolean("yes", "no"), "test \"a\" can't be converted to boolean (yes/no)");
	}


	@Test public void testAsByte()
	{
		expectEqual((byte)100, of("100").asByte());
		expectIAE(() -> of("a", "test").asByte(), "test \"a\" can't be converted to byte");
	}


	@Test public void testAsChar()
	{
		expectEqual('a', of("a").asChar());
		expectIAE(() -> of("ab").asChar(), "\"ab\" can't be converted to char");
	}


	@Test public void testAsClass()
	{
		expectNull(of(null).asClass(CharSequence.class));
		expectSame(String.class, of("java.lang.String", "type").asClass(CharSequence.class));
		expectIAE(() -> of("java.xxx", "type").asClass(CharSequence.class), "type \"java.xxx\" can't be converted to Class");
	}


	@Test public void testAsDouble()
	{
		expectEqual(123.4, of("123.4").asDouble(), 0.0);
		expectEqual(123.4, of(null).asDoubleOr(123.4), 0.0);
		expectEqual(123.4, of("123.4").asDoubleOr(5), 0.0);
		expectIAE(() -> of("a", "test").asDouble(), "test \"a\" can't be converted to double");
	}


	@Test public void testAsFloat()
	{
		expectEqual(123.4f, of("123.4").asFloat(), 0.0);
		expectIAE(() -> of("a", "test").asFloat(), "test \"a\" can't be converted to float");
	}


	@Test public void testAsEnum()
	{
		expectNull(of(null).asEnum(AccessMode.class));
		expectSame(AccessMode.READ, of(AccessMode.READ.name()).asEnum(AccessMode.class));
		expectIAE(() -> of("xyz").asEnum(AccessMode.class), "No enum constant java.nio.file.AccessMode.xyz");
	}


	@Test public void testAsFile()
	{
		expectEqual("123.txt", of("123.txt").asFile().getName());
		expectNull(of(null).asFile());
	}


	@Test public void testAsInt()
	{
		expectEqual(123, of("123").asInt());
		expectEqual(123, of("123").asIntOr(1));
		expectEqual(1, of(null).asIntOr(1));
		expectIAE(() -> of("a").asInt(), "\"a\" can't be converted to int");
	}


	@Test public void testAsList()
	{
		expectThat(of(null).asSplit(",")).empty();
		expectThat(of("a,b").asSplit(",")).elems("a", "b");
	}


	@Test public void testAsLong()
	{
		expectEqual(1234L, of("1234").asLong());
		expectEqual(1234L, of("1234").asLongOr(1));
		expectEqual(1L, of(null).asLongOr(1));
		expectIAE(() -> of("a").asLong(), "\"a\" can't be converted to long");
	}


	@Test public void testAsResult()
	{
		expectEqual((short)123, of("123").asResult(Short::parseShort).shortValue());
		expectIAE(() -> of("a").asResult(Short::parseShort), "\"a\" can't be converted");
	}


	@Test
	public void testProps()
	{
		GetString a = of("a");
		expectFalse(a.isNull());
		expectFalse(a.isEmpty());
		expectEqual("a", a.value());
		expectEqual("a", a.valueOr("b"));
		expectSame(a, a.notNull());
		expectSame(a, a.notEmpty());
		expectSame(a, a.replaceNull("x"));

		GetString n = of(null);
		expectTrue(n.isNull());
		expectTrue(n.isEmpty());
		expectEqual("b", n.valueOr("b"));
		expectEqual("x", n.replaceNull("x").value());
		expectIAE(() -> n.notNull(), "value is null");
		expectIAE(() -> n.notEmpty(), "value is null");

		GetString nw = of(null, "what");
		expectIAE(() -> nw.notNull(), "what is null");

		GetString e = of("");
		expectFalse(e.isNull());
		expectTrue(e.isEmpty());
		expectIAE(() -> e.notEmpty(), "value is empty");

		GetString ew = of("", "what");
		expectIAE(() -> ew.notEmpty(), "what is empty");
	}


	@Test
	public void testHelperDisplay()
	{
		expectEqual("null", Helper.toDisplay(null));
		expectEqual("\"a\"", Helper.toDisplay("a"));
		expectEqual("\"a\\t\\b\\f\\r\\n\\\"\\\\\"", Helper.toDisplay("a\t\b\f\r\n\"\\"));
		String s = Utils.repeat('.', 60);
		expectEqual('"' + Utils.repeat('.', 50) + '"' + "+<10 more>", Helper.toDisplay(s));
	}


	@Test
	public void testSimple()
	{
		expectEqual("null", of(null).toString());
		expectNull(of("a").what());
		expectEqual("some", of("a", "some").what());
	}


	@Test public void testAsURI()
	{
		expectEqual("a", of("a").asURI().toString());
		expectNull(of(null).asURI());
	}


	@Test public void testAsURL()
	{
		expectEqual("http://a.com", of("http://a.com").asURL().toString());
		expectNull(of(null).asURL());
		expectError(() -> of("a").asURL())
			.isA(IllegalArgumentException.class)
			.message("no protocol: a")
			.cause().isA(MalformedURLException.class);
	}


	private static void expectIAE(CheckedRunnable<?> runner, String msg)
	{
		expectError(runner).isA(IllegalArgumentException.class).message(msg);
	}
}
