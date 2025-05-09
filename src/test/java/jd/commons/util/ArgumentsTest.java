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


import static jd.commons.io.fluent.IO.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


public class ArgumentsTest
{
	@Test public void testCreate()
	{
		Arguments args;
			
		args = new Arguments((String[])null);
		assertEquals(0, args.size());
			
		args = new Arguments("", "a", null);
		assertEquals(1, args.size());

		args = new Arguments(Arrays.asList("", "a", null));
		assertEquals(1, args.size());
	}


	@Test 
	public void testConsume()
	{
		Arguments args = new Arguments("a");
		assertFalse(args.consume("b"));
		assertTrue(args.consume("a"));
	}
	

	@Test 
	public void testConsumeAny()
	{
		Arguments args = new Arguments("a");
		assertFalse(args.consumeAny("b"));
		assertTrue(args.consumeAny("b", "a"));
	}
	
	 
	@Test 
	public void testGetters()
	{
		Arguments args = new Arguments("x");
		
		assertTrue(args.hasMore());
		assertTrue(args.hasMore(1));
		assertFalse(args.hasMore(2));
		assertEquals(1, args.size());
		assertEquals(0, args.index());
		assertThat(args.getAll()).containsExactly("x");
		
		List<String> remaining = args.getRemaining();
		assertThat(remaining).containsExactly("x");
		
		assertEquals("x", args.get());
		assertTrue(args.replace("a"));
		assertEquals("a", args.get());
		assertEquals("a", args.next().value());
		assertEquals(1, args.index());
		
		assertFalse(args.hasMore());
		assertFalse(args.replace("!"));
		assertNull(args.get());

		assertIAE(() -> args.next(), "arg expected");
		assertIAE(() -> args.next("count"), "count arg expected");
	}


	@Test 
	public void testIncludes(@TempDir File dir) throws Exception
	{
		File includeFile = new File(dir, "inc.txt");
		Chars.fromString("-i1 -i2").write().asUtf8().to(includeFile);
		String includeArg = '@' + includeFile.toString();
		
		Arguments args = new Arguments("one", includeArg, "two");
		assertEquals(3, args.size());
		
		args.resolveIncludes();
		args.consume("one");
		args.consume("-i1");
		args.consume("-i2");
		args.consume("two");
		assertFalse(args.hasMore());
		
		Chars.fromString(includeArg).write().asUtf8().to(includeFile);
		
		assertThatThrownBy(() -> new Arguments("one", includeArg, "two").resolveIncludes())
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("circular inclusion of file " + includeFile);
	}


	@Test 
	public void testNext()
	{
		Arguments args = new Arguments("true", "false", "a");
		assertTrue(args.next().asBoolean());
		assertFalse(args.next().asBoolean());
		assertIAE(() -> args.next("flag").asBoolean(), "flag \"a\" can't be converted to boolean (true/false)");
	}
	
	
	@Test public void testNextMatches()
	{
		Pattern aPattern = Pattern.compile("a.*");
		Arguments args = new Arguments("abc");
		assertTrue(args.nextMatches(aPattern));
		assertTrue(args.nextMatches(s -> s.contains("b")));
		assertTrue(args.nextStartsWith("a"));

		args = new Arguments("xyz");
		assertFalse(args.nextMatches(aPattern));
		assertFalse(args.nextMatches(s -> s.contains("b")));
		assertFalse(args.nextStartsWith("a"));
	}
	
	
	@Test public void testNextStartsWith()
	{
		Arguments args = new Arguments("abc");
		assertTrue(args.nextStartsWith("a"));
		assertFalse(args.nextStartsWith("x"));
	}
	
	
	private static void assertIAE(ThrowingCallable callable, String msg)
	{
		assertThatThrownBy(callable).isInstanceOf(IllegalArgumentException.class).hasMessage(msg);
	}
}
