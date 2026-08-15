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
package jd.commons.io.fluent;


import static deepdive.ExpectStatic.*;
import static deepdive.ExpectThat.*;
import static java.io.Writer.*;
import static jd.commons.io.fluent.IO.*;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import jd.commons.io.lib.OpenWriter;
import jd.commons.util.Holder;
import jd.commons.util.UncheckedException;


public class CharWriteToTest
{
	private static final String AUML = "\u00E4";
	@Test
	public void testCountChars() throws Exception
	{
		CharSource cs = Chars.fromString(AUML);
		expectEqual(1, cs.write().countChars().asUtf8().toNull());
		expectEqual(1, cs.write().countChars().toNull()); // coverage for Writer
		expectEqual(1, cs.write().countChars().to(Chars.toNull())); // coverage for CharTarget
		expectEqual(2, cs.write().asUtf8().countBytes().toNull());
		expectEqual(2, cs.write().countChars().asUtf8().countBytes().toNull());
	}


	@Test
	public void testSilent() throws Exception
	{
		expectEqual("abc", Chars.fromString("abc").write().silent().toStr());
		IOException e = new IOException("hello");
		Holder<Exception> log = new Holder<>();
		expectSame(e, Chars.fromError(e).write().silent(log).to(Chars.toNull()));
		expectSame(e, log.get());
	}


	@Test
	public void testThrowing() throws Exception
	{
		IOException e = new IOException("a");
		expectError(()-> Chars.fromError(e).write().throwing(IllegalStateException::new).toNull())
			.isA(IllegalStateException.class)
			.cause().same(e);
	}


	@Test
	public void testToAppendable() throws Exception
	{
		StringBuilder sb = new StringBuilder();
		Chars.fromString("a").write().to(sb);
		expectEqual("a", sb.toString());
	}


	@Test
	public void testToError() throws Exception
	{
		IOException e = new IOException("x");
		Exception f = Chars.fromString("a").write().silent().toError(e);
		expectSame(e, f);
	}


	@SuppressWarnings("deprecation")
	@Test
	public void testToStr() throws Exception
	{
		CharWriteTo<Void,IOException> cw = Chars.fromLines("a").write();
		String expected = "a" + System.lineSeparator();
		expectEqual(expected, cw.toStr());
		not().expectEqual(expected, cw.toString());
	}


	@Test
	public void testUnckecked() throws Exception
	{
		// no exception thrown
		expectNull(Chars.fromString("abc").write().unchecked().to(nullWriter()));

		// exception thrown
		IOException ioe = new IOException("hallo");
		expectError(() -> Chars.fromError(ioe).write().unchecked().to(nullWriter()))
			.isA(UncheckedException.class)
			.message("java.io.IOException: hallo")
			.cause()
				.same(ioe);
	}


	@Test
	public void testWrap() throws Exception
	{
		expectNull(Chars.fromString("a").write().wrap(OpenWriter::new).toNull());
		expectNull(Chars.fromString("a").write().wrap(OpenWriter::new).to(Chars.toNull()));
	}
}
