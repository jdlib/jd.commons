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
import static jd.commons.io.fluent.IO.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import deepdive.function.CheckedRunnable;


public class ArgumentsTest
{
	@Test public void testCreate()
	{
		Arguments args;

		args = new Arguments((String[])null);
		expectEqual(0, args.size());

		args = new Arguments("", "a", null);
		expectEqual(1, args.size());

		args = new Arguments(Arrays.asList("", "a", null));
		expectEqual(1, args.size());
	}


	@Test
	public void testConsume()
	{
		Arguments args = new Arguments("a");
		expectFalse(args.consume("b"));
		expectTrue(args.consume("a"));
	}


	@Test
	public void testConsumeAny()
	{
		Arguments args = new Arguments("a");
		expectFalse(args.consumeAny("b"));
		expectTrue(args.consumeAny("b", "a"));
	}


	@Test
	public void testGetters()
	{
		Arguments args = new Arguments("x");

		expectTrue(args.hasMore());
		expectTrue(args.hasMore(1));
		expectFalse(args.hasMore(2));
		expectEqual(1, args.size());
		expectEqual(0, args.index());
		expectThat(args.getAll()).elems("x");

		List<String> remaining = args.getRemaining();
		expectThat(remaining).elems("x");

		expectEqual("x", args.get());
		expectTrue(args.replace("a"));
		expectEqual("a", args.get());
		expectEqual("a", args.next().value());
		expectEqual(1, args.index());

		expectFalse(args.hasMore());
		expectFalse(args.replace("!"));
		expectNull(args.get());

		expectIAE(() -> args.next(), "arg expected");
		expectIAE(() -> args.next("count"), "count arg expected");
	}


	@Test
	public void testIncludes(@TempDir File dir) throws Exception
	{
		File includeFile = new File(dir, "inc.txt");
		Chars.fromString("-i1 -i2").write().asUtf8().to(includeFile);
		String includeArg = '@' + includeFile.toString();

		Arguments args = new Arguments("one", includeArg, "two");
		expectEqual(3, args.size());

		args.resolveIncludes();
		args.consume("one");
		args.consume("-i1");
		args.consume("-i2");
		args.consume("two");
		expectFalse(args.hasMore());

		Chars.fromString(includeArg).write().asUtf8().to(includeFile);

		expectError(() -> new Arguments("one", includeArg, "two").resolveIncludes())
			.isA(IllegalArgumentException.class)
			.message("circular inclusion of file " + includeFile);
	}


	@Test
	public void testNext()
	{
		Arguments args = new Arguments("true", "false", "a");
		expectTrue(args.next().asBoolean());
		expectFalse(args.next().asBoolean());
		expectIAE(() -> args.next("flag").asBoolean(), "flag \"a\" can't be converted to boolean (true/false)");
	}


	@Test public void testNextMatches()
	{
		Pattern aPattern = Pattern.compile("a.*");
		Arguments args = new Arguments("abc");
		expectTrue(args.nextMatches(aPattern));
		expectTrue(args.nextMatches(s -> s.contains("b")));
		expectTrue(args.nextStartsWith("a"));

		args = new Arguments("xyz");
		expectFalse(args.nextMatches(aPattern));
		expectFalse(args.nextMatches(s -> s.contains("b")));
		expectFalse(args.nextStartsWith("a"));
	}


	@Test public void testNextStartsWith()
	{
		Arguments args = new Arguments("abc");
		expectTrue(args.nextStartsWith("a"));
		expectFalse(args.nextStartsWith("x"));
	}


	private static void expectIAE(CheckedRunnable<?> runnable, String msg)
	{
		expectError(runnable).isA(IllegalArgumentException.class).message(msg);
	}
}
